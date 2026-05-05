package by.cats.dto.response;

import by.cats.Model.GameStatus;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GameTurnResponse {
    private ShotResult playerShot;
    private List<ShotResult> botShots;
    private boolean isPlayerTurn;
    private GameStatus gameStatus;

}
