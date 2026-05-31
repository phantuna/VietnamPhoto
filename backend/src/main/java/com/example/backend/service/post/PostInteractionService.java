package com.example.backend.service.post;

import com.example.backend.dto.request.post.RatePostRequest;
import com.example.backend.dto.request.post.ReportPostRequest;

public interface PostInteractionService {
    void ratePost(String postId, String userId, RatePostRequest request);
    void reportPost(String postId, String userId, ReportPostRequest request);
}
