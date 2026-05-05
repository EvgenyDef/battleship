package by.cats.service.bot;

import by.cats.Model.Board;
import by.cats.Model.Point;

public interface BotStrategy {
    public Point getNextShot(Board playerBoard);
}
