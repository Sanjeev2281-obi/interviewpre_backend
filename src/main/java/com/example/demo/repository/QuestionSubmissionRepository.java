package com.example.demo.repository;

import com.example.demo.entity.QuestionSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuestionSubmissionRepository extends JpaRepository<QuestionSubmission, UUID> {
    List<QuestionSubmission> findByQuestionSessionId(UUID sessionId);
    Optional<QuestionSubmission> findByQuestionIdAndUserId(UUID questionId, Long userId);
}
