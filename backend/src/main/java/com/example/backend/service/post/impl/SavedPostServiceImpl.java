package com.example.backend.service.post.impl;

import com.example.backend.dto.response.post.PostResponse;
import com.example.backend.entity.Posts;
import com.example.backend.entity.SavedPost;
import com.example.backend.entity.Users;
import com.example.backend.exception.AppException;
import com.example.backend.exception.ErrorCode;
import com.example.backend.mapper.PostMapper;
import com.example.backend.repository.post.PostsRepository;
import com.example.backend.repository.post.SavedPostRepository;
import com.example.backend.repository.user.UserRepository;
import com.example.backend.repository.post.LikeRepository;
import com.example.backend.service.post.SavedPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SavedPostServiceImpl implements SavedPostService {

    private final SavedPostRepository savedPostRepository;
    private final PostsRepository postsRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final PostMapper postMapper;

    @Override
    @Transactional
    public boolean toggleSavePost(String userId, String postId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Posts post = postsRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Optional<SavedPost> existing = savedPostRepository.findByUserIdAndPostId(userId, postId);

        if (existing.isPresent()) {
            SavedPost savedPost = existing.get();
            if (savedPost.getDeleted() == 0) {
                savedPost.setDeleted(1);
                savedPostRepository.save(savedPost);
                return false; // Unsaved
            } else {
                savedPost.setDeleted(0);
                savedPostRepository.save(savedPost);
                return true; // Re-saved
            }
        } else {
            SavedPost newSaved = new SavedPost();
            newSaved.setUser(user);
            newSaved.setPost(post);
            newSaved.setDeleted(0);
            savedPostRepository.save(newSaved);
            return true; // Saved
        }
    }

    @Override
    public List<PostResponse> getSavedPosts(String userId) {
        List<SavedPost> savedPosts = savedPostRepository.findAllByUserId(userId);
        return savedPosts.stream()
                .map(s -> {
                    Posts post = s.getPost();
                    boolean liked = likeRepository.existsByUserIdAndPostId(userId, post.getId());
                    return postMapper.toResponse(post, liked, true);
                })
                .toList();
    }

    @Override
    public boolean isSaved(String userId, String postId) {
        return savedPostRepository.existsByUserIdAndPostIdAndDeleted(userId, postId, 0);
    }
}
