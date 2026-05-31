package com.example.backend.controller.chat;

import com.example.backend.dto.request.chat.SendMessageRequest;
import com.example.backend.dto.response.chat.ChatMessageResponse;
import com.example.backend.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

/**
 * STOMP WebSocket controller cho real-time messaging.
 *
 * FE gửi tin nhắn tới: /app/chat.send
 * FE nhận tin nhắn từ: /user/queue/messages  (subscribe phía FE)
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    /**
     * Xử lý khi FE gửi tin nhắn qua STOMP.
     * Principal.getName() = userId (được set bởi WebSocketAuthInterceptor).
     */
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload SendMessageRequest request, Principal principal) {
        if (principal == null) {
            log.warn("Unauthenticated WebSocket message attempt blocked");
            return;
        }

        String senderId = principal.getName();
        String receiverId = request.getReceiverId();

        log.debug("WS message: {} → {} : {}", senderId, receiverId, request.getContent());

        // Lưu vào DB
        ChatMessageResponse savedMessage = chatService.saveMessage(
                senderId, receiverId, request.getContent()
        );

        // Gửi đến NGƯỜI NHẬN qua /topic/messages/{receiverId}
        messagingTemplate.convertAndSend(
                "/topic/messages/" + receiverId,
                savedMessage
        );

        // Gửi lại NGƯỜI GỬI để confirm (hiển thị tin vừa gửi ngay)
        messagingTemplate.convertAndSend(
                "/topic/messages/" + senderId,
                savedMessage
        );
    }
}
