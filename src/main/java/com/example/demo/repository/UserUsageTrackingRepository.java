package com.example.demo.repository;

import com.example.demo.entity.UserUsageTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserUsageTrackingRepository extends JpaRepository<UserUsageTracking, UUID> {
    Optional<UserUsageTracking> findByUserIdAndWeekStart(Long userId, LocalDate weekStart);
}
