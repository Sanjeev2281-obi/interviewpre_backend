package com.example.demo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public class EvaluateMockRequest {

    @NotNull(message = "Session ID is required")
    private UUID sessionId;

    @NotEmpty(message = "Submissions list cannot be empty")
    @Valid
    private List<SubmissionDto> submissions;

    public EvaluateMockRequest() {}

    public EvaluateMockRequest(UUID sessionId, List<SubmissionDto> submissions) {
        this.sessionId = sessionId;
        this.submissions = submissions;
    }

    public UUID getSessionId() { return sessionId; }
    public void setSessionId(UUID sessionId) { this.sessionId = sessionId; }

    public List<SubmissionDto> getSubmissions() { return submissions; }
    public void setSubmissions(List<SubmissionDto> submissions) { this.submissions = submissions; }
}
