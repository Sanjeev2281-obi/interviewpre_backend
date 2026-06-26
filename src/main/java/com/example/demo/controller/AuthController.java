package com.example.demo.controller;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.User;
import com.example.demo.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(
            request.getName(),
            request.getEmail(),
            request.getPassword()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(
            request.getEmail(),
            request.getPassword()
        );
        return ResponseEntity.ok(response);
    }

    // ✅ Single /me endpoint — handles both old me() and new getMe() calls
    @GetMapping("/me")
    public ResponseEntity<AuthResponse.UserDto> me(
            @RequestHeader("Authorization") String authHeader) {
        User user = authService.getCurrentUser(authHeader);
        return ResponseEntity.ok(
            new AuthResponse.UserDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
            )
        );
    }
}