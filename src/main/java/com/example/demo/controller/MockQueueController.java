package com.example.demo.controller;

import com.example.demo.dto.GenerateMockRequest;
import com.example.demo.entity.User;
import com.example.demo.service.AuthService;
import com.example.demo.service.QueueService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/mock")
public class MockQueueController {

    private final QueueService queueService;
    private final AuthService authService;

    public MockQueueController(QueueService queueService, AuthService authService) {
        this.queueService = queueService;
        this.authService = authService;
    }

    private User getAuthenticatedUser(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
        }
        return authService.getCurrentUser(authHeader);
    }

    @PostMapping("/enqueue")
    public ResponseEntity<Map<String, Object>> enqueue(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody GenerateMockRequest request) {
        User user = getAuthenticatedUser(authHeader);
        UUID jobId = queueService.addToQueue(user.getId(), request);
        
        Map<String, Object> response = new HashMap<>();
        response.put("jobId", jobId.toString());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/queue/{jobId}")
    public ResponseEntity<Map<String, Object>> getQueueStatus(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable("jobId") UUID jobId) {
        // Enforce user authentication
        getAuthenticatedUser(authHeader);
        
        Map<String, Object> jobStatus = queueService.getJobStatus(jobId);
        return ResponseEntity.ok(jobStatus);
    }
}
