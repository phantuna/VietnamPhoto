package com.example.backend.repository.post;

import com.example.backend.entity.SavedPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SavedPostRepository extends JpaRepository<SavedPost, String> {
    Optional<SavedPost> findByUserIdAndPostId(String userId, String postId);
    
    @Query("SELECT s FROM SavedPost s WHERE s.user.id = :userId AND s.deleted = 0 ORDER BY s.createdDate DESC")
    List<SavedPost> findAllByUserId(@Param("userId") String userId);
    
    boolean existsByUserIdAndPostIdAndDeleted(String userId, String postId, Integer deleted);
}
