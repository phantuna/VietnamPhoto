package com.example.backend.repository.post;

import com.example.backend.entity.Posts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface PostsRepositoryCustom {
    Page<Posts> findAllPostsWithDetails(Pageable pageable);
    Optional<Posts> findByIdWithDetails(String id);
    Page<Posts> findAllPostsIncludeDeleted(Pageable pageable);
}
