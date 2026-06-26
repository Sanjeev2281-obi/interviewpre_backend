package com.example.demo.dto;

public class UsageStatsDto {
    private Integer weeklyUsed;
    private Integer weeklyLimit;
    private Integer remaining;
    private String resetsAt;

    public UsageStatsDto() {}

    public UsageStatsDto(Integer weeklyUsed, Integer weeklyLimit, Integer remaining, String resetsAt) {
        this.weeklyUsed = weeklyUsed;
        this.weeklyLimit = weeklyLimit;
        this.remaining = remaining;
        this.resetsAt = resetsAt;
    }

    public Integer getWeeklyUsed() { return weeklyUsed; }
    public void setWeeklyUsed(Integer weeklyUsed) { this.weeklyUsed = weeklyUsed; }

    public Integer getWeeklyLimit() { return weeklyLimit; }
    public void setWeeklyLimit(Integer weeklyLimit) { this.weeklyLimit = weeklyLimit; }

    public Integer getRemaining() { return remaining; }
    public void setRemaining(Integer remaining) { this.remaining = remaining; }

    public String getResetsAt() { return resetsAt; }
    public void setResetsAt(String resetsAt) { this.resetsAt = resetsAt; }
}
