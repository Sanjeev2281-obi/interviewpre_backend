// Add to any existing @Service class, or create a new one
// src/main/java/com/example/demo/service/DbKeepAliveService.java

package com.example.demo.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class DbKeepAliveService {

    private final JdbcTemplate jdbcTemplate;

    public DbKeepAliveService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Runs every 4 minutes (240,000 ms)
    @Scheduled(fixedRate = 240000)
    public void keepAlive() {
        jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        System.out.println("DB keep-alive ping sent");
    }
}