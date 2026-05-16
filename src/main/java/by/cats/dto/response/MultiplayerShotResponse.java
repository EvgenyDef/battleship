package by.cats.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class MultiplayerShotResponse {
    private String type;
    private String shooter;
    private ShotResult shotResult;
    private String nextTurn;
    private String winner;
}
