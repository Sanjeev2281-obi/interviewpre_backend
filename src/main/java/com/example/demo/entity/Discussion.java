package com.example.demo.entity;

 
import jakarta.persistence.*;
import java.time.LocalDateTime;
 
@Entity
@Table(name = "discussions")
public class Discussion {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @Column(nullable = false)
    private Long userId;
 
    @Column(nullable = false)
    private String authorName;
 
    @Column
    private String company;
 
    @Column(nullable = false)
    private String tag;
 
    @Column(nullable = false, length = 300)
    private String title;
 
    @Column(nullable = false, length = 5000)
    private String body;
 
    @Column
    private int likes = 0;
 
    @Column
    private int commentCount = 0;
 
    @Column
    private LocalDateTime createdAt = LocalDateTime.now();
 
    public Discussion() {}
 
    public Discussion(Long userId, String authorName, String company, String tag, String title, String body) {
        this.userId = userId;
        this.authorName = authorName;
        this.company = company;
        this.tag = tag;
        this.title = title;
        this.body = body;
    }
 
    // Getters & Setters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getAuthorName() { return authorName; }
    public String getCompany() { return company; }
    public String getTag() { return tag; }
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public int getLikes() { return likes; }
    public int getCommentCount() { return commentCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
 
    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public void setCompany(String company) { this.company = company; }
    public void setTag(String tag) { this.tag = tag; }
    public void setTitle(String title) { this.title = title; }
    public void setBody(String body) { this.body = body; }
    public void setLikes(int likes) { this.likes = likes; }
    public void setCommentCount(int commentCount) { this.commentCount = commentCount; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
 