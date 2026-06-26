package com.example.demo.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "interview_questions")
public class InterviewQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private MockSession session;

    @Column(name = "question_type", nullable = false)
    private String questionType; // dsa, behavioral

    @Column(name = "difficulty")
    private String difficulty; // easy, medium, hard

    @Column(name = "topic")
    private String topic;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "expected_complexity")
    private String expectedComplexity; // e.g. time: O(n), space: O(1)

    @Column(name = "company")
    private String company;

    @Column(name = "example", columnDefinition = "TEXT")
    private String example;

    @Column(name = "constraints_json", columnDefinition = "TEXT")
    private String constraintsJson;

    @Column(name = "hints_json", columnDefinition = "TEXT")
    private String hintsJson;

    @Column(name = "test_cases_json", columnDefinition = "TEXT")
    private String testCasesJson;

    @Column(name = "starter_code_json", columnDefinition = "TEXT")
    private String starterCodeJson;

    @Column(name = "star_method_json", columnDefinition = "TEXT")
    private String starMethodJson;

    @Column(name = "rubric_json", columnDefinition = "TEXT")
    private String rubricJson;

    public InterviewQuestion() {}

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public MockSession getSession() { return session; }
    public void setSession(MockSession session) { this.session = session; }

    public String getQuestionType() { return questionType; }
    public void setQuestionType(String questionType) { this.questionType = questionType; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getExpectedComplexity() { return expectedComplexity; }
    public void setExpectedComplexity(String expectedComplexity) { this.expectedComplexity = expectedComplexity; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getExample() { return example; }
    public void setExample(String example) { this.example = example; }

    public String getConstraintsJson() { return constraintsJson; }
    public void setConstraintsJson(String constraintsJson) { this.constraintsJson = constraintsJson; }

    public String getHintsJson() { return hintsJson; }
    public void setHintsJson(String hintsJson) { this.hintsJson = hintsJson; }

    public String getTestCasesJson() { return testCasesJson; }
    public void setTestCasesJson(String testCasesJson) { this.testCasesJson = testCasesJson; }

    public String getStarterCodeJson() { return starterCodeJson; }
    public void setStarterCodeJson(String starterCodeJson) { this.starterCodeJson = starterCodeJson; }

    public String getStarMethodJson() { return starMethodJson; }
    public void setStarMethodJson(String starMethodJson) { this.starMethodJson = starMethodJson; }

    public String getRubricJson() { return rubricJson; }
    public void setRubricJson(String rubricJson) { this.rubricJson = rubricJson; }
}
