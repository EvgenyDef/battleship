package by.cats.Model;

import lombok.Data;

import java.util.List;

@Data
public class Ship {
    private List<Point> coordinates;
    private Integer hitCount;

    public Boolean isSunk() {
        return hitCount == coordinates.size();
    }
}
