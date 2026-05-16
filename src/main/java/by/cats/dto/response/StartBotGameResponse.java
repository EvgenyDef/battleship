package by.cats.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StartBotGameResponse {
    private String sessionId;
    private String message;
}
