package by.cats.service.bot;

import by.cats.model.Board;
import by.cats.model.Point;
import org.springframework.stereotype.Service;

@Service
public class EasyBotStrategy extends BaseBotStrategy {

    @Override
    public Point getNextShot(Board playerBoard) {
        return getRandomAvailableShot(playerBoard);
    }

}
