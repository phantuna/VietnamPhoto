package com.example.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private String id;
    private String type;
    private String content;
    private Boolean isRead;

    private String actorId;
    private String actorName;
    private String actorAvatar;

    private String postId;

    private Long unreadCount;

    private LocalDateTime createdAt;
    private LocalDate createdDate;
}
