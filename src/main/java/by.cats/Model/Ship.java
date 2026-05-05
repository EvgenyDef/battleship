package by.cats.Model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Ship {
    private List<Point> coordinates;
    private Integer hitCount;
    private ShipTypes shipType;

    // проверка, уничтожен корабль или нет
    public Boolean isSunk() {
        return hitCount == coordinates.size();
    }
}
