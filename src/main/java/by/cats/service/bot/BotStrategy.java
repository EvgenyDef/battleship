package by.cats.service.bot;

import by.cats.model.Board;
import by.cats.model.Point;

public interface BotStrategy {
    public Point getNextShot(Board playerBoard);
}
