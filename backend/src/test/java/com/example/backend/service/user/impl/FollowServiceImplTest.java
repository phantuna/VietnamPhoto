package com.example.backend.service.user.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.example.backend.repository.user.follow.UserFollowRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.example.backend.dto.response.user.FollowStatusResponse;
import com.example.backend.entity.UserFollow;
import com.example.backend.entity.Users;
import com.example.backend.event.NewFollowerEvent;
import com.example.backend.exception.AppException;
import com.example.backend.exception.ErrorCode;
import com.example.backend.repository.user.UserRepository;

@ExtendWith(MockitoExtension.class)
public class FollowServiceImplTest {

    @Mock
    private UserFollowRepository userFollowRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private FollowServiceImpl followService;

    private Users follower;
    private Users following;
    private UserFollow userFollow;

    @BeforeEach
    void setUp() {
        follower = new Users();
        follower.setId("user-follower");
        follower.setUsername("follower");

        following = new Users();
        following.setId("user-following");
        following.setUsername("following");

        userFollow = new UserFollow();
        userFollow.setFollower(follower);
        userFollow.setFollowing(following);
        userFollow.setDeleted(0);
    }

    @Test
    void toggleFollow_FollowYourself_ThrowsAppException() {
        assertThatThrownBy(() -> followService.toggleFollow("user-123", "user-123"))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.CANNOT_FOLLOW_YOURSELF.name());
    }

    @Test
    void toggleFollow_NewFollow_CreatesFollowAndPublishesEvent() {
        when(userRepository.findById("user-follower")).thenReturn(Optional.of(follower));
        when(userRepository.findById("user-following")).thenReturn(Optional.of(following));
        when(userFollowRepository.findByFollowerIdAndFollowingId("user-follower", "user-following")).thenReturn(Optional.empty());

        when(userFollowRepository.countByFollowingIdAndDeleted("user-following", 0)).thenReturn(1L);
        when(userFollowRepository.countByFollowerIdAndDeleted("user-follower", 0)).thenReturn(1L);

        FollowStatusResponse response = followService.toggleFollow("user-follower", "user-following");

        assertThat(response).isNotNull();
        assertThat(response.isFollowing()).isTrue();
        assertThat(response.getFollowersCount()).isEqualTo(1L);
        assertThat(response.getFollowingCount()).isEqualTo(1L);

        verify(userFollowRepository, times(1)).save(any(UserFollow.class));
        verify(eventPublisher, times(1)).publishEvent(any(NewFollowerEvent.class));
    }

    @Test
    void toggleFollow_ExistingFollowActive_UnfollowsSoftDelete() {
        when(userRepository.findById("user-follower")).thenReturn(Optional.of(follower));
        when(userRepository.findById("user-following")).thenReturn(Optional.of(following));
        
        userFollow.setDeleted(0);
        when(userFollowRepository.findByFollowerIdAndFollowingId("user-follower", "user-following")).thenReturn(Optional.of(userFollow));

        when(userFollowRepository.countByFollowingIdAndDeleted("user-following", 0)).thenReturn(0L);
        when(userFollowRepository.countByFollowerIdAndDeleted("user-follower", 0)).thenReturn(0L);

        FollowStatusResponse response = followService.toggleFollow("user-follower", "user-following");

        assertThat(response).isNotNull();
        assertThat(response.isFollowing()).isFalse();
        assertThat(userFollow.getDeleted()).isEqualTo(1);
        verify(userFollowRepository, times(1)).save(userFollow);
    }

    @Test
    void toggleFollow_ExistingFollowSoftDeleted_ReactivatesAndPublishesEvent() {
        when(userRepository.findById("user-follower")).thenReturn(Optional.of(follower));
        when(userRepository.findById("user-following")).thenReturn(Optional.of(following));
        
        userFollow.setDeleted(1);
        when(userFollowRepository.findByFollowerIdAndFollowingId("user-follower", "user-following")).thenReturn(Optional.of(userFollow));

        when(userFollowRepository.countByFollowingIdAndDeleted("user-following", 0)).thenReturn(1L);
        when(userFollowRepository.countByFollowerIdAndDeleted("user-follower", 0)).thenReturn(1L);

        FollowStatusResponse response = followService.toggleFollow("user-follower", "user-following");

        assertThat(response).isNotNull();
        assertThat(response.isFollowing()).isTrue();
        assertThat(userFollow.getDeleted()).isEqualTo(0);
        
        verify(userFollowRepository, times(1)).save(userFollow);
        verify(eventPublisher, times(1)).publishEvent(any(NewFollowerEvent.class));
    }
}
