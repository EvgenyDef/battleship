package by.cats.Model;

/*
Двумерный массив или список CellStatus[][] grid.

Список всех кораблей на этом поле List<Ship>.

Метод для инициализации поля (заполнение EMPTY).

Метод для проверки, остались ли еще живые корабли на поле.
*/

import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;


public class Board {
    private CellStatus[][] grid;
    private List<Ship> ships;

    public void initGrid() {
        grid = new CellStatus[10][10];
    }

    public void checkLiveShips() {

    }
}
