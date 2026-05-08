package com.example.backend.service.notification;

import com.example.backend.dto.response.NotificationResponse;
import com.example.backend.entity.Notification;
import com.example.backend.entity.Posts;
import com.example.backend.entity.Users;
import com.example.backend.enums.NotificationType;
import com.example.backend.mapper.NotificationMapper;
import com.example.backend.repository.NotificationRepository;
import com.example.backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;

    @Transactional
    public NotificationResponse createPostLikedNotification(Users actor, Posts post) {
        Users receiver = post.getUser();

        if (receiver.getId().equals(actor.getId())) {
            return null;
        }

        Notification notification = Notification.builder()
                .receiver(receiver)
                .actor(actor)
                .post(post)
                .type(NotificationType.POST_LIKED)
                .content(actor.getUsername() + " đã thích bài viết của bạn")
                .isRead(false)
                .build();

        Notification saved = notificationRepository.save(notification);

        userRepository.increaseUnreadNotificationCount(receiver.getId());

        Long currentCount = receiver.getUnreadNotificationCount();
        Long unreadCount = (currentCount != null ? currentCount : 0L) + 1;

        return notificationMapper.toResponse(saved, unreadCount);
    }

    @Transactional
    public NotificationResponse createPostCommentedNotification(Users actor, Posts post, String commentContent) {
        Users receiver = post.getUser();

        if (receiver.getId().equals(actor.getId())) {
            return null;
        }

        Notification notification = Notification.builder()
                .receiver(receiver)
                .actor(actor)
                .post(post)
                .type(NotificationType.POST_COMMENTED)
                .content(actor.getUsername() + " đã bình luận: " + commentContent)
                .isRead(false)
                .build();

        Notification saved = notificationRepository.save(notification);

        userRepository.increaseUnreadNotificationCount(receiver.getId());

        Long currentCount = receiver.getUnreadNotificationCount();
        Long unreadCount = (currentCount != null ? currentCount : 0L) + 1;

        return notificationMapper.toResponse(saved, unreadCount);
    }

    public Page<NotificationResponse> getMyNotifications(
            String userId,
            Pageable pageable
    ) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return notificationRepository
                .findByReceiverIdAndDeletedOrderByCreatedAtTimeDesc(userId, 0, pageable)
                .map(n -> {
                    Long count = user.getUnreadNotificationCount();
                    return notificationMapper.toResponse(n, count != null ? count : 0L);
                });
    }

    public Long getUnreadCount(String userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long count = user.getUnreadNotificationCount();
        return count != null ? count : 0L;
    }

    @Transactional
    public void markAsRead(String userId, String notificationId) {
        int updated = notificationRepository.markAsRead(notificationId, userId);

        if (updated > 0) {
            userRepository.decreaseUnreadNotificationCount(userId);
        }
    }

    @Transactional
    public void markAllAsRead(String userId) {
        notificationRepository.markAllAsRead(userId);
        userRepository.resetUnreadNotificationCount(userId);
    }
}
