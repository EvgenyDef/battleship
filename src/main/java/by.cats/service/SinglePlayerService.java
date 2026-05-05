package by.cats.service;


import by.cats.Model.*;
import by.cats.dto.response.GameTurnResponse;
import by.cats.dto.response.ShotResult;
import by.cats.exception.GameNotFoundException;
import by.cats.exception.ShipPlacementException;
import by.cats.service.bot.BotStrategy;
import by.cats.service.bot.EasyBotStrategy;
import by.cats.service.bot.HardBotStrategy;
import by.cats.service.bot.MediumBotStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class SinglePlayerService {

    private final ConcurrentMap<String, SinglePlayerSession> games = new ConcurrentHashMap<>();

    private final ShipPlacementService shipPlacementService;
    private final GameEngine gameEngine;
    private final ShipPlacementValidator validator;

    private final EasyBotStrategy easy;
    private final MediumBotStrategy medium;
    private final HardBotStrategy hard;

    @Autowired
    public SinglePlayerService(ShipPlacementService shipPlacementService,
                               GameEngine gameEngine,
                               ShipPlacementValidator validator,
                               EasyBotStrategy easy,
                               MediumBotStrategy medium,
                               HardBotStrategy hard) {
        this.shipPlacementService = shipPlacementService;
        this.gameEngine = gameEngine;
        this.validator = validator;
        this.easy = easy;
        this.medium = medium;
        this.hard = hard;
    }

    public String startNewGame(String nickname, BotDifficulty botDifficulty) {
        String sessionId = UUID.randomUUID().toString();
        SinglePlayerSession playerSession = new SinglePlayerSession(
                sessionId,
                nickname,
                botDifficulty
        );

        shipPlacementService.placeShipsRandomly(playerSession.getBotBoard());

        games.put(sessionId, playerSession);

        return sessionId;
    }

    // присвоить расстановку пользователя в games по sessionId + проверить расстановку через валидатор
    public void placePlayerShips(String sessionId, List<Ship> ships) {
        SinglePlayerSession session = games.get(sessionId);

        if (session == null) {
            throw new GameNotFoundException("Сессия не найдена");
        }

        boolean isValid = validator.validateAll(ships);

        if (isValid) {
            Board board = session.getPlayerBoard();

            board.clear();

            board.setShips(ships);

            for (Ship ship : ships) {
                for (Point p : ship.getCoordinates()) {
                    board.setCellStatus(p.getX(), p.getY(), CellStatus.SHIP);
                }
            }
        }
        else {
            throw new ShipPlacementException("Неверная расстановка кораблей");
        }
    }

    // метод для выстрела: принимает sessionId, Point point и смотрит, чья очередь стрелять
    public GameTurnResponse playTurn(String sessionId, Point point) {
        SinglePlayerSession session = games.get(sessionId);

        if (session == null) throw new GameNotFoundException("Игра не найдена");
        if (!session.isPlayerTurn()) throw new IllegalStateException("Сейчас не ваш ход");
        if (session.getGameStatus() != GameStatus.IN_PROGRESS) throw new IllegalStateException("Игра окончена");

        ShotResult playerResult = gameEngine.applyShot(session.getBotBoard(), point.getX(), point.getY());

        if (!session.getBotBoard().checkLiveShips()) {
            session.setGameStatus(GameStatus.PLAYER_WIN);
        }

        List<ShotResult> botResults = new ArrayList<>();

        if (playerResult.getStatus().equals("MISS") && session.getGameStatus() == GameStatus.IN_PROGRESS) {
            session.setPlayerTurn(false);

            while (!session.isPlayerTurn()) {
                BotStrategy strategy = getStrategy(session.getBotDifficulty());
                Point botTarget = strategy.getNextShot(session.getPlayerBoard());

                ShotResult botResult = gameEngine.applyShot(session.getPlayerBoard(), botTarget.getX(), botTarget.getY());
                botResults.add(botResult);

                if (botResult.getStatus().equals("MISS")) {
                    session.setPlayerTurn(true);
                }

                else if (!session.getPlayerBoard().checkLiveShips()) {
                    session.setGameStatus(GameStatus.BOT_WIN);
                    break;
                }
            }
        }

        return GameTurnResponse.builder()
                .playerShot(playerResult)
                .botShots(botResults)
                .isPlayerTurn(session.isPlayerTurn())
                .gameStatus(session.getGameStatus())
                .build();
    }

    private BotStrategy getStrategy(BotDifficulty difficulty) {
        return switch (difficulty) {
            case EASY -> easy;
            case MEDIUM -> medium;
            case HARD -> hard;
        };
    }

    public SinglePlayerSession getSession(String sessionId) {
        return games.get(sessionId);
    }
}
