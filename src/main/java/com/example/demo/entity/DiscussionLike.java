package com.example.demo.entity;
 
import jakarta.persistence.*;
 
@Entity
@Table(name = "discussion_likes",
       uniqueConstraints = @UniqueConstraint(columnNames = {"discussion_id", "user_id"}))
public class DiscussionLike {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @Column(name = "discussion_id", nullable = false)
    private Long discussionId;
 
    @Column(name = "user_id", nullable = false)
    private Long userId;
 
    public DiscussionLike() {}
    public DiscussionLike(Long discussionId, Long userId) {
        this.discussionId = discussionId;
        this.userId = userId;
    }
 
    public Long getId() { return id; }
    public Long getDiscussionId() { return discussionId; }
    public Long getUserId() { return userId; }
    public void setId(Long id) { this.id = id; }
    public void setDiscussionId(Long d) { this.discussionId = d; }
    public void setUserId(Long u) { this.userId = u; }
}