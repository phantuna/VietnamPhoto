package com.example.backend.service.post;

import com.example.backend.dto.response.LikeToggleResponse;
import com.example.backend.entity.Likes;
import com.example.backend.entity.Posts;
import com.example.backend.entity.Users;
import com.example.backend.repository.LikeRepository;
import com.example.backend.repository.post.PostsRepository;
import com.example.backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết"));

        long currentLikes = post.getLikeCount() != null ? post.getLikeCount() : 0L;

        return likeRepository.findByUserIdAndPostId(userId, postId)
                .map(existingLike -> {
                    likeRepository.delete(existingLike);

                    long newTotal = Math.max(0, currentLikes - 1);
                    post.setLikeCount(newTotal);
                    postsRepository.save(post);

                    return new LikeToggleResponse(false, newTotal);
                })
                .orElseGet(() -> {
                    Users user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

                    Likes like = new Likes();
                    like.setUser(user);
                    like.setPost(post);
                    likeRepository.save(like);

                    long newTotal = currentLikes + 1;
                    post.setLikeCount(newTotal);
                    postsRepository.save(post);

                    com.example.backend.dto.response.NotificationResponse notification = notificationService.createPostLikedNotification(user, post);
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
}