package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private String role;

    @Column(name = "is_pro")
    private Boolean isPro = false;

    @Column(name = "pro_since")
    private LocalDateTime proSince;

    // Constructors
    public User() {}

    public User(Long id, String name, String email, String password, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.isPro = false;
    }

    public User(Long id, String name, String email, String password, String role, Boolean isPro, LocalDateTime proSince) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.isPro = isPro != null ? isPro : false;
        this.proSince = proSince;
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public Boolean getIsPro() { return isPro; }
    public LocalDateTime getProSince() { return proSince; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
    public void setIsPro(Boolean isPro) { this.isPro = isPro; }
    public void setProSince(LocalDateTime proSince) { this.proSince = proSince; }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String name;
        private String email;
        private String password;
        private String role;
        private Boolean isPro = false;
        private LocalDateTime proSince;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder password(String password) { this.password = password; return this; }
        public Builder role(String role) { this.role = role; return this; }
        public Builder isPro(Boolean isPro) { this.isPro = isPro; return this; }
        public Builder proSince(LocalDateTime proSince) { this.proSince = proSince; return this; }

        public User build() {
            return new User(id, name, email, password, role, isPro, proSince);
        }
    }
}