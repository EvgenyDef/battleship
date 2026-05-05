package by.cats.Model;

/*
Двумерный массив или список CellStatus[][] grid.

Список всех кораблей на этом поле List<Ship>.

Метод для инициализации поля (заполнение EMPTY).

Метод для проверки, остались ли еще живые корабли на поле.
*/


import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


public class Board {
    private CellStatus[][] grid;

    @Getter
    @Setter
    private List<Ship> ships;


    public Board() {
        this.grid = new CellStatus[10][10];
        this.ships = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                grid[i][j] = CellStatus.EMPTY;
            }
        }
    }

    public void setCellStatus(Integer x, Integer y, CellStatus cellStatus) {
        grid[x][y] = cellStatus;
    }

    public CellStatus getCellStatus(Integer x, Integer y) {
        return grid[x][y];
    }

    public boolean checkLiveShips() {
        boolean flag = false;
        for (Ship ship : ships) {
            if (!ship.isSunk())
                return true;
        }
        return false;
    }

    public Ship getShipAt(int x, int y) {
        return ships.stream()
                .filter(ship -> ship.getCoordinates().stream()
                        .anyMatch(p -> p.getX() == x && p.getY() == y))
                .findFirst()
                .orElse(null);
    }

    public void clear() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                grid[i][j] = CellStatus.EMPTY;
            }
        }
    }
}
