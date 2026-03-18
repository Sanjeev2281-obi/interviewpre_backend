package com.example.demo.service;
 
import com.example.demo.dto.DiscussionDto;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
 
@Service
public class DiscussionService {
 
    private final DiscussionRepository discussionRepo;
    private final DiscussionCommentRepository commentRepo;
    private final DiscussionLikeRepository likeRepo;
 
    private static final String[] AVATAR_COLORS = {
        "#1d4ed8","#7c3aed","#0f766e","#b45309","#dc2626","#0369a1","#374151","#065f46"
    };
 
    public DiscussionService(DiscussionRepository discussionRepo,
                             DiscussionCommentRepository commentRepo,
                             DiscussionLikeRepository likeRepo) {
        this.discussionRepo = discussionRepo;
        this.commentRepo = commentRepo;
        this.likeRepo = likeRepo;
    }
 
    public List<DiscussionDto> getAll(Long currentUserId) {
        return discussionRepo.findAllByOrderByCreatedAtDesc()
            .stream()
            .map(d -> toDto(d, currentUserId, false))
            .collect(Collectors.toList());
    }
 
    public DiscussionDto getById(Long id, Long currentUserId) {
        Discussion d = discussionRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Not found"));
        DiscussionDto dto = toDto(d, currentUserId, true);
        return dto;
    }
 
    @Transactional
    public DiscussionDto create(Long userId, String authorName, String company,
                                 String tag, String title, String body) {
        Discussion d = new Discussion(userId, authorName, company, tag, title, body);
        discussionRepo.save(d);
        return toDto(d, userId, false);
    }
 
    @Transactional
    public Map<String, Object> toggleLike(Long discussionId, Long userId) {
        Discussion d = discussionRepo.findById(discussionId)
            .orElseThrow(() -> new RuntimeException("Not found"));
 
        boolean alreadyLiked = likeRepo.existsByDiscussionIdAndUserId(discussionId, userId);
        if (alreadyLiked) {
            likeRepo.findByDiscussionIdAndUserId(discussionId, userId)
                .ifPresent(likeRepo::delete);
            d.setLikes(Math.max(0, d.getLikes() - 1));
        } else {
            likeRepo.save(new DiscussionLike(discussionId, userId));
            d.setLikes(d.getLikes() + 1);
        }
        discussionRepo.save(d);
        return Map.of("liked", !alreadyLiked, "likes", d.getLikes());
    }
 
    @Transactional
    public DiscussionDto.CommentDto addComment(Long discussionId, Long userId, String authorName, String body) {
        Discussion d = discussionRepo.findById(discussionId)
            .orElseThrow(() -> new RuntimeException("Not found"));
 
        DiscussionComment comment = new DiscussionComment(discussionId, userId, authorName, body);
        commentRepo.save(comment);
        d.setCommentCount(d.getCommentCount() + 1);
        discussionRepo.save(d);
 
        return new DiscussionDto.CommentDto(comment.getId(), comment.getAuthorName(),
                                            comment.getBody(), comment.getCreatedAt());
    }
 
    // ── Helper ────────────────────────────────────────────
    private DiscussionDto toDto(Discussion d, Long currentUserId, boolean includeComments) {
        DiscussionDto dto = new DiscussionDto();
        dto.setId(d.getId());
        dto.setAuthor(d.getAuthorName());
        dto.setCompany(d.getCompany() != null ? d.getCompany() : "Member");
        String name = d.getAuthorName() != null ? d.getAuthorName() : "U";
        dto.setAvatar(name.length() >= 2 ? name.substring(0, 2).toUpperCase() : name.toUpperCase());
        dto.setAvatarBg(AVATAR_COLORS[(int)(d.getId() % AVATAR_COLORS.length)]);
        dto.setTag(d.getTag());
        dto.setTitle(d.getTitle());
        dto.setBody(d.getBody());
        dto.setLikes(d.getLikes());
        dto.setComments(d.getCommentCount());
        dto.setLiked(currentUserId != null && likeRepo.existsByDiscussionIdAndUserId(d.getId(), currentUserId));
        dto.setCreatedAt(formatTime(d.getCreatedAt()));
 
        if (includeComments) {
            dto.setCommentList(
                commentRepo.findByDiscussionIdOrderByCreatedAtAsc(d.getId())
                    .stream()
                    .map(c -> new DiscussionDto.CommentDto(c.getId(), c.getAuthorName(), c.getBody(), c.getCreatedAt()))
                    .collect(Collectors.toList())
            );
        }
        return dto;
    }
 
    private String formatTime(LocalDateTime dt) {
        if (dt == null) return "Recently";
        long days = ChronoUnit.DAYS.between(dt, LocalDateTime.now());
        if (days == 0) return "Today";
        if (days == 1) return "Yesterday";
        if (days < 7) return days + " days ago";
        if (days < 30) return (days / 7) + " weeks ago";
        return (days / 30) + " months ago";
    }
}
 
 