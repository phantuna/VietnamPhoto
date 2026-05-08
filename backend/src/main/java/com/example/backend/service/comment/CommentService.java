package com.example.backend.service.comment;

import com.example.backend.dto.request.CommentRequest;
import com.example.backend.dto.response.CommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {
    CommentResponse createComment(CommentRequest request, String userId);
    Page<CommentResponse> getCommentsByPostId(String postId, Pageable pageable);
    void deleteComment(String commentId, String userId);
    CommentResponse updateComment(String commentId, String newContent, String userId);
}
