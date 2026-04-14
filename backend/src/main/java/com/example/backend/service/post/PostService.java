package com.example.backend.service.post;

import com.example.backend.dto.request.PostCreateRequest;
import com.example.backend.entity.Posts;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface PostService {
    Posts createPost(String userId, PostCreateRequest request);
}