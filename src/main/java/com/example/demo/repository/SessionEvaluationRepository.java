package com.example.demo.repository;

import com.example.demo.entity.SessionEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessionEvaluationRepository extends JpaRepository<SessionEvaluation, UUID> {
    Optional<SessionEvaluation> findBySessionId(UUID sessionId);
}
