package com.example.demo.repository;

import com.example.demo.entity.GenerationQueueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface GenerationQueueRepository extends JpaRepository<GenerationQueueEntity, UUID> {
    
    List<GenerationQueueEntity> findByStatusOrderByCreatedAtAsc(String status);

    List<GenerationQueueEntity> findByStatusOrderByCreatedAtAsc(String status, org.springframework.data.domain.Pageable pageable);

    @Query("SELECT COUNT(q) FROM GenerationQueueEntity q WHERE q.status = 'waiting' AND q.createdAt < :createdAt")
    int countWaitingJobsBefore(@Param("createdAt") LocalDateTime createdAt);
    
    @Query("SELECT COUNT(q) FROM GenerationQueueEntity q WHERE q.status = 'processing'")
    int countCurrentlyProcessing();

    @Query("SELECT COUNT(q) FROM GenerationQueueEntity q WHERE q.userId = :userId AND q.status IN ('waiting', 'processing')")
    long countActiveJobsForUser(@Param("userId") Long userId);
}
