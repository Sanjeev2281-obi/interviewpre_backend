package com.example.demo.entity;
 
import jakarta.persistence.*;
import java.time.LocalDateTime;
 
@Entity
@Table(name = "discussion_comments")
public class DiscussionComment {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @Column(nullable = false)
    private Long discussionId;
 
    @Column(nullable = false)
    private Long userId;
 
    @Column(nullable = false)
    private String authorName;
 
    @Column(nullable = false, length = 2000)
    private String body;
 
    @Column
    private LocalDateTime createdAt = LocalDateTime.now();
 
    public DiscussionComment() {}
 
    public DiscussionComment(Long discussionId, Long userId, String authorName, String body) {
        this.discussionId = discussionId;
        this.userId = userId;
        this.authorName = authorName;
        this.body = body;
    }
 
    public Long getId() { return id; }
    public Long getDiscussionId() { return discussionId; }
    public Long getUserId() { return userId; }
    public String getAuthorName() { return authorName; }
    public String getBody() { return body; }
    public LocalDateTime getCreatedAt() { return createdAt; }
 
    public void setId(Long id) { this.id = id; }
    public void setDiscussionId(Long discussionId) { this.discussionId = discussionId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public void setBody(String body) { this.body = body; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
 
 