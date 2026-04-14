package com.example.backend.repository.post;

import com.example.backend.entity.Posts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PostsRepository extends JpaRepository<Posts, String>,PostsRepositoryCustom {
    @Query(value = "SELECT p FROM Posts p " +
            "JOIN FETCH p.user " +
            "JOIN FETCH p.location",
            countQuery = "SELECT count(p) FROM Posts p")
    Page<Posts> findAllPostsWithDetails(Pageable pageable);

    @Query("SELECT p FROM Posts p " +
            "JOIN FETCH p.user " +
            "JOIN FETCH p.location " +
            "WHERE p.id = :id")
    Optional<Posts> findByIdWithDetails(@Param("id") String id);


}
