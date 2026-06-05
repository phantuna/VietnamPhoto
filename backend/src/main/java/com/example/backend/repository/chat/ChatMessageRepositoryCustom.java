package com.example.backend.repository.chat;

import com.example.backend.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatMessageRepositoryCustom {
    Page<ChatMessage> findMessagesWithDetailsByConversationId(String conversationId, Pageable pageable);
}
