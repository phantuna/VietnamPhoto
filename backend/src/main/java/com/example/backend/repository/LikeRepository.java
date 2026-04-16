package com.example.backend.repository;

import com.example.backend.entity.Likes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Likes, String> {

    Optional<Likes> findByUserIdAndPostId(String userId, String postId);

    long countByPostId(String postId);

    boolean existsByUserIdAndPostId(String userId, String postId);
}