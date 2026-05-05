package by.cats.service;

import by.cats.Model.Point;
import by.cats.Model.Ship;
import by.cats.Model.ShipTypes;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShipPlacementValidator {

    private static final Map<ShipTypes, Integer> SHIPS_COUNT_EXPECTED = Map.of(
            ShipTypes.FOUR_DECK, 1,
        ShipTypes.THREE_DECK, 2,
        ShipTypes.TWO_DECK, 3,
        ShipTypes.ONE_DECK, 4
    );

    public boolean validateAll(List<Ship> ships) {
        return checkCount(ships) && checkBounds(ships) && checkTouch(ships) && checkShipShapes(ships);
    }

    // Проверка, что количество кораблей совпадает с правилами (1x4, 2x3, 3x2, 4x1).
    public boolean checkCount(List<Ship> ships) {
        Map<ShipTypes, Integer> shipsCountActual = new HashMap<>();

        for (Ship ship : ships) {
            shipsCountActual.put(ship.getShipType(), shipsCountActual.getOrDefault(ship.getShipType(), 0) + 1);
            }
        return SHIPS_COUNT_EXPECTED.equals(shipsCountActual);
    }

    // проверка, что каждый корабль имеет координаты от [0, 9]
    public boolean checkBounds(List<Ship> ships) {
        for (Ship ship : ships) {
            List<Point> points = ship.getCoordinates();
            for (Point point : points) {
                if (point.getX() < 0 || point.getY() < 0 || point.getX() > 9 || point.getY() > 9) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean checkShipShapes(List<Ship> ships) {
        for (Ship ship : ships) {
            List<Point> coords = ship.getCoordinates();
            if (coords.size() == 1) continue; // Однопалубник всегда валиден по форме

            // Сортируем координаты, чтобы проверять последовательность
            coords.sort((p1, p2) -> p1.getX() != p2.getX() ? p1.getX() - p2.getX() : p1.getY() - p2.getY());

            boolean horizontal = coords.getFirst().getX() == coords.getLast().getX();
            boolean vertical = coords.getFirst().getY() == coords.getLast().getY();

            if (!horizontal && !vertical) return false; // Корабль "кривой" (не по прямой)

            // Проверяем, что нет "дырок" (идут подряд)
            for (int i = 0; i < coords.size() - 1; i++) {
                if (horizontal && coords.get(i + 1).getY() - coords.get(i).getY() != 1) return false;
                if (vertical && coords.get(i + 1).getX() - coords.get(i).getX() != 1) return false;
            }
        }
        return true;
    }

    // Проверка правила "соседних клеток" (корабли не должны касаться друг друга даже углами).
    public boolean checkTouch(List<Ship> ships) {
        // 1. Создаем сетку, где будем хранить ID кораблей (индекс в списке + 1)
        // 0 - пусто, 1...N - номер корабля
        int[][] grid = new int[10][10];

        for (int i = 0; i < ships.size(); i++) {
            for (Point p : ships.get(i).getCoordinates()) {
                // Если в этой клетке уже кто-то есть (корабли наложились)
                if (grid[p.getX()][p.getY()] != 0) return false;
                grid[p.getX()][p.getY()] = i + 1;
            }
        }

        // 2. Проверяем соседей для каждой клетки каждого корабля
        for (int i = 0; i < ships.size(); i++) {
            int shipId = i + 1;
            for (Point p : ships.get(i).getCoordinates()) {

                // Проверяем все 8 направлений вокруг точки
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        int nx = p.getX() + dx;
                        int ny = p.getY() + dy;

                        // Проверка границ массива
                        if (nx >= 0 && nx < 10 && ny >= 0 && ny < 10) {
                            int neighborId = grid[nx][ny];
                            // Если в соседней клетке чужой корабль
                            if (neighborId != 0 && neighborId != shipId) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}
