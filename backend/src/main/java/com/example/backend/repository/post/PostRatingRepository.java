package com.example.backend.repository.post;

import com.example.backend.entity.PostRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

@Repository
public interface PostRatingRepository extends JpaRepository<PostRating, String> {
    Optional<PostRating> findByPostIdAndUserId(String postId, String userId);

    @Query("SELECT COALESCE(AVG(pr.ratingValue), 0.0) FROM PostRating pr WHERE pr.post.id = :postId")
    Float getAverageRatingByPostId(@Param("postId") String postId);

    @Query("SELECT COUNT(pr) FROM PostRating pr WHERE pr.post.id = :postId")
    Integer countRatingsByPostId(@Param("postId") String postId);
}
