package com.example.demo.repository;
 
import com.example.demo.entity.DiscussionLike;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
 
public interface DiscussionLikeRepository extends JpaRepository<DiscussionLike, Long> {
    Optional<DiscussionLike> findByDiscussionIdAndUserId(Long discussionId, Long userId);
    boolean existsByDiscussionIdAndUserId(Long discussionId, Long userId);
}
 