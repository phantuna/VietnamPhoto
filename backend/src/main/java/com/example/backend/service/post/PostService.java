package com.example.backend.service.post;

import com.example.backend.dto.request.post.PostCreateRequest;
import com.example.backend.dto.request.post.PostUpdateRequest;
import com.example.backend.dto.response.post.PostResponse;

import java.util.List;

public interface PostService {
    PostResponse createPost(String userId, PostCreateRequest request);
    PostResponse getPostById(String postId, String userId);
    List<PostResponse> getAllPosts(String userId) ;
    PostResponse updatePost(String postId, String userId, PostUpdateRequest request);
    void deletePost(String postId, String userId);
}