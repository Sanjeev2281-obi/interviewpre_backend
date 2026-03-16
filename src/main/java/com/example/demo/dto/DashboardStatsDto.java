package com.example.demo.dto;

import java.util.List;

public class DashboardStatsDto {
    private long totalSolved;
    private long easySolved;
    private long mediumSolved;
    private long hardSolved;
    private int streak;
    private Integer resumeScore;
    private int mockInterviews;
    private List<RecentProblem> recentProblems;
    private List<Integer> solvedProblemIds;  // ✅ ADD THIS

    public DashboardStatsDto() {}

    public static class RecentProblem {
        private Integer problemId;
        private String title;
        private String difficulty;
        private String solvedAt;

        public RecentProblem(Integer problemId, String title, String difficulty, String solvedAt) {
            this.problemId = problemId;
            this.title = title;
            this.difficulty = difficulty;
            this.solvedAt = solvedAt;
        }

        public Integer getProblemId() { return problemId; }
        public String getTitle() { return title; }
        public String getDifficulty() { return difficulty; }
        public String getSolvedAt() { return solvedAt; }
    }

    public long getTotalSolved() { return totalSolved; }
    public void setTotalSolved(long totalSolved) { this.totalSolved = totalSolved; }
    public long getEasySolved() { return easySolved; }
    public void setEasySolved(long easySolved) { this.easySolved = easySolved; }
    public long getMediumSolved() { return mediumSolved; }
    public void setMediumSolved(long mediumSolved) { this.mediumSolved = mediumSolved; }
    public long getHardSolved() { return hardSolved; }
    public void setHardSolved(long hardSolved) { this.hardSolved = hardSolved; }
    public int getStreak() { return streak; }
    public void setStreak(int streak) { this.streak = streak; }
    public Integer getResumeScore() { return resumeScore; }
    public void setResumeScore(Integer resumeScore) { this.resumeScore = resumeScore; }
    public int getMockInterviews() { return mockInterviews; }
    public void setMockInterviews(int mockInterviews) { this.mockInterviews = mockInterviews; }
    public List<RecentProblem> getRecentProblems() { return recentProblems; }
    public void setRecentProblems(List<RecentProblem> recentProblems) { this.recentProblems = recentProblems; }

    // ✅ ADD THESE TWO
    public List<Integer> getSolvedProblemIds() { return solvedProblemIds; }
    public void setSolvedProblemIds(List<Integer> solvedProblemIds) { this.solvedProblemIds = solvedProblemIds; }
}