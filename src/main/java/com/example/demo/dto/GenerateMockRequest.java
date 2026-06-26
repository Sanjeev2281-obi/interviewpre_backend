package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class GenerateMockRequest {

    @NotBlank(message = "Target company is required")
    private String company = "Any";

    @NotEmpty(message = "At least one focus topic must be selected")
    private List<String> topics;

    @NotBlank(message = "Experience level is required")
    private String level = "1-3yr";

    @NotBlank(message = "Interview type is required")
    private String type = "Mixed"; // DSA Only, Mixed, Behavioral

    public GenerateMockRequest() {}

    public GenerateMockRequest(String company, List<String> topics, String level, String type) {
        this.company = company;
        this.topics = topics;
        this.level = level;
        this.type = type;
    }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public List<String> getTopics() { return topics; }
    public void setTopics(List<String> topics) { this.topics = topics; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
