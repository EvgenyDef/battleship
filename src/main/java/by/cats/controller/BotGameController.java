package by.cats.controller;

import by.cats.dto.request.FireRequest;
import by.cats.dto.request.SetupShipsRequest;
import by.cats.dto.request.StartBotGameRequest;
import by.cats.dto.response.ApiResponseDto;
import by.cats.dto.response.GameTurnResponse;
import by.cats.dto.response.StartBotGameResponse;
import by.cats.service.SinglePlayerService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("api/game/bot")
@AllArgsConstructor
public class BotGameController {

    private final SinglePlayerService singlePlayerService;

    @PostMapping("/start")
    public ResponseEntity<StartBotGameResponse> start(@RequestBody StartBotGameRequest request, Principal principal) {
        String sessionId = singlePlayerService.startNewGame(principal.getName(), request.getDifficulty());
        return ResponseEntity.ok(new StartBotGameResponse(sessionId, "игра создана"));
    }

    @PostMapping("/place-ship")
    public ResponseEntity<ApiResponseDto> setup(@RequestBody SetupShipsRequest request) {
        singlePlayerService.placePlayerShips(request.getSessionId(), request.getShips());
        return ResponseEntity.ok(new ApiResponseDto(true, "бой начался", null));
    }

    @PostMapping("/fire")
    public ResponseEntity<GameTurnResponse> fire(@RequestBody FireRequest request) {
        GameTurnResponse actions = singlePlayerService.playTurn(request.getSessionId(), request.getPoint());
        return ResponseEntity.ok(actions);
    }
}
