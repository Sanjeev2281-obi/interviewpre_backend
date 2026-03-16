package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_progress")
public class UserProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "problem_id")
    private Integer problemId;

    @Column(name = "problem_title")
    private String problemTitle;

    @Column(name = "difficulty")
    private String difficulty;

    @Column(name = "solved_at")
    private LocalDateTime solvedAt;

    public UserProgress() {}

    public UserProgress(Long userId, Integer problemId, String problemTitle, String difficulty) {
        this.userId = userId;
        this.problemId = problemId;
        this.problemTitle = problemTitle;  // ✅ fixed - was assigned twice
        this.difficulty = difficulty;       // ✅ fixed - was never set
        this.solvedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Integer getProblemId() { return problemId; }
    public String getProblemTitle() { return problemTitle; }
    public String getDifficulty() { return difficulty; }
    public LocalDateTime getSolvedAt() { return solvedAt; }

    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setProblemId(Integer problemId) { this.problemId = problemId; }
    public void setProblemTitle(String problemTitle) { this.problemTitle = problemTitle; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public void setSolvedAt(LocalDateTime solvedAt) { this.solvedAt = solvedAt; }
}