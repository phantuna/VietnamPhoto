package com.example.backend.repository.post;

import com.example.backend.entity.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Likes, String> {

    Optional<Likes> findByUserIdAndPostId(String userId, String postId);

    long countByPostId(String postId);

    boolean existsByUserIdAndPostId(String userId, String postId);

    @Query("SELECT l.post.id FROM Likes l WHERE l.user.id = :userId AND l.post.id IN :postIds")
    List<String> findLikedPostIdsByUserIdAndPostIdsIn(@Param("userId") String userId, @Param("postIds") Collection<String> postIds);
}