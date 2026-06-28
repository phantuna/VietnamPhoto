package com.example.backend.service.comment.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.example.backend.dto.request.comment.CommentRequest;
import com.example.backend.dto.response.comment.CommentResponse;
import com.example.backend.entity.Comment;
import com.example.backend.entity.Posts;
import com.example.backend.entity.Users;
import com.example.backend.exception.AppException;
import com.example.backend.exception.ErrorCode;
import com.example.backend.mapper.CommentMapper;
import com.example.backend.repository.comment.CommentRepository;
import com.example.backend.repository.post.PostsRepository;
import com.example.backend.repository.user.UserRepository;
import com.example.backend.service.banned.BadWordFilterService;

@ExtendWith(MockitoExtension.class)
public class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostsRepository postsRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private BadWordFilterService badWordFilterService;

    @InjectMocks
    private CommentServiceImpl commentService;

    private Users user;
    private Posts post;
    private Comment comment;

    @BeforeEach
    void setUp() {
        user = new Users();
        user.setId("user-123");
        user.setRoles(Collections.emptyList());

        post = new Posts();
        post.setId("post-456");

        comment = new Comment();
        comment.setId("comment-789");
        comment.setUser(user);
        comment.setPost(post);
        comment.setContent("Hello!");
    }

    @Test
    void createComment_PostNotFound_ThrowsAppException() {
        when(userRepository.findById("user-123")).thenReturn(Optional.of(user));
        when(postsRepository.findById("post-999")).thenReturn(Optional.empty());

        CommentRequest request = new CommentRequest();
        request.setPostId("post-999");
        request.setContent("Test comment");

        assertThatThrownBy(() -> commentService.createComment(request, "user-123"))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.POST_NOT_FOUND.name());
    }

    @Test
    void createComment_ValidRequest_Success() {
        when(userRepository.findById("user-123")).thenReturn(Optional.of(user));
        when(postsRepository.findById("post-456")).thenReturn(Optional.of(post));
        when(badWordFilterService.censorText("Nice picture!")).thenReturn("Nice picture!");
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.toResponse(any(Comment.class))).thenReturn(new CommentResponse());

        CommentRequest request = new CommentRequest();
        request.setPostId("post-456");
        request.setContent("Nice picture!");

        CommentResponse response = commentService.createComment(request, "user-123");

        assertThat(response).isNotNull();
        verify(commentRepository, times(1)).save(any(Comment.class));
        verify(eventPublisher, times(1)).publishEvent(any(Object.class));
    }

    @Test
    void deleteComment_UnauthorizedUser_ThrowsAppException() {
        // Comment của user-123, nhưng người xóa là user-999 (không phải admin)
        when(commentRepository.findById("comment-789")).thenReturn(Optional.of(comment));
        
        Users otherUser = new Users();
        otherUser.setId("user-999");
        otherUser.setRoles(Collections.emptyList());
        when(userRepository.findById("user-999")).thenReturn(Optional.of(otherUser));

        assertThatThrownBy(() -> commentService.deleteComment("comment-789", "user-999"))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.UNAUTHORIZED_COMMENT_ACTION.name());
    }

    @Test
    void updateComment_NotCommentOwner_ThrowsAppException() {
        when(commentRepository.findById("comment-789")).thenReturn(Optional.of(comment));

        assertThatThrownBy(() -> commentService.updateComment("comment-789", "New content", "user-999"))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.UNAUTHORIZED_COMMENT_ACTION.name());
    }
}
