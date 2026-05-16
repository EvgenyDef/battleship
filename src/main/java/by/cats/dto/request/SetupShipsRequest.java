package by.cats.dto.request;

import by.cats.model.Ship;
import lombok.Data;

import java.util.List;

@Data
public class SetupShipsRequest {
    private String sessionId;
    private List<Ship> ships;
}
