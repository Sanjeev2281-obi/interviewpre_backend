package com.example.demo.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "session_evaluations")
public class SessionEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private MockSession session;

    @Column(name = "overall_score")
    private Integer overallScore;

    @Column(name = "grade")
    private String grade;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @Column(name = "strengths_json", columnDefinition = "TEXT")
    private String strengthsJson;

    @Column(name = "improvements_json", columnDefinition = "TEXT")
    private String improvementsJson;

    @Column(name = "hiring_decision")
    private String hiringDecision;

    @Column(name = "next_steps_json", columnDefinition = "TEXT")
    private String nextStepsJson;

    public SessionEvaluation() {}

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public MockSession getSession() { return session; }
    public void setSession(MockSession session) { this.session = session; }

    public Integer getOverallScore() { return overallScore; }
    public void setOverallScore(Integer overallScore) { this.overallScore = overallScore; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getStrengthsJson() { return strengthsJson; }
    public void setStrengthsJson(String strengthsJson) { this.strengthsJson = strengthsJson; }

    public String getImprovementsJson() { return improvementsJson; }
    public void setImprovementsJson(String improvementsJson) { this.improvementsJson = improvementsJson; }

    public String getHiringDecision() { return hiringDecision; }
    public void setHiringDecision(String hiringDecision) { this.hiringDecision = hiringDecision; }

    public String getNextStepsJson() { return nextStepsJson; }
    public void setNextStepsJson(String nextStepsJson) { this.nextStepsJson = nextStepsJson; }
}
