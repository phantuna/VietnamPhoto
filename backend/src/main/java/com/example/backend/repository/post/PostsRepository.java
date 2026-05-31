package com.example.backend.repository.post;

import com.example.backend.entity.Posts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import java.time.LocalDateTime;

public interface PostsRepository extends JpaRepository<Posts, String>,PostsRepositoryCustom {
    long countByUserIdAndCreatedDate(String userId, java.time.LocalDate date);
    
    long countByCreatedDate(java.time.LocalDate date);

    @Query(value = "SELECT p FROM Posts p " +
            "JOIN FETCH p.user " +
            "JOIN FETCH p.location " +
            "WHERE p.deleted = 0 AND (p.status IS NULL OR p.status = 'ACTIVE')",
            countQuery = "SELECT count(p) FROM Posts p WHERE p.deleted = 0 AND (p.status IS NULL OR p.status = 'ACTIVE')")
    Page<Posts> findAllPostsWithDetails(Pageable pageable);

    @Query("SELECT p FROM Posts p " +
            "JOIN FETCH p.user " +
            "JOIN FETCH p.location " +
            "WHERE p.id = :id AND p.deleted = 0 AND (p.status IS NULL OR p.status = 'ACTIVE')")
    Optional<Posts> findByIdWithDetails(@Param("id") String id);

    @Query(value = "SELECT p.* FROM posts p JOIN (SELECT r.post_id, count(r.id) as report_count FROM reports r GROUP BY r.post_id) temp ON temp.post_id = p.id ORDER BY temp.report_count DESC", 
           countQuery = "SELECT count(DISTINCT p.id) FROM posts p JOIN reports r ON r.post_id = p.id", 
           nativeQuery = true)
    org.springframework.data.domain.Page<Posts> findAllPostsIncludeDeleted(org.springframework.data.domain.Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "UPDATE posts SET deleted = :deleted WHERE id = :postId", nativeQuery = true)
    void togglePostStatus(@Param("postId") String postId, @Param("deleted") int deleted);


}
