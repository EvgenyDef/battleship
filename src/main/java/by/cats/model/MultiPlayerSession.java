package by.cats.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MultiPlayerSession {
    private String lobbyCode;
    private String hostNickname;
    private String guestNickname;

    @Builder.Default
    private Board hostBoard = new Board();
    @Builder.Default
    private Board guestBoard = new Board();

    private boolean hostReady;
    private boolean guestReady;

    private boolean isHostTurn;
    private LobbyStatus lobbyStatus;

    @Builder.Default
    private LocalDateTime lastActivity = LocalDateTime.now();

    public MultiPlayerSession(String lobbyCode, String hostNickname) {
        this.lobbyCode = lobbyCode;
        this.hostNickname = hostNickname;
        this.hostBoard = new Board();
        this.guestBoard = new Board();
        this.lobbyStatus = LobbyStatus.WAITING;
        this.isHostTurn = true;
        this.lastActivity = LocalDateTime.now();
        this.hostReady = false;
        this.guestReady = false;
    }
}
