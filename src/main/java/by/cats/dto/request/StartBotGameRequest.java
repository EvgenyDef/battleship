package by.cats.dto.request;

import by.cats.model.BotDifficulty;
import lombok.Data;

@Data
public class StartBotGameRequest {
    private BotDifficulty difficulty;
}
