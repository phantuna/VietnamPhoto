package com.example.backend.controller.user;

import com.example.backend.dto.response.user.FollowStatusResponse;
import com.example.backend.service.user.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    /**
     * Toggle follow/unfollow
     * POST /api/v1/follow/{followingId}?followerId={followerId}
     */
    @PostMapping("/{followingId}")
    public ResponseEntity<FollowStatusResponse> toggleFollow(
            @PathVariable String followingId,
            @RequestParam String followerId
    ) {
        return ResponseEntity.ok(followService.toggleFollow(followerId, followingId));
    }

    /**
     * Kiểm tra trạng thái follow + lấy số lượng
     * GET /api/v1/follow/status?followerId=...&followingId=...
     */
    @GetMapping("/status")
    public ResponseEntity<FollowStatusResponse> checkFollowStatus(
            @RequestParam String followerId,
            @RequestParam String followingId
    ) {
        return ResponseEntity.ok(followService.getFollowStatus(followerId, followingId));
    }

    /**
     * Lấy số lượng followers/following của 1 user
     * GET /api/v1/follow/counts/{userId}
     */
    @GetMapping("/counts/{userId}")
    public ResponseEntity<FollowStatusResponse> getCounts(@PathVariable String userId) {
        return ResponseEntity.ok(FollowStatusResponse.builder()
                .followersCount(followService.countFollowers(userId))
                .followingCount(followService.countFollowing(userId))
                .build());
    }

    /**
     * Lấy danh sách userId đã follow nhau 2 chiều với mình
     * GET /api/v1/follow/mutual?userId=...
     */
    @GetMapping("/mutual")
    public ResponseEntity<List<String>> getMutualFollowUserIds(@RequestParam String userId) {
        return ResponseEntity.ok(followService.getMutualFollowUserIds(userId));
    }
}
