package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @CacheEvict(value = "users", key = "#email")
    public void upgradeUserToPro(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found: " + email));
        user.setIsPro(true);
        user.setRole("PRO");
        user.setProSince(LocalDateTime.now());
        userRepository.save(user);
        System.out.println("Upgraded to PRO: " + email);
    }
}
