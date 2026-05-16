package by.cats.dto.request;

import by.cats.model.Point;
import lombok.Data;

@Data
public class FireRequest {
    private String sessionId;
    private Point point;
}
