package by.cats.service.bot;

import by.cats.Model.Board;
import by.cats.Model.CellStatus;
import by.cats.Model.Point;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

// ищу клетки со статусом HIT. Если нашел, то стреляю по соседям
// Соседи должны иметь статус EMPTY или SHIP, только потом по ним нужно стрелять
// Если ненашел, то вызываю метод get getRandomAvailableShot

@Service
public class MediumBotStrategy extends BaseBotStrategy {

    @Override
    public Point getNextShot(Board playerBoard) {
        List<Point> availablePoints = new ArrayList<>();
        for (int i = 0; i <= 9; i++) {
            for (int j = 0; j <= 9; j++) {
                CellStatus cellStatus = playerBoard.getCellStatus(i, j);
                if (cellStatus == CellStatus.HIT) {
                    List<Point> neighbours = getNeighboursStatus(i, j);

                    availablePoints.addAll(neighbours.stream()
                            .filter(p -> playerBoard.getCellStatus(p.getX(), p.getY()) == CellStatus.EMPTY
                                    || playerBoard.getCellStatus(p.getX(), p.getY()) == CellStatus.SHIP)
                            .toList());
                }
            }
        }

        if (availablePoints.isEmpty())
            return getRandomAvailableShot(playerBoard);

        return availablePoints.getFirst();
    }

    private List<Point> getNeighboursStatus(Integer x, Integer y) {
        List<Point> neighbors = new ArrayList<>();
        if (y > 0) neighbors.add(new Point(x, y - 1));
        if (x < 9) neighbors.add(new Point(x + 1, y));
        if (y < 9) neighbors.add(new Point(x, y + 1));
        if (x > 0) neighbors.add(new Point(x - 1, y));
        return neighbors;
    }
}
