package com.example.backend.service.post;

import com.example.backend.dto.response.post.PostResponse;
import java.util.List;

public interface SavedPostService {
    /** Toggle save status of a post */
    boolean toggleSavePost(String userId, String postId);
    
    /** Get all saved posts of a user */
    List<PostResponse> getSavedPosts(String userId);
    
    /** Check if a post is saved by a user */
    boolean isSaved(String userId, String postId);
}
