package com.example.backend.service.post;

import com.example.backend.dto.response.post.LikeToggleResponse;
import com.example.backend.dto.response.notification.NotificationResponse;
import com.example.backend.entity.Likes;
import com.example.backend.entity.Posts;
import com.example.backend.entity.Users;
import com.example.backend.exception.AppException;
import com.example.backend.exception.ErrorCode;
import com.example.backend.repository.post.LikeRepository;
import com.example.backend.repository.post.PostsRepository;
import com.example.backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.Collections;
import java.util.Set;
@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final LikeRepository likeRepository;
    private final PostsRepository postsRepository;
    private final UserRepository userRepository;
    private final com.example.backend.service.notification.NotificationService notificationService;
    private final com.example.backend.service.notification.NotificationSseService notificationSseService;

    @Transactional
    public LikeToggleResponse toggleLike(String userId, String postId) {
        Posts post = postsRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        long currentLikes = post.getLikeCount() != null ? post.getLikeCount() : 0L;

        return likeRepository.findByUserIdAndPostId(userId, postId)
                .map(existingLike -> {
                    likeRepository.delete(existingLike);

                    long newTotal = Math.max(0, currentLikes - 1);
                    post.setLikeCount(newTotal);
                    postsRepository.save(post);
                    
                    notificationService.removePostLikedNotification(existingLike.getUser(), post);

                    return new LikeToggleResponse(false, newTotal);
                })
                .orElseGet(() -> {
                    Users user = userRepository.findById(userId)
                            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

                    Likes like = new Likes();
                    like.setUser(user);
                    like.setPost(post);
                    likeRepository.save(like);

                    long newTotal = currentLikes + 1;
                    post.setLikeCount(newTotal);
                    postsRepository.save(post);

                    NotificationResponse notification = notificationService.createPostLikedNotification(user, post);
                    if (notification != null) {
                        notificationSseService.sendToUser(post.getUser().getId(), notification);
                    }

                    return new LikeToggleResponse(true, newTotal);
                });
    }

    @Transactional(readOnly = true)
    public long countLikes(String postId) {
        return likeRepository.countByPostId(postId);
    }

    @Transactional(readOnly = true)
    public boolean isLiked(String userId, String postId) {
        return likeRepository.existsByUserIdAndPostId(userId, postId);
    }

    @Transactional(readOnly = true)
    public Set<String> getLikedPostIds(String userId, Collection<String> postIds) {
        if (userId == null || postIds.isEmpty()) return Collections.emptySet();
        return new HashSet<>(likeRepository.findLikedPostIdsByUserIdAndPostIdsIn(userId, postIds));
    }
}