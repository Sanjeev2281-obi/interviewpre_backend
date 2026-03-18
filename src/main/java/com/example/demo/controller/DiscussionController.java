package com.example.demo.controller;
 
import com.example.demo.dto.DiscussionDto;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.DiscussionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
 
import java.util.List;
import java.util.Map;
 
@RestController
@RequestMapping("/api/discussions")
public class DiscussionController {
 
    private final DiscussionService discussionService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
 
    public DiscussionController(DiscussionService discussionService,
                                 JwtUtil jwtUtil,
                                 UserRepository userRepository) {
        this.discussionService = discussionService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }
 
    private User getUser(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            throw new RuntimeException("Missing Authorization");
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
 
    // GET /api/discussions — all discussions
    @GetMapping
    public ResponseEntity<List<DiscussionDto>> getAll(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = null;
        try { userId = getUser(authHeader).getId(); } catch (Exception ignored) {}
        return ResponseEntity.ok(discussionService.getAll(userId));
    }
 
    // GET /api/discussions/{id}
    @GetMapping("/{id}")
    public ResponseEntity<DiscussionDto> getById(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = null;
        try { userId = getUser(authHeader).getId(); } catch (Exception ignored) {}
        return ResponseEntity.ok(discussionService.getById(id, userId));
    }
 
    // POST /api/discussions — create
    @PostMapping
    public ResponseEntity<DiscussionDto> create(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> body) {
        User user = getUser(authHeader);
        String tag   = body.get("tag");
        String title = body.get("title");
        String content = body.get("body");
        if (title == null || title.isBlank() || content == null || content.isBlank())
            return ResponseEntity.badRequest().build();
 
        DiscussionDto dto = discussionService.create(
            user.getId(), user.getName(),
            body.getOrDefault("company", "Member"),
            tag, title, content
        );
        return ResponseEntity.ok(dto);
    }
 
    // POST /api/discussions/{id}/like — toggle like
    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> like(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        User user = getUser(authHeader);
        return ResponseEntity.ok(discussionService.toggleLike(id, user.getId()));
    }
 
    // POST /api/discussions/{id}/comments — add comment
    @PostMapping("/{id}/comments")
    public ResponseEntity<DiscussionDto.CommentDto> addComment(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> body) {
        User user = getUser(authHeader);
        String content = body.get("body");
        if (content == null || content.isBlank())
            return ResponseEntity.badRequest().build();
 
        return ResponseEntity.ok(
            discussionService.addComment(id, user.getId(), user.getName(), content)
        );
    }
}