package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.entity.User;
import com.example.demo.service.AuthService;
import com.example.demo.service.MockService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/mock")
public class MockController {

    private final MockService mockService;
    private final AuthService authService;

    public MockController(MockService mockService, AuthService authService) {
        this.mockService = mockService;
        this.authService = authService;
    }

    private User getAuthenticatedUser(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
        }
        return authService.getCurrentUser(authHeader);
    }

    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generate(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody GenerateMockRequest request) {
        User user = getAuthenticatedUser(authHeader);
        Map<String, Object> sessionData = mockService.generateSession(user, request);
        return ResponseEntity.ok(sessionData);
    }

    @PostMapping("/evaluate")
    public ResponseEntity<Map<String, Object>> evaluate(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody EvaluateMockRequest request) {
        User user = getAuthenticatedUser(authHeader);
        Map<String, Object> evaluationData = mockService.evaluateSession(user, request);
        return ResponseEntity.ok(evaluationData);
    }

    @GetMapping("/history")
    public ResponseEntity<List<Map<String, Object>>> getHistory(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        User user = getAuthenticatedUser(authHeader);
        List<Map<String, Object>> history = mockService.getSessions(user, page, size);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/history/{sessionId}")
    public ResponseEntity<Map<String, Object>> getHistoryDetails(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable("sessionId") UUID sessionId) {
        User user = getAuthenticatedUser(authHeader);
        Map<String, Object> details = mockService.getSessionDetails(user, sessionId);
        return ResponseEntity.ok(details);
    }

    @GetMapping("/usage")
    public ResponseEntity<UsageStatsDto> getUsage(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        User user = getAuthenticatedUser(authHeader);
        UsageStatsDto usage = mockService.getUsage(user);
        return ResponseEntity.ok(usage);
    }

    @GetMapping("/weak-topics")
    public ResponseEntity<List<WeakTopicDto>> getWeakTopics(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        User user = getAuthenticatedUser(authHeader);
        List<WeakTopicDto> weakTopics = mockService.getWeakTopics(user);
        return ResponseEntity.ok(weakTopics);
    }

    @PostMapping("/retry")
    public ResponseEntity<Map<String, Object>> retry(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody Map<String, String> body) {
        User user = getAuthenticatedUser(authHeader);
        String sessionIdStr = body.get("sessionId");
        if (sessionIdStr == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "Missing sessionId in request body");
        }
        UUID sessionId = UUID.fromString(sessionIdStr);
        Map<String, Object> newSession = mockService.retrySession(user, sessionId);
        return ResponseEntity.ok(newSession);
    }
}
