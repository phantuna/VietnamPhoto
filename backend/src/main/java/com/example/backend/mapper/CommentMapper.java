package com.example.backend.mapper;

import com.example.backend.dto.response.CommentResponse;
import com.example.backend.dto.response.UserResponse;
import com.example.backend.entity.Comment;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import java.util.List;
import java.util.Comparator;

@Component
public class CommentMapper {

    public CommentResponse toResponse(Comment comment) {
        if (comment == null) {
            return null;
        }

        List<CommentResponse> replies = null;
        if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
            replies = comment.getReplies().stream()
                    .sorted(Comparator.comparing(Comment::getCreatedDate).thenComparing(Comment::getId))
                    .map(this::toResponseWithoutReplies)
                    .collect(Collectors.toList());
        }

        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdDate(comment.getCreatedDate())
                .author(mapAuthor(comment))
                .replies(replies)
                .build();
    }

    private CommentResponse toResponseWithoutReplies(Comment comment) {
        if (comment == null) {
            return null;
        }
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdDate(comment.getCreatedDate())
                .author(mapAuthor(comment))
                .build();
    }

    private UserResponse mapAuthor(Comment comment) {
        if (comment.getUser() == null) return null;

        return UserResponse.builder()
                .id(comment.getUser().getId().toString())
                .username(comment.getUser().getUsername())
                .avatarUrl(comment.getUser().getAvatarUrl())
                .build();
    }
}
