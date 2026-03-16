package com.example.demo.dto;

public class ToggleSolvedRequest {
    private Integer problemId;
    private String problemTitle;
    private String difficulty;

    public Integer getProblemId() { return problemId; }
    public String getProblemTitle() { return problemTitle; }
    public String getDifficulty() { return difficulty; }

    public void setProblemId(Integer problemId) { this.problemId = problemId; }
    public void setProblemTitle(String problemTitle) { this.problemTitle = problemTitle; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
}