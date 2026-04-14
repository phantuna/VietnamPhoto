package com.example.backend.controller;

import com.example.backend.dto.request.PostCreateRequest;
import com.example.backend.dto.response.PostResponse;
import com.example.backend.entity.Posts;
import com.example.backend.mapper.PostMapper;
import com.example.backend.service.post.impl.PostServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostServiceImpl postService;
    // Cần có 1 Mapper để chuyển từ Posts Entity -> PostResponse
    private final PostMapper postMapper;

    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @AuthenticationPrincipal String userId,
            @RequestBody @Valid PostCreateRequest request
    ) {
        Posts savedPost = postService.createPost(userId, request);

        PostResponse response = postMapper.toResponse(savedPost);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}