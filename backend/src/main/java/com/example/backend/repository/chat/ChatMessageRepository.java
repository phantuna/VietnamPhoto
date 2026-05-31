package com.example.backend.repository.chat;

import com.example.backend.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, String> {

    /**
     * Lấy tin nhắn của conversation, mới nhất trước (cho phân trang).
     */
    Page<ChatMessage> findByConversationIdOrderBySentAtDesc(String conversationId, Pageable pageable);

    /**
     * Đánh dấu đã đọc toàn bộ tin của người kia trong conversation này.
     */
    @Modifying
    @Query("""
            UPDATE ChatMessage m
            SET m.isRead = true
            WHERE m.conversation.id = :convId
              AND m.sender.id != :userId
              AND m.isRead = false
            """)
    int markMessagesAsRead(@Param("convId") String convId, @Param("userId") String userId);

    /**
     * Đếm số tin chưa đọc của user trong conversation (tin từ người kia gửi).
     */
    @Query("""
            SELECT COUNT(m) FROM ChatMessage m
            WHERE m.conversation.id = :convId
              AND m.sender.id != :userId
              AND m.isRead = false
            """)
    long countUnread(@Param("convId") String convId, @Param("userId") String userId);

    /**
     * Lấy tin nhắn mới nhất trong conversation (dùng cho preview ở inbox).
     */
    @Query("""
            SELECT m FROM ChatMessage m
            WHERE m.conversation.id = :convId
            ORDER BY m.sentAt DESC
            LIMIT 1
            """)
    java.util.Optional<ChatMessage> findLatestMessage(@Param("convId") String convId);
}
