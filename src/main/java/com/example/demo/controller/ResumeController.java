package com.example.demo.controller;
import com.example.demo.dto.ToggleSolvedRequest;
import com.example.demo.entity.ResumeReview;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.ResumeReviewRepository;
import com.example.demo.service.ProgressService;
import com.example.demo.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import com.example.demo.entity.User;
@RestController
@RequestMapping("/api/resume")
public class ResumeController {

    private final ProgressService progressService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public ResumeController(ProgressService progressService, JwtUtil jwtUtil, UserRepository userRepository) {
        this.progressService = progressService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

   private User getUser(String authHeader) {
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        throw new RuntimeException("Missing or invalid Authorization header");
    }
    String token = authHeader.replace("Bearer ", "");
    String email = jwtUtil.extractEmail(token);
    return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
}
@PostMapping("/review")
public ResponseEntity<Map<String, Object>> saveResumeScore(
        @RequestHeader(value = "Authorization", required = false) String authHeader,
        @RequestBody Map<String, Integer> request) {

    if (authHeader == null) {
        return ResponseEntity.status(401).body(Map.of("error", "Authorization header missing"));
    }

    User user = getUser(authHeader);
    Integer score = request.get("score");
    if (score == null) {
        return ResponseEntity.badRequest().body(Map.of("error", "Score is required"));
    }

    ResumeReview review = progressService.saveResumeScore(user.getId(), score);

    return ResponseEntity.ok(Map.of(
        "score", review.getScore(),
        "userId", user.getId(),
        "updatedAt", review.getCreatedAt()
    ));
}
}