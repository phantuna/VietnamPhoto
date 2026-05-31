package com.example.backend.service.chat;

import com.example.backend.dto.response.chat.ChatMessageResponse;
import com.example.backend.dto.response.chat.ConversationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChatService {

    /**
     * Lấy hoặc tạo mới conversation giữa 2 người dùng (idempotent).
     */
    ConversationResponse getOrCreateConversation(String currentUserId, String otherUserId);

    /**
     * Lấy danh sách tất cả conversations của user (inbox).
     */
    List<ConversationResponse> getMyConversations(String userId);

    /**
     * Lấy lịch sử tin nhắn trong 1 conversation (phân trang, mới nhất trước).
     */
    Page<ChatMessageResponse> getMessages(String conversationId, String currentUserId, Pageable pageable);

    /**
     * Lưu tin nhắn vào DB và trả về DTO để gửi qua WebSocket.
     *
     * @param senderId   ID người gửi
     * @param receiverId ID người nhận
     * @param content    Nội dung tin
     */
    ChatMessageResponse saveMessage(String senderId, String receiverId, String content);

    /**
     * Đánh dấu đã đọc tất cả tin nhắn của người kia trong conversation.
     */
    void markAsRead(String conversationId, String currentUserId);
}
