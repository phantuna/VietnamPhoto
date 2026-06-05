package com.example.backend.service.comment.impl;

import com.example.backend.dto.request.comment.CommentRequest;
import com.example.backend.dto.response.comment.CommentResponse;
import com.example.backend.entity.Comment;
import com.example.backend.entity.Posts;
import com.example.backend.entity.Users;
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

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostsRepository postsRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final com.example.backend.service.banned.BadWordFilterService badWordFilterService;

    @Override
    @Transactional
    public CommentResponse createComment(CommentRequest request, String userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Posts post = postsRepository.findById(request.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment parentComment = null;
        if (request.getParentId() != null && !request.getParentId().isEmpty()) {
            parentComment = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent comment not found"));
            
            // Logic đảm bảo trả lời 1 cấp:
            // Nếu người dùng phản hồi một reply, thì parent của comment mới
            // sẽ được chuyển thành parent của reply đó (tức là comment gốc).
            if (parentComment.getParentComment() != null) {
                parentComment = parentComment.getParentComment();
            }
        }

        String cleanContent = badWordFilterService.censorText(request.getContent());

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
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        
        Users currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase("ADMIN") || role.getId().equalsIgnoreCase("ADMIN"));

        if (!comment.getUser().getId().equals(userId) && !isAdmin) {
            throw new RuntimeException("Not authorized to delete this comment");
        }
        
        commentRepository.delete(comment);
    }

    @Override
    @Transactional
    public CommentResponse updateComment(String commentId, String newContent, String userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("Not authorized to update this comment");
        }

        String cleanContent = badWordFilterService.censorText(newContent);
        comment.setContent(cleanContent);
        comment = commentRepository.save(comment);
        return commentMapper.toResponse(comment);
    }
}
