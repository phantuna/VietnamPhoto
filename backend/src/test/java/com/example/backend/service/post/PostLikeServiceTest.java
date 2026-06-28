package com.example.backend.service.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.backend.dto.response.post.LikeToggleResponse;
import com.example.backend.entity.Likes;
import com.example.backend.entity.Posts;
import com.example.backend.entity.Users;
import com.example.backend.exception.AppException;
import com.example.backend.exception.ErrorCode;
import com.example.backend.repository.post.LikeRepository;
import com.example.backend.repository.post.PostsRepository;
import com.example.backend.repository.user.UserRepository;
import com.example.backend.service.notification.NotificationService;
import com.example.backend.service.notification.NotificationSseService;

@ExtendWith(MockitoExtension.class)
public class PostLikeServiceTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private PostsRepository postsRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private NotificationSseService notificationSseService;

    @InjectMocks
    private PostLikeService postLikeService;

    private Users user;
    private Users postOwner;
    private Posts post;
    private Likes like;

    @BeforeEach
    void setUp() {
        user = new Users();
        user.setId("user-liker");

        postOwner = new Users();
        postOwner.setId("user-owner");

        post = new Posts();
        post.setId("post-456");
        post.setUser(postOwner);
        post.setLikeCount(5L);

        like = new Likes();
        like.setUser(user);
        like.setPost(post);
    }

    @Test
    void toggleLike_PostNotFound_ThrowsAppException() {
        when(postsRepository.findById("post-999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postLikeService.toggleLike("user-liker", "post-999"))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.POST_NOT_FOUND.name());
    }

    @Test
    void toggleLike_AlreadyLiked_RemovesLikeAndDecrementsCount() {
        when(postsRepository.findById("post-456")).thenReturn(Optional.of(post));
        when(likeRepository.findByUserIdAndPostId("user-liker", "post-456")).thenReturn(Optional.of(like));

        LikeToggleResponse response = postLikeService.toggleLike("user-liker", "post-456");

        assertThat(response).isNotNull();
        assertThat(response.isLiked()).isFalse();
        assertThat(response.getTotalLikes()).isEqualTo(4L);
        assertThat(post.getLikeCount()).isEqualTo(4L);

        verify(likeRepository, times(1)).delete(like);
        verify(postsRepository, times(1)).save(post);
        verify(notificationService, times(1)).removePostLikedNotification(user, post);
    }

    @Test
    void toggleLike_NotLikedYet_AddsLikeAndIncrementsCount() {
        when(postsRepository.findById("post-456")).thenReturn(Optional.of(post));
        when(likeRepository.findByUserIdAndPostId("user-liker", "post-456")).thenReturn(Optional.empty());
        when(userRepository.findById("user-liker")).thenReturn(Optional.of(user));

        LikeToggleResponse response = postLikeService.toggleLike("user-liker", "post-456");

        assertThat(response).isNotNull();
        assertThat(response.isLiked()).isTrue();
        assertThat(response.getTotalLikes()).isEqualTo(6L);
        assertThat(post.getLikeCount()).isEqualTo(6L);

        verify(likeRepository, times(1)).save(any(Likes.class));
        verify(postsRepository, times(1)).save(post);
        verify(notificationService, times(1)).createPostLikedNotification(user, post);
    }
}
