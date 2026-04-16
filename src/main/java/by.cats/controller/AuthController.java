package by.cats.controller;

import by.cats.entity.User;
import by.cats.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
    private final UserRepository userRepository;

    @Autowired
    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String nickname, Model model) {
        if (userRepository.findByName(nickname).isPresent()) {
            model.addAttribute("error", "Этот никнейм уже занят");
            return "register";
        }

        User user = User.builder()
                .name(nickname)
                .avatarName("avatar1")
                .build();
        userRepository.save(user);

        return "redirect:/login"; // После регистрации отправляем на вход
    }

    @GetMapping("/game")
    public String gamePage(java.security.Principal principal, org.springframework.ui.Model model) {
        // Principal содержит информацию о залогиненном пользователе
        model.addAttribute("nickname", principal.getName());
        return "game"; // Создадим файл game.html
    }
}
