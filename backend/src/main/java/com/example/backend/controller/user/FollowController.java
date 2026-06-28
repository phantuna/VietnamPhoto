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

    @PostMapping("/{followingId}")
    public ResponseEntity<FollowStatusResponse> toggleFollow(
            @PathVariable String followingId,
            @RequestParam String followerId
    ) {
        return ResponseEntity.ok(followService.toggleFollow(followerId, followingId));
    }

    @GetMapping("/status")
    public ResponseEntity<FollowStatusResponse> checkFollowStatus(
            @RequestParam String followerId,
            @RequestParam String followingId
    ) {
        return ResponseEntity.ok(followService.getFollowStatus(followerId, followingId));
    }

    @GetMapping("/counts/{userId}")
    public ResponseEntity<FollowStatusResponse> getCounts(@PathVariable String userId) {
        return ResponseEntity.ok(FollowStatusResponse.builder()
                .followersCount(followService.countFollowers(userId))
                .followingCount(followService.countFollowing(userId))
                .build());
    }

    @GetMapping("/mutual")
    public ResponseEntity<List<String>> getMutualFollowUserIds(@RequestParam String userId) {
        return ResponseEntity.ok(followService.getMutualFollowUserIds(userId));
    }

    @GetMapping("/following-ids/{userId}")
    public ResponseEntity<List<String>> getFollowingUserIds(@PathVariable String userId) {
        return ResponseEntity.ok(followService.getFollowingUserIds(userId));
    }
}
