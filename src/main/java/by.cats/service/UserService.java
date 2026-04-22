package by.cats.service;


import by.cats.entity.User;
import by.cats.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public Optional<User> findUserById(Integer userId) {
        return userRepository.findUserById(userId);
    }

    public Optional<User> findUserByName(String name) {
        return userRepository.findByName(name);
    }

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
