package by.cats.service;

import by.cats.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthService {
    private UserRepository userRepository;


}
