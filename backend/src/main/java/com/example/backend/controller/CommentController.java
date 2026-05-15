package com.example.backend.controller;

import com.example.backend.dto.request.CommentRequest;
import com.example.backend.dto.response.CommentResponse;
import com.example.backend.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public CommentResponse createComment(
            @AuthenticationPrincipal String userId,
            @RequestBody CommentRequest request
    ) {
        return commentService.createComment(request, userId);
    }

    @GetMapping("/post/{postId}")
    public Page<CommentResponse> getCommentsByPostId(
            @PathVariable String postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.ASC, "createdDate")
                        .and(Sort.by(Sort.Direction.ASC, "id"))
        );

        return commentService.getCommentsByPostId(postId, pageable);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public CommentResponse updateComment(
            @PathVariable String id,
            @AuthenticationPrincipal String userId,
            @RequestBody CommentRequest request
    ) {
        return commentService.updateComment(id, request.getContent(), userId);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public void deleteComment(
            @PathVariable String id,
            @AuthenticationPrincipal String userId
    ) {
        commentService.deleteComment(id, userId);
    }
}