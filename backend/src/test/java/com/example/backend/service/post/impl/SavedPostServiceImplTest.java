package com.example.backend.service.post.impl;

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

import com.example.backend.entity.Posts;
import com.example.backend.entity.SavedPost;
import com.example.backend.entity.Users;
import com.example.backend.exception.AppException;
import com.example.backend.exception.ErrorCode;
import com.example.backend.repository.post.PostsRepository;
import com.example.backend.repository.post.SavedPostRepository;
import com.example.backend.repository.user.UserRepository;

@ExtendWith(MockitoExtension.class)
public class SavedPostServiceImplTest {

    @Mock
    private SavedPostRepository savedPostRepository;

    @Mock
    private PostsRepository postsRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SavedPostServiceImpl savedPostService;

    private Users user;
    private Posts post;
    private SavedPost savedPost;

    @BeforeEach
    void setUp() {
        user = new Users();
        user.setId("user-123");
        
        post = new Posts();
        post.setId("post-456");

        savedPost = new SavedPost();
        savedPost.setUser(user);
        savedPost.setPost(post);
        savedPost.setDeleted(0);
    }

    @Test
    void toggleSavePost_UserNotFound_ThrowsAppException() {
        when(userRepository.findById("user-999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> savedPostService.toggleSavePost("user-999", "post-456"))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.USER_NOT_FOUND.name());
    }

    @Test
    void toggleSavePost_PostNotFound_ThrowsAppException() {
        when(userRepository.findById("user-123")).thenReturn(Optional.of(user));
        when(postsRepository.findById("post-999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> savedPostService.toggleSavePost("user-123", "post-999"))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.POST_NOT_FOUND.name());
    }

    @Test
    void toggleSavePost_FirstTimeSave_ReturnsTrue() {
        when(userRepository.findById("user-123")).thenReturn(Optional.of(user));
        when(postsRepository.findById("post-456")).thenReturn(Optional.of(post));
        when(savedPostRepository.findByUserIdAndPostId("user-123", "post-456")).thenReturn(Optional.empty());

        boolean result = savedPostService.toggleSavePost("user-123", "post-456");

        assertThat(result).isTrue();
        verify(savedPostRepository, times(1)).save(any(SavedPost.class));
    }

    @Test
    void toggleSavePost_AlreadySaved_UnsavesReturnsFalse() {
        when(userRepository.findById("user-123")).thenReturn(Optional.of(user));
        when(postsRepository.findById("post-456")).thenReturn(Optional.of(post));
        
        savedPost.setDeleted(0);
        when(savedPostRepository.findByUserIdAndPostId("user-123", "post-456")).thenReturn(Optional.of(savedPost));

        boolean result = savedPostService.toggleSavePost("user-123", "post-456");

        assertThat(result).isFalse();
        assertThat(savedPost.getDeleted()).isEqualTo(1);
        verify(savedPostRepository, times(1)).save(savedPost);
    }

    @Test
    void toggleSavePost_PreviouslyUnsaved_ResavesReturnsTrue() {
        when(userRepository.findById("user-123")).thenReturn(Optional.of(user));
        when(postsRepository.findById("post-456")).thenReturn(Optional.of(post));
        
        savedPost.setDeleted(1);
        when(savedPostRepository.findByUserIdAndPostId("user-123", "post-456")).thenReturn(Optional.of(savedPost));

        boolean result = savedPostService.toggleSavePost("user-123", "post-456");

        assertThat(result).isTrue();
        assertThat(savedPost.getDeleted()).isEqualTo(0);
        verify(savedPostRepository, times(1)).save(savedPost);
    }
}
