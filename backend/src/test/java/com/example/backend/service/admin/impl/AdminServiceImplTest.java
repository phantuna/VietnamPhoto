package com.example.backend.service.admin.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.backend.entity.Posts;
import com.example.backend.entity.Report;
import com.example.backend.entity.Users;
import com.example.backend.enums.ReportStatus;
import com.example.backend.exception.AppException;
import com.example.backend.exception.ErrorCode;
import com.example.backend.repository.post.PostsRepository;
import com.example.backend.repository.post.report.ReportRepository;
import com.example.backend.repository.user.UserRepository;
import com.example.backend.service.user.ReputationService;

@ExtendWith(MockitoExtension.class)
public class AdminServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostsRepository postsRepository;

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private ReputationService reputationService;

    @InjectMocks
    private AdminServiceImpl adminService;

    private Report report;
    private Posts post;
    private Users postOwner;

    @BeforeEach
    void setUp() {
        postOwner = new Users();
        postOwner.setId("owner-123");
        postOwner.setUsername("owner");

        post = new Posts();
        post.setId("post-456");
        post.setUser(postOwner);

        report = new Report();
        report.setId("report-789");
        report.setPost(post);
        report.setStatus(ReportStatus.PENDING);
    }

    @Test
    void resolveReport_ReportNotFound_ThrowsAppException() {
        when(reportRepository.findById("report-999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.resolveReport("report-999"))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.REPORT_NOT_FOUND.name());
    }

    @Test
    void resolveReport_AlreadyResolved_ThrowsAppException() {
        report.setStatus(ReportStatus.RESOLVED);
        when(reportRepository.findById("report-789")).thenReturn(Optional.of(report));

        assertThatThrownBy(() -> adminService.resolveReport("report-789"))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.REPORT_ALREADY_RESOLVED.name());
    }

    @Test
    void resolveReport_ValidPendingReport_SoftDeletesPostAndDeductsPoints() {
        when(reportRepository.findById("report-789")).thenReturn(Optional.of(report));
        when(reportRepository.findReportsByPostIdWithDetails("post-456")).thenReturn(List.of(report));

        adminService.resolveReport("report-789");

        assertThat(report.getStatus()).isEqualTo(ReportStatus.RESOLVED);
        assertThat(post.getDeleted()).isEqualTo(1);
        verify(postsRepository, times(1)).save(post);
        verify(reputationService, times(1)).subtractPoints(postOwner, 20, "Bài viết bị Admin xác nhận vi phạm");
    }

    @Test
    void banUser_UserNotFound_ThrowsAppException() {
        when(userRepository.findById("user-999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.banUser("user-999"))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.USER_NOT_FOUND.name());
    }

    @Test
    void banUser_UserExists_DeletesUser() {
        Users targetUser = new Users();
        targetUser.setId("user-123");
        when(userRepository.findById("user-123")).thenReturn(Optional.of(targetUser));

        adminService.banUser("user-123");

        verify(userRepository, times(1)).delete(targetUser);
    }
}
