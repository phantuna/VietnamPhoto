package com.example.backend.service.chat;

import com.example.backend.dto.response.chat.ChatMessageResponse;
import com.example.backend.dto.response.chat.ConversationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChatService {

    ConversationResponse getOrCreateConversation(String currentUserId, String otherUserId);

    List<ConversationResponse> getMyConversations(String userId);

    Page<ChatMessageResponse> getMessages(String conversationId, String currentUserId, Pageable pageable);


    ChatMessageResponse saveMessage(String senderId, String receiverId, String content);

    void markAsRead(String conversationId, String currentUserId);
}
