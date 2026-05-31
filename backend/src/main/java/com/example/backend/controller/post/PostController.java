package com.example.backend.controller.post;

import com.example.backend.dto.request.post.PostCreateRequest;
import com.example.backend.dto.request.post.PostUpdateRequest;
import com.example.backend.dto.response.post.LikeToggleResponse;
import com.example.backend.dto.response.post.PostResponse;
import com.example.backend.service.post.PostLikeService;
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
    private final PostLikeService postLikeService;


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

    @GetMapping("/{postId}")
    public PostResponse getPostById(
            @PathVariable String postId,
            @RequestParam(required = false) String viewerId
    ) {
        return postService.getPostById(postId, viewerId);
    }

    @GetMapping("/getAll")
    public List<PostResponse> getAllPosts(
            @RequestParam(required = false) String viewerId
    ) {
        return postService.getAllPosts(viewerId);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public void deletePost(
            @NotBlank @PathVariable String id,
            @AuthenticationPrincipal String userId) {
        postService.deletePost(id, userId);
    }


    @PostMapping("/{postId}/like")
    public LikeToggleResponse toggleLike(
            @PathVariable String postId,
            @RequestParam String userId
    ) {
        return postLikeService.toggleLike(userId, postId);
    }

    @GetMapping("/{postId}/likes/count")
    public long countLikes(@PathVariable String postId) {
        return postLikeService.countLikes(postId);
    }

    @GetMapping("/{postId}/liked")
    public boolean isLiked(
            @PathVariable String postId,
            @RequestParam String userId
    ) {
        return postLikeService.isLiked(userId, postId);
    }
}