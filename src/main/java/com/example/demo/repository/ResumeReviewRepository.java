package com.example.demo.repository;

import com.example.demo.entity.ResumeReview;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ResumeReviewRepository extends JpaRepository<ResumeReview, Long> {
    Optional<ResumeReview> findTopByUserIdOrderByCreatedAtDesc(Long userId);
}