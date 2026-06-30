package com.example.backend.controller.post;

import com.example.backend.dto.request.post.RatePostRequest;
import com.example.backend.dto.request.post.ReportPostRequest;
import com.example.backend.service.post.PostInteractionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.example.backend.dto.response.ApiResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostInteractionController {

    private final PostInteractionService postInteractionService;

    @PostMapping("/{id}/rate")
    public ApiResponse<String> ratePost(
            @PathVariable("id") String postId,
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody RatePostRequest request) {
        
        postInteractionService.ratePost(postId, userId, request);
        ApiResponse<String> response = new ApiResponse<>();
        response.setResult("Đánh giá sao thành công");
        return response;
    }

    @PostMapping("/{id}/report")
    public ApiResponse<String> reportPost(
            @PathVariable("id") String postId,
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody ReportPostRequest request) {
        
        postInteractionService.reportPost(postId, userId, request);
        ApiResponse<String> response = new ApiResponse<>();
        response.setResult("Báo cáo bài viết thành công. Quản trị viên sẽ xem xét.");
        return response;
    }
}
