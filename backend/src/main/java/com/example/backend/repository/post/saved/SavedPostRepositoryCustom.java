package com.example.backend.repository.post.saved;

import com.example.backend.entity.SavedPost;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SavedPostRepositoryCustom {
    Page<SavedPost> findAllSavedPostsWithDetailsByUserId(String userId, Pageable pageable);
}
