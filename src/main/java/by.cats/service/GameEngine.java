package by.cats.service;


import by.cats.Model.Board;
import by.cats.Model.CellStatus;
import by.cats.Model.Point;
import by.cats.Model.Ship;
import by.cats.dto.response.ShotResult;
import by.cats.exception.DoubleShotException;
import org.springframework.stereotype.Service;

import java.util.List;

//GameEngine.java (Service)
//
//Описание: Главный сервис, управляющий процессом боя.
//
//План:
//
//Метод applyShot(Board enemyBoard, int x, int y):
//
//Проверяет, что в эту клетку еще не стреляли.
//
//Ищет, есть ли в этой координате корабль.
//
//Меняет статус клетки в Board.
//
//Если попал — увеличивает счетчик попаданий в Ship.
//
//Возвращает ShotResult.
@Service
public class GameEngine {

    public ShotResult applyShot(Board enemyBoard, int x, int y) throws DoubleShotException {

        ShotResult shotResult;

        CellStatus cellStatus = enemyBoard.getCellStatus(x, y);

        if (cellStatus == CellStatus.MISS || cellStatus == CellStatus.HIT || cellStatus == CellStatus.SUNK)
            throw new DoubleShotException("В эту клетку уже стреляли");

        else if (cellStatus == CellStatus.SHIP) {
            enemyBoard.setCellStatus(x, y, CellStatus.HIT);

            Ship ship = enemyBoard.getShipAt(x, y);

            ship.setHitCount(ship.getHitCount() + 1);

            boolean isSunk = ship.isSunk();

            if (isSunk) {
                List<Point> points = ship.getCoordinates();
                for (Point point : points) {
                    enemyBoard.setCellStatus(point.getX(), point.getY(), CellStatus.SUNK);
                }
                markNeighborsAsMiss(enemyBoard, ship);
            }

            shotResult = ShotResult.builder()
                    .point(new Point(x, y))
                    .status(isSunk ? "корабль уничтожен" : "попадание")
                    .points(isSunk ? ship.getCoordinates() : List.of())
                    .build();
        }

        else {
            enemyBoard.setCellStatus(x, y, CellStatus.MISS);
            shotResult = ShotResult.builder()
                    .point(new Point(x, y))
                    .status("промах")
                    .points(List.of())
                    .build();
        }

        return shotResult;

    }

    private void markNeighborsAsMiss(Board enemyBoard, Ship ship) {
        List<Point> points = ship.getCoordinates();
        for (Point p : points) {
            int x = p.getX();
            int y = p.getY();

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    int tempX = x + dx;
                    int tempY = y + dy;

                    if (tempX >= 0 && tempX <= 9 && tempY >= 0 && tempY <= 9
                    && enemyBoard.getCellStatus(tempX, tempY) == CellStatus.EMPTY) {
                        enemyBoard.setCellStatus(tempX, tempY, CellStatus.MISS);
                    }
                }
            }
        }
    }
}
