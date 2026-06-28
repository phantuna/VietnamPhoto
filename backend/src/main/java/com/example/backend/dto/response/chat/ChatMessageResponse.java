package com.example.backend.dto.response.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {
    private String id;
    private String conversationId;
    private String senderId;
    private String senderUsername;
    private String senderAvatarUrl;
    private String content;
    private Boolean isRead;
    private LocalDateTime sentAt;
}
