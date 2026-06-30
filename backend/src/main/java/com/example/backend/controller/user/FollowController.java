package com.example.backend.controller.user;

import com.example.backend.dto.response.user.FollowStatusResponse;
import com.example.backend.service.user.FollowService;
import lombok.RequiredArgsConstructor;
import com.example.backend.dto.response.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/{followingId}")
    public ApiResponse<FollowStatusResponse> toggleFollow(
            @PathVariable String followingId,
            @RequestParam String followerId
    ) {
        ApiResponse<FollowStatusResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(followService.toggleFollow(followerId, followingId));
        return apiResponse;
    }

    @GetMapping("/status")
    public ApiResponse<FollowStatusResponse> checkFollowStatus(
            @RequestParam String followerId,
            @RequestParam String followingId
    ) {
        ApiResponse<FollowStatusResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(followService.getFollowStatus(followerId, followingId));
        return apiResponse;
    }

    @GetMapping("/counts/{userId}")
    public ApiResponse<FollowStatusResponse> getCounts(@PathVariable String userId) {
        ApiResponse<FollowStatusResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(FollowStatusResponse.builder()
                .followersCount(followService.countFollowers(userId))
                .followingCount(followService.countFollowing(userId))
                .build());
        return apiResponse;
    }

    @GetMapping("/mutual")
    public ApiResponse<List<String>> getMutualFollowUserIds(@RequestParam String userId) {
        ApiResponse<List<String>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(followService.getMutualFollowUserIds(userId));
        return apiResponse;
    }

    @GetMapping("/following-ids/{userId}")
    public ApiResponse<List<String>> getFollowingUserIds(@PathVariable String userId) {
        ApiResponse<List<String>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(followService.getFollowingUserIds(userId));
        return apiResponse;
    }
}
