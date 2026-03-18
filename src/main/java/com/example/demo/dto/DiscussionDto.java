package com.example.demo.dto;
 
import java.time.LocalDateTime;
import java.util.List;
 
public class DiscussionDto {
    private Long id;
    private String author;
    private String company;
    private String avatar;
    private String avatarBg;
    private String tag;
    private String title;
    private String body;
    private int likes;
    private int comments;
    private boolean liked;
    private String createdAt;
    private List<CommentDto> commentList;
 
    public static class CommentDto {
        private Long id;
        private String author;
        private String avatar;
        private String body;
        private String createdAt;
 
        public CommentDto(Long id, String author, String body, LocalDateTime createdAt) {
            this.id = id;
            this.author = author;
            this.avatar = author != null && author.length() >= 2
                ? author.substring(0, 2).toUpperCase() : "?";
            this.body = body;
            this.createdAt = formatTime(createdAt);
        }
 
        private static String formatTime(LocalDateTime dt) {
            if (dt == null) return "Recently";
            long days = java.time.temporal.ChronoUnit.DAYS.between(dt, LocalDateTime.now());
            if (days == 0) return "Today";
            if (days == 1) return "Yesterday";
            if (days < 7) return days + " days ago";
            return (days / 7) + " weeks ago";
        }
 
        public Long getId() { return id; }
        public String getAuthor() { return author; }
        public String getAvatar() { return avatar; }
        public String getBody() { return body; }
        public String getCreatedAt() { return createdAt; }
    }
 
    // Constructors & getters
    public DiscussionDto() {}
 
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAuthor() { return author; }
    public void setAuthor(String a) { this.author = a; }
    public String getCompany() { return company; }
    public void setCompany(String c) { this.company = c; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String a) { this.avatar = a; }
    public String getAvatarBg() { return avatarBg; }
    public void setAvatarBg(String a) { this.avatarBg = a; }
    public String getTag() { return tag; }
    public void setTag(String t) { this.tag = t; }
    public String getTitle() { return title; }
    public void setTitle(String t) { this.title = t; }
    public String getBody() { return body; }
    public void setBody(String b) { this.body = b; }
    public int getLikes() { return likes; }
    public void setLikes(int l) { this.likes = l; }
    public int getComments() { return comments; }
    public void setComments(int c) { this.comments = c; }
    public boolean isLiked() { return liked; }
    public void setLiked(boolean l) { this.liked = l; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String c) { this.createdAt = c; }
    public List<CommentDto> getCommentList() { return commentList; }
    public void setCommentList(List<CommentDto> cl) { this.commentList = cl; }
}
 
 