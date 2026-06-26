package com.example.demo.dto;

public class WeakTopicDto {
    private String topic;
    private Integer attempts;
    private Integer failures;
    private Double strengthScore;

    public WeakTopicDto() {}

    public WeakTopicDto(String topic, Integer attempts, Integer failures, Double strengthScore) {
        this.topic = topic;
        this.attempts = attempts;
        this.failures = failures;
        this.strengthScore = strengthScore;
    }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public Integer getAttempts() { return attempts; }
    public void setAttempts(Integer attempts) { this.attempts = attempts; }

    public Integer getFailures() { return failures; }
    public void setFailures(Integer failures) { this.failures = failures; }

    public Double getStrengthScore() { return strengthScore; }
    public void setStrengthScore(Double strengthScore) { this.strengthScore = strengthScore; }
}
