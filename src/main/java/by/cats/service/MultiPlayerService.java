package by.cats.service;


import by.cats.dto.response.MultiplayerShotResponse;
import by.cats.dto.response.ShotResult;
import by.cats.exception.DoubleShotException;
import by.cats.exception.LobbyIsAlreadyOccupied;
import by.cats.exception.LobbyNotFoundException;
import by.cats.model.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@RequiredArgsConstructor
@Service
public class MultiPlayerService {
    private final ConcurrentMap<String, MultiPlayerSession> games = new ConcurrentHashMap<>();

    private final ShipPlacementService shipPlacementService;
    private final GameEngine engine;
    private final ShipPlacementValidator validator;

    private SimpMessagingTemplate messagingTemplate;

    public String createLobby(String hostName) {
        String lobbyId = UUID.randomUUID().toString();
        var game = MultiPlayerSession.builder()
                .lobbyCode(lobbyId)
                .hostNickname(hostName)
                .lobbyStatus(LobbyStatus.WAITING)
                .lastActivity(LocalDateTime.now())
                .isHostTurn(true)
                .guestBoard(new Board())
                .guestBoard(new Board())
                .build();

        games.put(lobbyId, game);

        return lobbyId;
    }

    public void joinLobby(String lobbyCode, String guestNickname) {
        var lobby = games.get(lobbyCode);
        if (lobby == null)
            throw new LobbyNotFoundException("Лобби с id " + lobbyCode + " не существует");

        if (lobby.getGuestNickname() != null)
            throw new LobbyIsAlreadyOccupied("Лобби уже занято");

        lobby.setGuestNickname(guestNickname);
        lobby.setLobbyStatus(LobbyStatus.PLACING);
    }

    public void setBoard(String lobbyId, String nickname, Board board) {
        var lobby = games.get(lobbyId);
        if (lobby.getHostNickname().equals(nickname)) {
            lobby.setHostBoard(board);
            lobby.setHostReady(true);
        }
        else {
            lobby.setGuestBoard(board);
            lobby.setGuestReady(true);
        }
        lobby.setLastActivity(LocalDateTime.now());
    }

    @Scheduled(fixedDelay = 5000)
    public void cleanupTimedOutLobbies() {
        LocalDateTime time = LocalDateTime.now();
        games.forEach((code, session) -> {
            boolean oneIsWaiting = (session.isHostReady() && !session.isGuestReady()) ||
                    (!session.isHostReady() && session.isGuestReady());

            if (session.getLobbyStatus() == LobbyStatus.PLACING && oneIsWaiting) {
                if (session.getLastActivity().plusSeconds(60).isBefore(time)) {

                    notifyPlayersAboutTimeout(session, Map.of("type", "ERROR", "message", "Lobby timeout"));

                    games.remove(code);

                    System.out.println("Лобби " + code + " удалено по таймауту");
                }
            }
        });
    }

    public void notifyPlayersAboutTimeout(MultiPlayerSession session, Object message) {
        messagingTemplate.convertAndSend("/topic/lobby/" + session.getLobbyCode(), message);
    }

    public MultiPlayerSession getLobby(String lobbyId) {
        return games.get(lobbyId);
    }

    public MultiPlayerSession removeLobby(String lobbyId) {
        return games.remove(lobbyId);
    }


    public void setPlayerReady(String lobbyCode, String nickname) {
        MultiPlayerSession session = games.get(lobbyCode);
        if (session == null) return;

        // Определяем, кто прислал сигнал
        if (session.getHostNickname().equals(nickname)) {
            session.setHostReady(true);
        } else if (session.getGuestNickname().equals(nickname)) {
            session.setGuestReady(true);
        }

        // Каждый раз, когда кто-то готов, обновляем время активности
        session.setLastActivity(LocalDateTime.now());

        // Уведомляем всех в лобби, что кто-то готов
        // Мы шлем DTO, чтобы фронтенд знал, кто именно готов
        messagingTemplate.convertAndSend("/topic/lobby/" + lobbyCode,
                Map.of(
                        "type", "PLAYER_READY",
                        "nickname", nickname,
                        "hostReady", session.isHostReady(),
                        "guestReady", session.isGuestReady()
                )
        );

        // Если оба готовы — переводим игру в фазу боя
        if (session.isHostReady() && session.isGuestReady()) {
            session.setLobbyStatus(LobbyStatus.BATTLE);

            // Уведомляем фронтенд, что пора переходить к стрельбе
            messagingTemplate.convertAndSend("/topic/lobby/" + lobbyCode,
                    Map.of("type", "GAME_STARTED")
            );
        }
    }

