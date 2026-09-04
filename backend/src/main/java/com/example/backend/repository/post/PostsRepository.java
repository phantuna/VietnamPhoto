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
    @Query("SELECT COUNT(p) FROM Posts p WHERE p.user.id = :userId AND FUNCTION('DATE', p.createdDate) = :date")
    long countByUserIdAndCreatedDate(@Param("userId") String userId, @Param("date") java.time.LocalDate date);
    
    @Query("SELECT COUNT(p) FROM Posts p WHERE FUNCTION('DATE', p.createdDate) = :date")
    long countByCreatedDate(@Param("date") java.time.LocalDate date);
    
    @Query(value = "SELECT p FROM Posts p LEFT JOIN FETCH p.user LEFT JOIN FETCH p.location WHERE p.location.id = :locationId AND p.deleted = 0 AND (p.status IS NULL OR p.status = com.example.backend.enums.PostStatus.ACTIVE)", 
           countQuery = "SELECT count(p) FROM Posts p WHERE p.location.id = :locationId AND p.deleted = 0 AND (p.status IS NULL OR p.status = com.example.backend.enums.PostStatus.ACTIVE)")
    Page<Posts> findActivePostsByLocationIdWithDetails(@Param("locationId") String locationId, Pageable pageable);

    @Query(value = "SELECT p FROM Posts p LEFT JOIN FETCH p.user LEFT JOIN FETCH p.location WHERE p.location.id IN :locationIds AND p.deleted = 0 AND (p.status IS NULL OR p.status = com.example.backend.enums.PostStatus.ACTIVE)", 
           countQuery = "SELECT count(p) FROM Posts p WHERE p.location.id IN :locationIds AND p.deleted = 0 AND (p.status IS NULL OR p.status = com.example.backend.enums.PostStatus.ACTIVE)")
    Page<Posts> findActivePostsByLocationIdsWithDetails(@Param("locationIds") java.util.List<String> locationIds, Pageable pageable);
    @Query(value = "SELECT p FROM Posts p LEFT JOIN FETCH p.user LEFT JOIN FETCH p.location WHERE p.status = com.example.backend.enums.PostStatus.PENDING_REVIEW AND p.deleted = 0",
           countQuery = "SELECT count(p) FROM Posts p WHERE p.status = com.example.backend.enums.PostStatus.PENDING_REVIEW AND p.deleted = 0")
    Page<Posts> findPendingPosts(Pageable pageable);


    @Modifying
    @Transactional
    @Query(value = "UPDATE posts SET deleted = :deleted WHERE id = :postId", nativeQuery = true)
    void togglePostStatus(@Param("postId") String postId, @Param("deleted") int deleted);

}
