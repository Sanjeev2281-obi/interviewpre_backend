package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class SubmissionDto {

    @NotNull(message = "Question ID is required")
    private UUID questionId;

    private String language;
    private String codeAnswer;
    private String behavioralAnswer;
    private Integer usedHints = 0;

    public SubmissionDto() {}

    public UUID getQuestionId() { return questionId; }
    public void setQuestionId(UUID questionId) { this.questionId = questionId; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getCodeAnswer() { return codeAnswer; }
    public void setCodeAnswer(String codeAnswer) { this.codeAnswer = codeAnswer; }

    public String getBehavioralAnswer() { return behavioralAnswer; }
    public void setBehavioralAnswer(String behavioralAnswer) { this.behavioralAnswer = behavioralAnswer; }

    public Integer getUsedHints() { return usedHints; }
    public void setUsedHints(Integer usedHints) { this.usedHints = usedHints; }
}
