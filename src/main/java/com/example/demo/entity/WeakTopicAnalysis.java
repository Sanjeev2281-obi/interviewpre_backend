package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "weak_topic_analysis")
public class WeakTopicAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "topic", nullable = false)
    private String topic;

    @Column(name = "attempts", nullable = false)
    private Integer attempts = 0;

    @Column(name = "failures", nullable = false)
    private Integer failures = 0;

    @Column(name = "strength_score")
    private Double strengthScore;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public WeakTopicAnalysis() {}

    public WeakTopicAnalysis(User user, String topic, Integer attempts, Integer failures, Double strengthScore) {
        this.user = user;
        this.topic = topic;
        this.attempts = attempts;
        this.failures = failures;
        this.strengthScore = strengthScore;
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public Integer getAttempts() { return attempts; }
    public void setAttempts(Integer attempts) { this.attempts = attempts; }

    public Integer getFailures() { return failures; }
    public void setFailures(Integer failures) { this.failures = failures; }

    public Double getStrengthScore() { return strengthScore; }
    public void setStrengthScore(Double strengthScore) { this.strengthScore = strengthScore; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
