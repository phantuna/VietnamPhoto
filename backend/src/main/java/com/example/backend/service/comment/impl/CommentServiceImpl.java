package com.example.backend.service.comment.impl;

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
import com.example.backend.service.comment.CommentService;
import com.example.backend.event.PostCommentedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.backend.service.comment.ToxicCommentModerationService;
import com.example.backend.dto.response.comment.ToxicModerationResponse;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostsRepository postsRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final com.example.backend.service.banned.BadWordFilterService badWordFilterService;
    private final ToxicCommentModerationService toxicCommentModerationService;

    @Override
    @Transactional
    public CommentResponse createComment(CommentRequest request, String userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Posts post = postsRepository.findById(request.getPostId())
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        Comment parentComment = null;
        if (request.getParentId() != null && !request.getParentId().isEmpty()) {
            parentComment = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));
            
            // Logic đảm bảo trả lời 1 cấp:
            // Nếu người dùng phản hồi một reply, thì parent của comment mới
            // sẽ được chuyển thành parent của reply đó (tức là comment gốc).
            if (parentComment.getParentComment() != null) {
                parentComment = parentComment.getParentComment();
            }
        }

        // Gọi AI Moderation Service kiểm duyệt bình luận độc hại
        ToxicModerationResponse moderation = toxicCommentModerationService.checkToxic(request.getContent());

        String cleanContent;
        if ("REJECTED".equalsIgnoreCase(moderation.getAction())) {
            throw new AppException(ErrorCode.CONTAIN_BANNED_WORDS);
        } else if ("PENDING".equalsIgnoreCase(moderation.getAction())) {
            // OFFENSIVE speech: Lọc các từ thô tục bằng BadWordFilterService
            cleanContent = badWordFilterService.censorText(request.getContent());
        } else {
            // CLEAN: Giữ nguyên bình luận gốc
            cleanContent = request.getContent();
        }

        Comment comment = Comment.builder()
                .content(cleanContent)
                .post(post)
                .user(user)
                .parentComment(parentComment)
                .build();

        comment = commentRepository.save(comment);

        eventPublisher.publishEvent(new PostCommentedEvent(user, post, cleanContent));

        return commentMapper.toResponse(comment);
    }

    @Override
    public Page<CommentResponse> getCommentsByPostId(String postId, Pageable pageable) {
        return commentRepository
                .findCommentsByPostIdWithDetails(postId, pageable)
                .map(commentMapper::toResponse);
    }

    @Override
    @Transactional
    public void deleteComment(String commentId, String userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));
        
        Users currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Dùng role.getId() vì id là định danh role trong hệ thống (ví dụ: "ADMIN", "USER")
        // Không dùng role.getName() vì field này có thể null trong DB
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> "ADMIN".equalsIgnoreCase(role.getId()));

        if (!comment.getUser().getId().equals(userId) && !isAdmin) {
            throw new AppException(ErrorCode.UNAUTHORIZED_COMMENT_ACTION);
        }
        
        commentRepository.delete(comment);
    }

    @Override
    @Transactional
    public CommentResponse updateComment(String commentId, String newContent, String userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED_COMMENT_ACTION);
        }

        // Gọi AI Moderation Service kiểm duyệt bình luận độc hại khi cập nhật
        ToxicModerationResponse moderation = toxicCommentModerationService.checkToxic(newContent);

        String cleanContent;
        if ("REJECTED".equalsIgnoreCase(moderation.getAction())) {
            throw new AppException(ErrorCode.CONTAIN_BANNED_WORDS);
        } else if ("PENDING".equalsIgnoreCase(moderation.getAction())) {
            // OFFENSIVE speech: Lọc các từ thô tục bằng BadWordFilterService
            cleanContent = badWordFilterService.censorText(newContent);
        } else {
            // CLEAN: Giữ nguyên bình luận gốc
            cleanContent = newContent;
        }

        comment.setContent(cleanContent);
        comment = commentRepository.save(comment);
        return commentMapper.toResponse(comment);
    }
}
