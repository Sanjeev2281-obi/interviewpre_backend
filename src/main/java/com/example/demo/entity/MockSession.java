package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "mock_sessions")
public class MockSession {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "company", nullable = false)
    private String company;

    @Column(name = "interview_type", nullable = false)
    private String interviewType;

    @Column(name = "level", nullable = false)
    private String level;

    @Column(name = "status", nullable = false)
    private String status; // RUNNING, COMPLETED

    @Column(name = "score")
    private Integer score;

    @Column(name = "grade")
    private String grade;

    @Column(name = "hiring_decision")
    private String hiringDecision;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt = LocalDateTime.now();

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "time_taken")
    private Long timeTaken; // in seconds

    public MockSession() {}

    public MockSession(User user, String company, String interviewType, String level, String status) {
        this.user = user;
        this.company = company;
        this.interviewType = interviewType;
        this.level = level;
        this.status = status;
        this.startedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getInterviewType() { return interviewType; }
    public void setInterviewType(String interviewType) { this.interviewType = interviewType; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public String getHiringDecision() { return hiringDecision; }
    public void setHiringDecision(String hiringDecision) { this.hiringDecision = hiringDecision; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public Long getTimeTaken() { return timeTaken; }
    public void setTimeTaken(Long timeTaken) { this.timeTaken = timeTaken; }
}
