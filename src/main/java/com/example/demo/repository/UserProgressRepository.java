package com.example.demo.repository;

import com.example.demo.entity.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {

    // ✅ Fixed method name
    List<UserProgress> findByUserIdOrderBySolvedAtDesc(Long userId);

    Optional<UserProgress> findByUserIdAndProblemId(Long userId, Integer problemId);

    boolean existsByUserIdAndProblemId(Long userId, Integer problemId);

    long countByUserId(Long userId);

    long countByUserIdAndDifficulty(Long userId, String difficulty);

    @Transactional
    void deleteByUserIdAndProblemId(Long userId, Integer problemId);
}