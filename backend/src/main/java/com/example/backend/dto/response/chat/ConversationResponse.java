package com.example.backend.dto.response.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dữ liệu 1 conversation hiển thị trong inbox.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationResponse {
    private String id;

    /** Thông tin người đang chat cùng (khác với current user) */
    private String otherUserId;
    private String otherUserUsername;
    private String otherUserAvatarUrl;

    /** Tin nhắn gần nhất để preview */
    private String lastMessageContent;
    private java.time.LocalDateTime lastMessageAt;

    /** Số tin chưa đọc của current user trong conversation này */
    private long unreadCount;
}
