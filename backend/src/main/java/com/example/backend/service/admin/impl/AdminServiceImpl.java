package com.example.backend.service.admin.impl;

import com.example.backend.dto.response.admin.*;
import com.example.backend.entity.*;
import com.example.backend.enums.PostStatus;
import com.example.backend.enums.ReportStatus;
import com.example.backend.exception.AppException;
import com.example.backend.exception.ErrorCode;
import com.example.backend.repository.post.PostsRepository;
import com.example.backend.repository.post.report.ReportRepository;
import com.example.backend.repository.user.UserRepository;
import com.example.backend.service.admin.AdminService;
import com.example.backend.service.user.ReputationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final PostsRepository postsRepository;
    private final ReportRepository reportRepository;
    private final ReputationService reputationService;
    private final com.example.backend.repository.location.LocationsRepository locationsRepository;
    private final com.example.backend.repository.user.RoleRepository roleRepository;

    @Override
    @Transactional(readOnly = true)
    public AdminStatsResponse getDashboardStats() {
        long totalUsers = userRepository.count();
        long totalPosts = postsRepository.count();
        long pendingReports = reportRepository.countByStatus(ReportStatus.PENDING);

        List<AdminStatsResponse.DailyStat> postsPerDay = new ArrayList<>();
        List<AdminStatsResponse.DailyStat> usersPerDay = new ArrayList<>();
        
        LocalDate today = LocalDate.now();
        String[] dayNames = {"", "T2", "T3", "T4", "T5", "T6", "T7", "CN"};

        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String dayName = dayNames[date.getDayOfWeek().getValue()];

            long postCount = postsRepository.countByCreatedDate(date);
            long userCount = userRepository.countByCreatedDate(date);

            postsPerDay.add(new AdminStatsResponse.DailyStat(dayName, postCount));
            usersPerDay.add(new AdminStatsResponse.DailyStat(dayName, userCount));
        }

        return AdminStatsResponse.builder()
                .totalUsers(totalUsers)
                .totalPosts(totalPosts)
                .pendingReports(pendingReports)
                .postsPerDay(postsPerDay)
                .usersPerDay(usersPerDay)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReportResponse> getReports(String statusStr, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Report> reports;
        
        if (statusStr != null && !statusStr.isEmpty() && !statusStr.equalsIgnoreCase("ALL")) {
            try {
                ReportStatus status = ReportStatus.valueOf(statusStr.toUpperCase());
                reports = reportRepository.findReportsByStatusWithDetails(status, pageable);
            } catch (IllegalArgumentException e) {
                reports = reportRepository.findAllReportsWithDetails(pageable);
            }
        } else {
            reports = reportRepository.findAllReportsWithDetails(pageable);
        }

        return reports.map(this::mapToResponse);
    }

    @Override
    @Transactional
    public void resolveReport(String reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new AppException(ErrorCode.REPORT_NOT_FOUND));

        if (report.getStatus() != ReportStatus.PENDING) {
            throw new AppException(ErrorCode.REPORT_ALREADY_RESOLVED);
        }

        Posts post = report.getPost();
        if (post != null) {
            // Lấy tất cả báo cáo đang PENDING của bài viết này để xử lý chung 1 lần
            List<Report> pendingReports = reportRepository.findReportsByPostIdWithDetails(post.getId())
                                            .stream()
                                            .filter(r -> r.getStatus() == ReportStatus.PENDING)
                                            .toList();
            for (Report r : pendingReports) {
                r.setStatus(ReportStatus.RESOLVED);
                reportRepository.save(r);
            }

            post.setStatus(PostStatus.HIDDEN);
            post.setDeleted(1); // Soft delete bài viết
            postsRepository.save(post);

            Users postOwner = post.getUser();
            if (postOwner != null) {
                reputationService.subtractPoints(postOwner, 20, "Bài viết bị Admin xác nhận vi phạm");
            }
            log.info("Resolved {} reports for post {}. Post hidden. User penalized.", pendingReports.size(), post.getId());
        } else {
            report.setStatus(ReportStatus.RESOLVED);
            reportRepository.save(report);
            log.info("Report {} resolved.", reportId);
        }
    }

    @Override
    @Transactional
    public void dismissReport(String reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new AppException(ErrorCode.REPORT_NOT_FOUND));

        if (report.getStatus() != ReportStatus.PENDING) {
            throw new AppException(ErrorCode.REPORT_ALREADY_RESOLVED);
        }

        Posts post = report.getPost();
        if (post != null) {
            List<Report> pendingReports = reportRepository.findReportsByPostIdWithDetails(post.getId())
                                            .stream()
                                            .filter(r -> r.getStatus() == ReportStatus.PENDING)
                                            .toList();
            for (Report r : pendingReports) {
                r.setStatus(ReportStatus.DISMISSED);
                reportRepository.save(r);
            }
            log.info("Dismissed {} reports for post {}.", pendingReports.size(), post.getId());
        } else {
            report.setStatus(ReportStatus.DISMISSED);
            reportRepository.save(report);
            log.info("Report {} dismissed.", reportId);
        }
    }

    @Override
    @Transactional
    public void banUser(String userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        
        // Vì class Users có cấu hình @SQLDelete, gọi delete() sẽ tự động update deleted = 1
        userRepository.delete(user);
        log.info("Admin đã khóa tài khoản user {}", userId);
    }

    @Override
    @Transactional
    public void unbanUser(String userId) {
        userRepository.unbanUser(userId);
        log.info("Admin đã mở khóa tài khoản user {}", userId);
    }

    @Override
    @Transactional
    public void updateUserRole(String userId, boolean isAdmin) {
        String currentUserId = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        if (userId.equals(currentUserId) && !isAdmin) {
            throw new AppException(ErrorCode.CANNOT_REVOKE_OWN_ADMIN);
        }
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Role adminRole = roleRepository.findById("ADMIN").orElseThrow(() -> new AppException(ErrorCode.USER_ROLE_NOT_FOUND));
        if (isAdmin) {
            if (!user.getRoles().contains(adminRole)) {
                user.getRoles().add(adminRole);
            }
        } else {
            user.getRoles().remove(adminRole);
        }
        userRepository.save(user);
        log.info("Cập nhật quyền admin cho user {} thành {}", userId, isAdmin);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminUserResponse> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Users> users = userRepository.findAllUsersIncludeBanned(pageable);
        return users.map(u -> AdminUserResponse.builder()
                .id(u.getId())
                .username(u.getUsername())
                .email(u.getEmail())
                .avatarUrl(u.getAvatarUrl())
                .reputationScore(u.getReputationScore())
                .level(u.getLevel())
                .deleted(u.getDeleted())
                .roles(u.getRoles() != null ? u.getRoles().stream().map(Role::getId).toList() : new java.util.ArrayList<>())
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminPostResponse> getAllPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Posts> posts = postsRepository.findAllPostsIncludeDeleted(pageable);
        return posts.map(p -> {
            AdminPostResponse.UserInfo user = null;
            if (p.getUser() != null) {
                user = AdminPostResponse.UserInfo.builder()
                        .id(p.getUser().getId())
                        .username(p.getUser().getUsername())
                        .email(p.getUser().getEmail())
                        .build();
            }

            AdminPostResponse.LocationInfo loc = null;
            if (p.getLocation() != null) {
                loc = AdminPostResponse.LocationInfo.builder()
                        .id(p.getLocation().getId())
                        .name(p.getLocation().getName())
                        .nameWithType(p.getLocation().getNameWithType())
                        .build();
            }

            List<AdminPostResponse.PhotoInfo> photos = new ArrayList<>();
            if (p.getPhotos() != null) {
                for (Photos photo : p.getPhotos()) {
                    photos.add(AdminPostResponse.PhotoInfo.builder()
                            .id(photo.getId())
                            .imageUrl(photo.getImageUrl())
                            .build());
                }
            }

            return AdminPostResponse.builder()
                    .id(p.getId())
                    .caption(p.getCaption())
                    .createdDate(p.getCreatedDate())
                    .user(user)
                    .location(loc)
                    .averageRating(p.getAverageRating())
                    .totalRatings(p.getTotalRatings())
                    .deleted(p.getDeleted())
                    .photos(photos)
                    .build();
        });
    }

    @Override
    @Transactional
    @CacheEvict(value = "posts", allEntries = true)
    public void togglePostStatus(String postId, int deleted) {
        postsRepository.togglePostStatus(postId, deleted);
        log.info("Admin updated post {} status to deleted={}", postId, deleted);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportResponse> getPostReportHistory(String postId) {
        List<Report> reports = reportRepository.findReportsByPostIdWithDetails(postId);
        return reports.stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminLocationResponse> getAllLocations(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Locations> locations = locationsRepository.findAllLocationsIncludeDeleted(pageable);
        return locations.map(l -> AdminLocationResponse.builder()
                .id(l.getId())
                .code(l.getCode())
                .name(l.getName())
                .nameWithType(l.getNameWithType())
                .type(l.getCategory())
                .latitude(l.getLatitude())
                .longitude(l.getLongitude())
                .deleted(l.getDeleted())
                .build());
    }

    @Override
    @Transactional
    @CacheEvict(value = "locations", allEntries = true)
    public void toggleLocationStatus(String locationId, int deleted) {
        locationsRepository.toggleLocationStatus(locationId, deleted);
        log.info("Admin updated location {} status to deleted={}", locationId, deleted);
    }

    private ReportResponse mapToResponse(Report report) {
        String reporterUsername;
        if (report.getReporter() != null) {
            reporterUsername = report.getReporter().getUsername();
        } else {
            reporterUsername = "Hệ thống tự động";
        }

        return ReportResponse.builder()
                .id(report.getId())
                .postId(report.getPost() != null ? report.getPost().getId() : null)
                .postCaption(report.getPost() != null ? report.getPost().getCaption() : null)
                .postAuthorId((report.getPost() != null && report.getPost().getUser() != null) ? report.getPost().getUser().getId() : null)
                .postAuthorUsername((report.getPost() != null && report.getPost().getUser() != null) ? report.getPost().getUser().getUsername() : null)
                .reporterId(report.getReporter() != null ? report.getReporter().getId() : null)
                .reporterUsername(reporterUsername)
                .reason(report.getReason())
                .status(report.getStatus())
                .createdAt(report.getCreatedDate() != null ? report.getCreatedDate().atStartOfDay() : null)
                .build();
    }
}
