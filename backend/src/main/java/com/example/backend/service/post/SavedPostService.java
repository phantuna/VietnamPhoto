package com.example.backend.service.post;

import com.example.backend.dto.response.post.PostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SavedPostService {
    /** Toggle save status of a post */
    boolean toggleSavePost(String userId, String postId);
    
    /** Get all saved posts of a user */
    Page<PostResponse> getSavedPosts(String userId, int page, int size);
    
    /** Check if a post is saved by a user */
    boolean isSaved(String userId, String postId);
}
