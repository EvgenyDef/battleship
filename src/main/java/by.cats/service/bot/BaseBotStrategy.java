package by.cats.service.bot;

import by.cats.Model.Board;
import by.cats.Model.CellStatus;
import by.cats.Model.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

abstract class BaseBotStrategy implements BotStrategy {

    Random random = new Random();

    protected Point getRandomAvailableShot(Board playerBoard) {
        List<Point> avaliblePoints = new ArrayList<>();

        for (int i = 0; i <= 9; i++) {
            for (int j = 0; j <= 9; j++) {
                CellStatus cellStatus = playerBoard.getCellStatus(i, j);

                if (cellStatus == CellStatus.SHIP || cellStatus == CellStatus.EMPTY)
                    avaliblePoints.add(new Point(i, j));
            }
        }
        return avaliblePoints.get(random.nextInt(avaliblePoints.size()));
    }

}
