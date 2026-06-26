package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "user_usage_tracking")
public class UserUsageTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "week_start", nullable = false)
    private LocalDate weekStart;

    @Column(name = "weekly_count", nullable = false)
    private Integer weeklyCount = 0;

    public UserUsageTracking() {}

    public UserUsageTracking(User user, LocalDate weekStart, Integer weeklyCount) {
        this.user = user;
        this.weekStart = weekStart;
        this.weeklyCount = weeklyCount;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public LocalDate getWeekStart() { return weekStart; }
    public void setWeekStart(LocalDate weekStart) { this.weekStart = weekStart; }

    public Integer getWeeklyCount() { return weeklyCount; }
    public void setWeeklyCount(Integer weeklyCount) { this.weeklyCount = weeklyCount; }
}
