package com.example.demo.repository;
 
import com.example.demo.entity.DiscussionComment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
 
public interface DiscussionCommentRepository extends JpaRepository<DiscussionComment, Long> {
    List<DiscussionComment> findByDiscussionIdOrderByCreatedAtAsc(Long discussionId);
}
 