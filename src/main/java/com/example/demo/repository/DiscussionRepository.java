package com.example.demo.repository;
 
import com.example.demo.entity.Discussion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
 
public interface DiscussionRepository extends JpaRepository<Discussion, Long> {
    List<Discussion> findAllByOrderByCreatedAtDesc();
    List<Discussion> findByTagOrderByCreatedAtDesc(String tag);
}
