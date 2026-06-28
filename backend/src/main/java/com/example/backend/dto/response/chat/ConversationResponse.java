package com.example.backend.dto.response.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationResponse {
    private String id;

    private String otherUserId;
    private String otherUserUsername;
    private String otherUserAvatarUrl;

    private String lastMessageContent;
    private java.time.LocalDateTime lastMessageAt;
    private long unreadCount;
}
