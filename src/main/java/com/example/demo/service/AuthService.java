package com.example.demo.service;

import com.example.demo.dto.AuthResponse;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder encoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(String name, String email, String password) {

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered");
        }

        User user = User.builder()
                .name(name)
                .email(email)
                .password(encoder.encode(password))
                .role("USER")
                .build();

        User saved = userRepository.save(user);

        String token = jwtUtil.generateToken(saved.getEmail(), saved.getRole());

        return new AuthResponse(
            token,
            new AuthResponse.UserDto(saved.getId(), saved.getName(), saved.getEmail(), saved.getRole())
        );
    }

    public AuthResponse login(String email, String password) {

        // ← CHANGE: uses cached user instead of hitting DB every login
        User user = findByEmail(email);

        if (!encoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        return new AuthResponse(
            token,
            new AuthResponse.UserDto(user.getId(), user.getName(), user.getEmail(), user.getRole())
        );
    }

    public User getCurrentUser(String token) {

        String jwt = token.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(jwt);

        // ← CHANGE: uses cached user instead of hitting DB on every API request
        return findByEmail(email);
    }

    // ← NEW: cached method — DB only hit once per unique email, then served from memory
    @Cacheable(value = "users", key = "#email")
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ← NEW: call this if user data changes (role upgrade, name change etc.)
    @CacheEvict(value = "users", key = "#email")
    public void evictUserCache(String email) {
        // clears cached user so next request fetches fresh data from DB
    }
    @CacheEvict(value = "users", key = "#email")
    public void upgradeUserToPro(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setRole("PRO");
        userRepository.save(user);
    }
}