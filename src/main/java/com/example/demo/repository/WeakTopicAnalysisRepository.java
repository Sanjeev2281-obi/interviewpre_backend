package com.example.demo.repository;

import com.example.demo.entity.WeakTopicAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WeakTopicAnalysisRepository extends JpaRepository<WeakTopicAnalysis, UUID> {
    List<WeakTopicAnalysis> findByUserId(Long userId);
    Optional<WeakTopicAnalysis> findByUserIdAndTopic(Long userId, String topic);
}
