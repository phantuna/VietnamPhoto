package com.example.backend.repository.comment;

import com.example.backend.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentRepositoryCustom {
    Page<Comment> findCommentsByPostIdWithDetails(String postId, Pageable pageable);
}
