package com.example.backend.controller.post;

import com.example.backend.dto.response.post.PostResponse;
import com.example.backend.service.post.SavedPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/saved")
@RequiredArgsConstructor
public class SavedPostController {

    private final SavedPostService savedPostService;

    @PostMapping("/{postId}")
    public boolean toggleSave(@PathVariable String postId, @RequestParam String userId) {
        return savedPostService.toggleSavePost(userId, postId);
    }

    @GetMapping
    public org.springframework.data.domain.Page<PostResponse> getSavedPosts(
            @RequestParam String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return savedPostService.getSavedPosts(userId, page, size);
    }

    @GetMapping("/status/{postId}")
    public boolean getSaveStatus(@PathVariable String postId, @RequestParam String userId) {
        return savedPostService.isSaved(userId, postId);
    }
}
