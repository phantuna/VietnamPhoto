package com.example.backend.controller.chat;

import com.example.backend.dto.response.chat.ChatMessageResponse;
import com.example.backend.dto.response.chat.ConversationResponse;
import com.example.backend.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import com.example.backend.dto.response.ApiResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    @GetMapping("/conversations")
    public ApiResponse<List<ConversationResponse>> getMyConversations(Authentication auth) {
        String userId = (String) auth.getPrincipal();
        ApiResponse<List<ConversationResponse>> response = new ApiResponse<>();
        response.setResult(chatService.getMyConversations(userId));
        return response;
    }

    @PostMapping("/conversations/{receiverId}")
    public ApiResponse<ConversationResponse> openConversation(
            @PathVariable String receiverId,
            Authentication auth) {
        String userId = (String) auth.getPrincipal();
        ApiResponse<ConversationResponse> response = new ApiResponse<>();
        response.setResult(chatService.getOrCreateConversation(userId, receiverId));
        return response;
    }


    @GetMapping("/conversations/{conversationId}/messages")
    public ApiResponse<Page<ChatMessageResponse>> getMessages(
            @PathVariable String conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication auth) {
        String userId = (String) auth.getPrincipal();
        PageRequest pageable = PageRequest.of(page, size, Sort.by("sentAt").descending());
        ApiResponse<Page<ChatMessageResponse>> response = new ApiResponse<>();
        response.setResult(chatService.getMessages(conversationId, userId, pageable));
        return response;
    }

    @PutMapping("/conversations/{conversationId}/read")
    public ApiResponse<String> markAsRead(
            @PathVariable String conversationId,
            Authentication auth) {
        String userId = (String) auth.getPrincipal();
        chatService.markAsRead(conversationId, userId);
        ApiResponse<String> response = new ApiResponse<>();
        response.setResult("Đánh dấu đã đọc thành công");
        return response;
    }
}
