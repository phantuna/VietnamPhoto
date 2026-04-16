package com.example.backend.service.post;

import com.example.backend.dto.request.post.PostCreateRequest;
import com.example.backend.dto.request.post.PostUpdateRequest;
import com.example.backend.dto.response.PostResponse;
import com.example.backend.entity.Posts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostService {
    PostResponse createPost(String userId, PostCreateRequest request);
    PostResponse getPostById(String postId, String userId);
    List<PostResponse> getAllPosts(String userId) ;
    PostResponse updatePost(String postId, String userId, PostUpdateRequest request);
    void deletePost(String postId, String userId);
}