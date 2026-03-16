package com.example.demo.controller;

import com.example.demo.dto.DashboardStatsDto;
import com.example.demo.dto.ToggleSolvedRequest;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.ProgressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/progress")

public class ProgressController {

    private final ProgressService progressService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public ProgressController(ProgressService progressService, JwtUtil jwtUtil, UserRepository userRepository) {
        this.progressService = progressService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    private User getUser(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // GET /api/progress/stats
    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDto> getStats(
            @RequestHeader("Authorization") String authHeader) {
        User user = getUser(authHeader);
        return ResponseEntity.ok(progressService.getStats(user.getId()));
    }

    // POST /api/progress/toggle
    @PostMapping("/toggle")
    public ResponseEntity<Map<String, Object>> toggle(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ToggleSolvedRequest request) {
        User user = getUser(authHeader);
        boolean solved = progressService.toggleSolved(user.getId(), request);
        return ResponseEntity.ok(Map.of(
            "solved", solved,
            "problemId", request.getProblemId()
        ));
    }
}