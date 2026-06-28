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

import com.example.backend.dto.request.post.RatePostRequest;
import com.example.backend.dto.request.post.ReportPostRequest;
import com.example.backend.entity.PostRating;
import com.example.backend.entity.Posts;
import com.example.backend.entity.Report;
import com.example.backend.entity.Users;
import com.example.backend.enums.ReportStatus;
import com.example.backend.exception.AppException;
import com.example.backend.exception.ErrorCode;
import com.example.backend.repository.post.PostRatingRepository;
import com.example.backend.repository.post.PostsRepository;
import com.example.backend.repository.post.report.ReportRepository;
import com.example.backend.repository.user.UserRepository;

@ExtendWith(MockitoExtension.class)
public class PostInteractionServiceImplTest {

    @Mock
    private PostsRepository postsRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRatingRepository postRatingRepository;

    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private PostInteractionServiceImpl postInteractionService;

    private Users user;
    private Posts post;
    private PostRating rating;

    @BeforeEach
    void setUp() {
        user = new Users();
        user.setId("user-123");

        post = new Posts();
        post.setId("post-456");
        post.setTotalRatings(0);
        post.setAverageRating(0.0f);

        rating = new PostRating();
        rating.setUser(user);
        rating.setPost(post);
    }

    @Test
    void ratePost_PostNotFound_ThrowsAppException() {
        when(postsRepository.findById("post-999")).thenReturn(Optional.empty());

        RatePostRequest request = new RatePostRequest();
        request.setRatingValue(5);

        assertThatThrownBy(() -> postInteractionService.ratePost("post-999", "user-123", request))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.POST_NOT_FOUND.name());
    }

    @Test
    void ratePost_UserNotFound_ThrowsAppException() {
        when(postsRepository.findById("post-456")).thenReturn(Optional.of(post));
        when(userRepository.findById("user-999")).thenReturn(Optional.empty());

        RatePostRequest request = new RatePostRequest();
        request.setRatingValue(5);

        assertThatThrownBy(() -> postInteractionService.ratePost("post-456", "user-999", request))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.USER_NOT_FOUND.name());
    }

    @Test
    void ratePost_ValidRequest_SavesRatingAndUpdatesAverage() {
        when(postsRepository.findById("post-456")).thenReturn(Optional.of(post));
        when(userRepository.findById("user-123")).thenReturn(Optional.of(user));
        when(postRatingRepository.findByPostIdAndUserId("post-456", "user-123")).thenReturn(Optional.empty());
        when(postRatingRepository.getAverageRatingByPostId("post-456")).thenReturn(4.5f);
        when(postRatingRepository.countRatingsByPostId("post-456")).thenReturn(1);

        RatePostRequest request = new RatePostRequest();
        request.setRatingValue(5);

        postInteractionService.ratePost("post-456", "user-123", request);

        assertThat(post.getAverageRating()).isEqualTo(4.5f);
        assertThat(post.getTotalRatings()).isEqualTo(1);
        verify(postRatingRepository, times(1)).save(any(PostRating.class));
        verify(postsRepository, times(1)).save(post);
    }

    @Test
    void ratePost_LowAverageUnderTenRatings_NoAutoFlag() {
        when(postsRepository.findById("post-456")).thenReturn(Optional.of(post));
        when(userRepository.findById("user-123")).thenReturn(Optional.of(user));
        when(postRatingRepository.findByPostIdAndUserId("post-456", "user-123")).thenReturn(Optional.empty());
        when(postRatingRepository.getAverageRatingByPostId("post-456")).thenReturn(1.5f);
        // Có vote trung bình < 2.0 nhưng số lượt vote chỉ là 9 (chưa đủ 10)
        when(postRatingRepository.countRatingsByPostId("post-456")).thenReturn(9);

        RatePostRequest request = new RatePostRequest();
        request.setRatingValue(1);

        postInteractionService.ratePost("post-456", "user-123", request);

        verify(reportRepository, times(0)).save(any(Report.class));
    }

    @Test
    void ratePost_LowAverageTenRatings_AutoFlagsPost() {
        when(postsRepository.findById("post-456")).thenReturn(Optional.of(post));
        when(userRepository.findById("user-123")).thenReturn(Optional.of(user));
        when(postRatingRepository.findByPostIdAndUserId("post-456", "user-123")).thenReturn(Optional.empty());
        when(postRatingRepository.getAverageRatingByPostId("post-456")).thenReturn(1.8f);
        // Có vote trung bình < 2.0 và số lượt vote >= 10
        when(postRatingRepository.countRatingsByPostId("post-456")).thenReturn(10);
        when(reportRepository.existsByPostAndReporterIsNullAndStatus(post, ReportStatus.PENDING)).thenReturn(false);

        RatePostRequest request = new RatePostRequest();
        request.setRatingValue(1);

        postInteractionService.ratePost("post-456", "user-123", request);

        verify(reportRepository, times(1)).save(any(Report.class));
    }

    @Test
    void reportPost_ValidRequest_SavesPendingReport() {
        when(postsRepository.findById("post-456")).thenReturn(Optional.of(post));
        when(userRepository.findById("user-123")).thenReturn(Optional.of(user));

        ReportPostRequest request = new ReportPostRequest();
        request.setReason("Spam content");

        postInteractionService.reportPost("post-456", "user-123", request);

        verify(reportRepository, times(1)).save(any(Report.class));
    }
}
