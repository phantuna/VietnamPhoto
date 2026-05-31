package com.example.backend.controller.chat;

import com.example.backend.dto.response.chat.ChatMessageResponse;
import com.example.backend.dto.response.chat.ConversationResponse;
import com.example.backend.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API cho chat:
 * - Inbox (danh sách conversations)
 * - Lịch sử tin nhắn (có phân trang)
 * - Tạo/mở conversation
 * - Mark đã đọc
 */
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    /**
     * GET /chat/conversations
     * Lấy toàn bộ conversations của current user (inbox).
     */
    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationResponse>> getMyConversations(Authentication auth) {
        String userId = (String) auth.getPrincipal();
        return ResponseEntity.ok(chatService.getMyConversations(userId));
    }

    /**
     * POST /chat/conversations/{receiverId}
     * Mở/tạo conversation với người dùng khác.
     */
    @PostMapping("/conversations/{receiverId}")
    public ResponseEntity<ConversationResponse> openConversation(
            @PathVariable String receiverId,
            Authentication auth) {
        String userId = (String) auth.getPrincipal();
        return ResponseEntity.ok(chatService.getOrCreateConversation(userId, receiverId));
    }

    /**
     * GET /chat/conversations/{conversationId}/messages?page=0&size=20
     * Lấy lịch sử tin nhắn, mới nhất trước, hỗ trợ phân trang.
     */
    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<Page<ChatMessageResponse>> getMessages(
            @PathVariable String conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication auth) {
        String userId = (String) auth.getPrincipal();
        PageRequest pageable = PageRequest.of(page, size, Sort.by("sentAt").descending());
        return ResponseEntity.ok(chatService.getMessages(conversationId, userId, pageable));
    }

    /**
     * PUT /chat/conversations/{conversationId}/read
     * Đánh dấu đã đọc toàn bộ tin nhắn chưa đọc trong conversation.
     */
    @PutMapping("/conversations/{conversationId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable String conversationId,
            Authentication auth) {
        String userId = (String) auth.getPrincipal();
        chatService.markAsRead(conversationId, userId);
        return ResponseEntity.noContent().build();
    }
}
