package by.cats.controller;

import by.cats.Model.Board;
import by.cats.Model.BotDifficulty;
import by.cats.Model.Point;
import by.cats.dto.response.ShotResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//План:
//
//POST /api/bot/start (принимает сложность, возвращает ID сессии).
//
//POST /api/bot/place-ships (игрок присылает свою расстановку).
//
//POST /api/bot/fire (игрок присылает координаты выстрела).

@RestController
@RequestMapping("api/game/bot")
public class BotGameController {

    @PostMapping("/start")
    public ResponseEntity<Integer> start(BotDifficulty botDifficulty) {

    }

    @PostMapping("/place-ship")
    public ResponseEntity<?> placeShip(Board userBoard) {

    }

    @PostMapping("/fire")
    public ResponseEntity<ShotResult> fire(Point point) {

    }
}
