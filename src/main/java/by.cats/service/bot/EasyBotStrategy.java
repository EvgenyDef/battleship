package by.cats.service.bot;

import by.cats.Model.Board;
import by.cats.Model.CellStatus;
import by.cats.Model.Point;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class EasyBotStrategy extends BaseBotStrategy {

    @Override
    public Point getNextShot(Board playerBoard) {
        return getRandomAvailableShot(playerBoard);
    }

}
