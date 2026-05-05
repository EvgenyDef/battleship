package by.cats.dto.response;

import by.cats.Model.CellStatus;
import by.cats.Model.Point;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ShotResult {
    private String status;
    private Point point;
    private List<Point> points;
}
