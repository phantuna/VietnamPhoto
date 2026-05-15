package com.example.backend.event.listener;

import com.example.backend.dto.response.NotificationResponse;
import com.example.backend.entity.Users;
import com.example.backend.event.NewFollowerEvent;
import com.example.backend.event.PostCommentedEvent;
import com.example.backend.event.PostCreatedEvent;
import com.example.backend.service.notification.NotificationService;
import com.example.backend.service.notification.NotificationSseService;
import com.example.backend.service.user.FollowService;
import com.example.backend.repository.post.PostsRepository;
import com.example.backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;
    private final NotificationSseService notificationSseService;
    private final PostsRepository postsRepository;
    private final UserRepository userRepository;
    private final FollowService followService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handlePostCommentedEvent(PostCommentedEvent event) {
        var actor = userRepository.findById(event.getActor().getId()).orElse(null);
        var post = postsRepository.findById(event.getPost().getId()).orElse(null);
        if (actor == null || post == null) return;

        NotificationResponse notification = notificationService.createPostCommentedNotification(
                actor, post, event.getCommentContent());
        if (notification != null) {
            notificationSseService.sendToUser(post.getUser().getId(), notification);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleNewFollowerEvent(NewFollowerEvent event) {
        var follower = userRepository.findById(event.getFollower().getId()).orElse(null);
        var following = userRepository.findById(event.getFollowing().getId()).orElse(null);
        if (follower == null || following == null) return;

        NotificationResponse notification = notificationService.createNewFollowerNotification(follower, following);
        if (notification != null) {
            notificationSseService.sendToUser(following.getId(), notification);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handlePostCreatedEvent(PostCreatedEvent event) {
        var author = userRepository.findById(event.getAuthor().getId()).orElse(null);
        var post = postsRepository.findById(event.getPost().getId()).orElse(null);
        if (author == null || post == null) return;

        // Notify tất cả followers của tác giả
        for (Users follower : followService.getFollowers(author.getId())) {
            NotificationResponse notification = notificationService.createNewPostNotification(follower, author, post);
            if (notification != null) {
                notificationSseService.sendToUser(follower.getId(), notification);
            }
        }
    }
}
