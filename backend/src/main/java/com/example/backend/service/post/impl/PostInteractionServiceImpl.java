package com.example.backend.service.post.impl;

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
import com.example.backend.service.post.PostInteractionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostInteractionServiceImpl implements PostInteractionService {

    private final PostsRepository postsRepository;
    private final UserRepository userRepository;
    private final PostRatingRepository postRatingRepository;
    private final ReportRepository reportRepository;

    @Override
    @Transactional
    public void ratePost(String postId, String userId, RatePostRequest request) {
        Posts post = postsRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        PostRating rating = postRatingRepository.findByPostIdAndUserId(postId, userId)
                .orElse(new PostRating());

        rating.setPost(post);
        rating.setUser(user);
        rating.setRatingValue(request.getRatingValue());
        
        postRatingRepository.save(rating);

        // Cập nhật điểm trung bình và tổng lượt vote
        Float avgRating = postRatingRepository.getAverageRatingByPostId(postId);
        Integer totalRatings = postRatingRepository.countRatingsByPostId(postId);
        
        // Chuyển Float double precision về Float nếu cần, nhưng COALESCE đã xử lý null
        post.setAverageRating(avgRating != null ? avgRating : 0f);
        post.setTotalRatings(totalRatings);
        
        postsRepository.save(post);

        // Auto-flag system: Vote > 10 và trung bình < 2.0 -> Tự động báo cáo Admin
        if (post.getTotalRatings() >= 10 && post.getAverageRating() < 2.0f) {
            autoFlagPost(post);
        }
    }

    @Override
    @Transactional
    public void reportPost(String postId, String userId, ReportPostRequest request) {
        Posts post = postsRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        Users reporter = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Report report = new Report();
        report.setPost(post);
        report.setReporter(reporter);
        report.setReason(request.getReason());
        report.setStatus(ReportStatus.PENDING);

        reportRepository.save(report);
        log.info("User {} đã báo cáo bài viết {} với lý do: {}", userId, postId, request.getReason());
    }

    private void autoFlagPost(Posts post) {
        // Guard: tránh tạo nhiều auto-report trùng cho cùng 1 bài viết
        boolean alreadyFlagged = reportRepository.existsByPostAndReporterIsNullAndStatus(
                post, ReportStatus.PENDING
        );
        if (alreadyFlagged) {
            log.info("Post {} đã được auto-flag trước đó (PENDING), bỏ qua.", post.getId());
            return;
        }

        Report report = new Report();
        report.setPost(post);
        report.setReporter(null); // Hệ thống tự động
        report.setReason("Hệ thống tự động: Điểm đánh giá trung bình cộng đồng quá thấp (" + post.getAverageRating() + " sao).");
        report.setStatus(ReportStatus.PENDING);

        reportRepository.save(report);
        log.warn("Hệ thống đã tự động cắm cờ bài viết {} do vote sao thấp.", post.getId());
    }
}