    public void handleShot(String lobbyCode, String shooterNickname, Point p) {
        int x = p.getX();
        int y = p.getY();
        MultiPlayerSession session = games.get(lobbyCode);

        // 1. Базовые проверки
        if (session == null || session.getLobbyStatus() != LobbyStatus.BATTLE) {
            return; // Игра не в той фазе или не существует
        }

        // 2. Проверка: а ваш ли сейчас ход?
        boolean isHost = session.getHostNickname().equals(shooterNickname);
        if ((isHost && !session.isHostTurn()) || (!isHost && session.isHostTurn())) {
            // Можно отправить личное сообщение об ошибке, но обычно фронтенд просто блокирует кнопку
            return;
        }

        // 3. Определяем, чью доску атакуем
        Board targetBoard = isHost ? session.getGuestBoard() : session.getHostBoard();
        String opponentNickname = isHost ? session.getGuestNickname() : session.getHostNickname();

        try {
            // 4. Выполняем выстрел через наш GameEngine
            ShotResult shotResult = engine.applyShot(targetBoard, x, y);

            // 5. Логика передачи хода
            // Если промах - ход переходит оппоненту. Если попал - стреляешь снова.
            if (shotResult.getStatus().equals("MISS")) {
                session.setHostTurn(!session.isHostTurn());
            }

            // 6. Проверка на победу
            boolean isGameOver = !targetBoard.checkLiveShips();
            if (isGameOver) {
                session.setLobbyStatus(LobbyStatus.FINISH);
            }

            // 7. Формируем ответ для обоих игроков
            MultiplayerShotResponse response = MultiplayerShotResponse.builder()
                    .type(isGameOver ? "GAME_OVER" : "SHOT_RESULT")
                    .shooter(shooterNickname)
                    .shotResult(shotResult)
                    .nextTurn(session.isHostTurn() ? session.getHostNickname() : session.getGuestNickname())
                    .winner(isGameOver ? shooterNickname : null)
                    .build();

            // 8. Рассылаем результат ВСЕМ участникам лобби
            messagingTemplate.convertAndSend("/topic/lobby/" + lobbyCode, response);

            // Если игра окончена, можно удалить лобби из памяти через некоторое время
            if (isGameOver) {
                // games.remove(lobbyCode); // Или по таймеру
            }

        } catch (DoubleShotException e) {
            // Обработка повторного выстрела в ту же клетку
            messagingTemplate.convertAndSendToUser(shooterNickname, "/queue/errors", e.getMessage());
        }
    }

    public void setupShips(String lobbyCode, String nickname, List<Ship> ships) {
        MultiPlayerSession session = games.get(lobbyCode);
        if (session == null || session.getLobbyStatus() != LobbyStatus.PLACING) {
            throw new IllegalStateException("Сейчас нельзя расставлять корабли");
        }

        // 1. Валидация (используем тот же валидатор, что и для ботов!)
        // Это важно: нельзя доверять фронтенду, игрок может попытаться "сжульничать"
        if (!validator.validateAll(ships)) {
            // Шлем персональную ошибку через WebSocket
            messagingTemplate.convertAndSendToUser(nickname, "/topic/errors", "Неверная расстановка кораблей!");
            return;
        }

        // 2. Определяем, чью доску заполняем
        boolean isHost = session.getHostNickname().equals(nickname);
        Board board = isHost ? session.getHostBoard() : session.getGuestBoard();

        // 3. Заполняем доску (очистка + добавление кораблей + заполнение grid)
        board.clear();
        for (Ship ship : ships) {
            board.getShips().add(ship);
            for (Point p : ship.getCoordinates()) {
                board.setCellStatus(p.getX(), p.getY(), CellStatus.SHIP);
            }
        }

        // 4. Помечаем готовность
        if (isHost) session.setHostReady(true);
        else session.setGuestReady(true);

        // 5. Уведомляем всех в лобби
        messagingTemplate.convertAndSend("/topic/lobby/" + lobbyCode,
                Map.of("type", "PLAYER_READY", "nickname", nickname));

        // 6. Если оба готовы — начинаем битву
        if (session.isHostReady() && session.isGuestReady()) {
            session.setLobbyStatus(LobbyStatus.BATTLE);
            messagingTemplate.convertAndSend("/topic/lobby/" + lobbyCode,
                    Map.of("type", "GAME_STARTED", "nextTurn", session.getHostNickname()));
        }
    }
}
