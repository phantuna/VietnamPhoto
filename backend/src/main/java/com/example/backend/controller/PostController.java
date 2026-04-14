package com.example.backend.controller;

import com.example.backend.dto.request.post.PostCreateRequest;
import com.example.backend.dto.request.post.PostUpdateRequest;
import com.example.backend.dto.response.PostResponse;
import com.example.backend.service.post.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/created")
    @PreAuthorize("isAuthenticated()")
    public PostResponse createPost(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody PostCreateRequest request) {
        return postService.createPost(userId, request);
    }

    @PutMapping("/updated/{id}")
    @PreAuthorize("isAuthenticated()")
    public PostResponse updatePost(
            @NotBlank @PathVariable String id,
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody PostUpdateRequest request) {
        return postService.updatePost(id, userId, request);
    }

    // 🌟 ĐÃ SỬA LẠI THÀNH TRẢ VỀ LIST CƠ BẢN
    @GetMapping("/getAll")
    @PreAuthorize("isAuthenticated()")
    public List<PostResponse> getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping("/getById/{id}")
    @PreAuthorize("isAuthenticated()")
    public PostResponse getPostById(@NotBlank @PathVariable String id) {
        return postService.getPostById(id);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public void deletePost(
            @NotBlank @PathVariable String id,
            @AuthenticationPrincipal String userId) {
        postService.deletePost(id, userId);
    }
}