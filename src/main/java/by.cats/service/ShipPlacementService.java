package by.cats.service;

import by.cats.Model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// сначала проверяем
@Service
public class ShipPlacementService {
    private final int[] shipSizes = {4, 3, 3, 2, 2, 2, 1, 1, 1, 1};
    private final Random r = new Random();

    public void placeShipsRandomly(Board board) {
        List<Ship> randomShips = generateRandomPlacement();
        // Очищаем текущую доску
        board.clear(); // Добавь метод clear() в Board, если его нет

        // Заполняем доску полученными кораблями
        for (Ship ship : randomShips) {
            board.getShips().add(ship);
            for (Point p : ship.getCoordinates()) {
                board.setCellStatus(p.getX(), p.getY(), CellStatus.SHIP);
            }
        }
    }

    private List<Ship> generateRandomPlacement() {
        while (true) { // Главный цикл на случай "тупика"
            Board tempBoard = new Board(); // Временная доска для расстановки
            boolean allPlaced = true;

            for (int size : shipSizes) {
                boolean placed = false;
                int attempts = 0;

                // Пытаемся поставить конкретный корабль (макс 100 попыток)
                while (!placed && attempts < 100) {
                    int x = r.nextInt(10);
                    int y = r.nextInt(10);
                    boolean isHorizontal = r.nextBoolean();

                    if (canPlaceShip(tempBoard, x, y, size, isHorizontal)) {
                        // Создаем корабль и добавляем его на временную доску
                        Ship ship = createShip(x, y, size, isHorizontal);
                        tempBoard.getShips().add(ship);

                        // Обновляем клетки доски, чтобы canPlaceShip видел их
                        for (Point p : ship.getCoordinates()) {
                            tempBoard.setCellStatus(p.getX(), p.getY(), CellStatus.SHIP);
                        }
                        placed = true;
                    }
                    attempts++;
                }

                if (!placed) {
                    allPlaced = false;
                    break; // Не смогли поставить один корабль — бросаем всё и заново
                }
            }

            if (allPlaced) {
                return tempBoard.getShips();
            }
        }
    }

    private Ship createShip(int x, int y, int size, boolean isHorizontal) {
        List<Point> points = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            if (isHorizontal) {
                points.add(new Point(x + i, y));
            } else {
                points.add(new Point(x, y + i));
            }
        }
        // Используй свой конструктор Ship (тебе может понадобиться добавить такой конструктор)
        return new Ship(points, 0, ShipTypes.fromInt(size));
    }

    private boolean canPlaceShip(Board board, int x, int y, int size, boolean isHorizontal) {
        if (isHorizontal) {
            if (x + size > 10) return false;
        } else {
            if (y + size > 10) return false;
        }

        int startX = x - 1;
        int startY = y - 1;
        int endX = isHorizontal ? x + size : x + 1;
        int endY = isHorizontal ? y + 1 : y + size;

        for (int i = startX; i <= endX; i++) {
            for (int j = startY; j <= endY; j++) {
                // Если клетка внутри поля
                if (i >= 0 && i < 10 && j >= 0 && j < 10) {
                    if (board.getCellStatus(i, j) != CellStatus.EMPTY) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
