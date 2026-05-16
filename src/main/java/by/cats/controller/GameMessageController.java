package by.cats.controller;

import by.cats.dto.request.FireRequest;
import by.cats.dto.request.SetupShipsRequest;
import by.cats.service.MultiPlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class GameMessageController {
    private final MultiPlayerService multiPlayerService;

    @MessageMapping("/lobby/{lobbyCode}/ready")
    public void handleReady(@DestinationVariable String lobbyCode, Principal principal) {
        multiPlayerService.setPlayerReady(lobbyCode, principal.getName());
    }

    @MessageMapping("/lobby/{lobbyCode}/shot")
    public void handleShot(@DestinationVariable String lobbyCode, FireRequest shotRequest, Principal principal) {
        multiPlayerService.handleShot(lobbyCode, principal.getName(), shotRequest.getPoint());
    }

    @MessageMapping("/lobby/{lobbyCode}/setup")
    public void handleSetup(@DestinationVariable String lobbyCode, SetupShipsRequest request, Principal principal) {
        multiPlayerService.setupShips(lobbyCode, principal.getName(), request.getShips());
    }
}
