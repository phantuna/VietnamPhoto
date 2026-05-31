package com.example.backend.controller.post;

import com.example.backend.dto.request.post.RatePostRequest;
import com.example.backend.dto.request.post.ReportPostRequest;
import com.example.backend.service.post.PostInteractionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostInteractionController {

    private final PostInteractionService postInteractionService;

    @PostMapping("/{id}/rate")
    public ResponseEntity<?> ratePost(
            @PathVariable("id") String postId,
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody RatePostRequest request) {
        
        postInteractionService.ratePost(postId, userId, request);
        return ResponseEntity.ok().body("Đánh giá sao thành công");
    }

    @PostMapping("/{id}/report")
    public ResponseEntity<?> reportPost(
            @PathVariable("id") String postId,
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody ReportPostRequest request) {
        
        postInteractionService.reportPost(postId, userId, request);
        return ResponseEntity.ok().body("Báo cáo bài viết thành công. Quản trị viên sẽ xem xét.");
    }
}
