package by.cats.service.bot;

import by.cats.Model.Board;
import by.cats.Model.Point;
import by.cats.Model.CellStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HardBotStrategy extends BaseBotStrategy {

    @Override
    public Point getNextShot(Board playerBoard) {
        // 1. Сначала проверяем, есть ли кого добивать (как в Medium, но можно умнее)
        Point huntShot = findSmartHuntShot(playerBoard);
        if (huntShot != null) return huntShot;

        // 2. Если раненых нет, используем шахматную стратегию (обстрел 50% поля)
        Point searchShot = getChessboardShot(playerBoard);
        if (searchShot != null) return searchShot;

        // 3. Если шахматная сетка закончилась (ищем оставшиеся однопалубники)
        return getRandomAvailableShot(playerBoard);
    }

    /**
     * Алгоритм шахматной сетки (Диагональный обстрел)
     */
    private Point getChessboardShot(Board board) {
        List<Point> targets = new ArrayList<>();
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                // Клетки, где сумма координат четная (белые клетки шахматной доски)
                if ((x + y) % 2 == 0) {
                    CellStatus status = board.getCellStatus(x, y);
                    if (status == CellStatus.EMPTY || status == CellStatus.SHIP) {
                        targets.add(new Point(x, y));
                    }
                }
            }
        }
        if (targets.isEmpty()) return null;
        return targets.get(random.nextInt(targets.size()));
    }


    private Point findSmartHuntShot(Board board) {
        List<Point> hits = new ArrayList<>();
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                if (board.getCellStatus(x, y) == CellStatus.HIT) {
                    hits.add(new Point(x, y));
                }
            }
        }

        if (hits.isEmpty()) return null;

        // Если подбита только одна клетка - стреляем в любого соседа
        if (hits.size() == 1) {
            return getRandomValidNeighbor(hits.get(0), board);
        }

        // Если подбито 2+ клетки, определяем направление (линию)
        Point p1 = hits.get(0);
        Point p2 = hits.get(1);
        boolean isHorizontal = p1.getX() == p2.getX();

        List<Point> lineTargets = new ArrayList<>();
        for (Point hit : hits) {
            List<Point> neighbors = getNeighbors(hit.getX(), hit.getY());
            for (Point n : neighbors) {
                // Если мы знаем, что корабль горизонтальный, проверяем только соседей по горизонтали
                if (isHorizontal && n.getX() != p1.getX()) continue;
                if (!isHorizontal && n.getY() != p1.getY()) continue;

                CellStatus status = board.getCellStatus(n.getX(), n.getY());
                if (status == CellStatus.EMPTY || status == CellStatus.SHIP) {
                    lineTargets.add(n);
                }
            }
        }

        if (lineTargets.isEmpty()) return null;
        return lineTargets.get(random.nextInt(lineTargets.size()));
    }

    private Point getRandomValidNeighbor(Point p, Board board) {
        List<Point> neighbors = getNeighbors(p.getX(), p.getY());
        List<Point> validNeighbors = neighbors.stream()
                .filter(n -> board.getCellStatus(n.getX(), n.getY()) == CellStatus.EMPTY ||
                        board.getCellStatus(n.getX(), n.getY()) == CellStatus.SHIP)
                .collect(Collectors.toList());

        return validNeighbors.isEmpty() ? null : validNeighbors.get(random.nextInt(validNeighbors.size()));
    }

    private List<Point> getNeighbors(int x, int y) {
        List<Point> neighbors = new ArrayList<>();
        if (y > 0) neighbors.add(new Point(x, y - 1));
        if (x < 9) neighbors.add(new Point(x + 1, y));
        if (y < 9) neighbors.add(new Point(x, y + 1));
        if (x > 0) neighbors.add(new Point(x - 1, y));
        return neighbors;
    }
}
