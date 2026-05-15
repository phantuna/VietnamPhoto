package com.example.backend.mapper;

import com.example.backend.dto.response.NotificationResponse;
import com.example.backend.entity.Notification;
import com.example.backend.entity.Users;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(Notification n, Long unreadCount) {
        Users actor = n.getActor();

        return NotificationResponse.builder()
                .id(n.getId())
                .type(n.getType().name())
                .content(n.getContent())
                .isRead(n.getIsRead())
                .actorId(actor.getId())
                .actorName(actor.getUsername())
                .actorAvatar(actor.getAvatarUrl())
                .postId(n.getPost() != null ? n.getPost().getId() : null)
                .unreadCount(unreadCount)
                .createdAt(n.getCreatedAtTime() != null ? n.getCreatedAtTime().toString() : null)
                .build();
    }
}