package by.cats.Model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class SinglePlayerSession {
    private String sessionId;
    private String nickName;
    private Board playerBoard;
    private Board botBoard;
    private BotDifficulty botDifficulty;
    private boolean isPlayerTurn;
    private GameStatus gameStatus;
    private LocalDateTime lastActivity;

    public SinglePlayerSession(String sessionId, String nickName, BotDifficulty botDifficulty) {
        this.sessionId = sessionId;
        this.nickName = nickName;
        this.playerBoard = new Board();
        this.botBoard = new Board();
        this.botDifficulty = botDifficulty;
        this.isPlayerTurn = true;
        this.gameStatus = GameStatus.WAITING_FOR_SHIPS;
        this.lastActivity = LocalDateTime.now();
    }
}
