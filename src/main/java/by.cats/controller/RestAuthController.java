package by.cats.controller;

import by.cats.dto.request.UserRegistrationDto;
import by.cats.dto.response.ApiResponseDto;
import by.cats.entity.User;
import by.cats.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

//@RestController
//@RequestMapping("/api/auth")
//public class RestAuthController {
//
//
//    private final UserRepository userRepository;
//
//    @Autowired
//    public RestAuthController(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    @PostMapping("/register")
//    public ResponseEntity<?> register(@RequestBody AuthRequest request) {
//        if (userRepository.findByName(request.getName()).isPresent()) {
//            return ResponseEntity.badRequest().body("Этот никнейм уже занят");
//        }
//
//        User user = new User();
//        user.setName(request.getName());
//        userRepository.save(user);
//
//        return ResponseEntity.ok(new AuthResponse("Регистрация успешна", user.getName()));
//    }
//
//    // Метод для проверки текущего пользователя (кто залогинен)
//    @GetMapping("/me")
//    public ResponseEntity<?> getCurrentUser(Principal principal) {
//        if (principal == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Вы не вошли в систему");
//        }
//        return ResponseEntity.ok(new AuthResponse("Пользователь авторизован", principal.getName()));
//    }
//}

@RestController
@RequestMapping("/api/auth")
public class RestAuthController {

    private final UserRepository userRepository;

    @Autowired
    public RestAuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponseDto> register(@RequestBody UserRegistrationDto regDto) {
        // 1. Мы получили DTO (regDto.getNickname())

        if (userRepository.findByName(regDto.getName()).isPresent()) {
            // 2. Возвращаем DTO с ошибкой
            return ResponseEntity.badRequest().body(
                    new ApiResponseDto(false, "Никнейм уже занят", null)
            );
        }

        User user = User.builder()
                .name(regDto.getName())
                .avatarName(regDto.getAvatarName())
                .build();
        userRepository.save(user);

        // 3. Возвращаем DTO с успехом
        return ResponseEntity.ok(
                new ApiResponseDto(true, "Регистрация прошла успешно", user.getName())
        );
    }
}