package com.example.demo.entity;



import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resume_review")
public class ResumeReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "score")
    private Integer score;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public ResumeReview() {}

    public ResumeReview(Long userId, Integer score) {
        this.userId = userId;
        this.score = score;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Integer getScore() { return score; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setScore(Integer score) { this.score = score; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}