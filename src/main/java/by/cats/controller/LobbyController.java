package by.cats.controller;

import by.cats.dto.response.LobbyResponse;
import by.cats.service.MultiPlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/lobby")
@RequiredArgsConstructor
public class LobbyController {

    private final MultiPlayerService multiPlayerService;

    @PostMapping("/create")
    public ResponseEntity<LobbyResponse> create(String hostNickname) {
        String lobbyId = multiPlayerService.createLobby(hostNickname);
        return ResponseEntity.ok(new LobbyResponse(lobbyId, null));
    }

    @PostMapping("/join")
    public ResponseEntity<LobbyResponse> join(String lobbyCode, String guestNickname) {
        multiPlayerService.joinLobby(lobbyCode, guestNickname);
        return ResponseEntity.ok(new LobbyResponse(lobbyCode, "Игрок успешно зашел в лобби"));
    }
}
