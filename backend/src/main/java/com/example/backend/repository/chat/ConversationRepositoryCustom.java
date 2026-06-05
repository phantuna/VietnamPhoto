package com.example.backend.repository.chat;

import com.example.backend.entity.Conversation;
import java.util.List;
import java.util.Optional;

public interface ConversationRepositoryCustom {
    List<Conversation> findAllConversationsWithDetailsByUserId(String userId);
    Optional<Conversation> findConversationBetweenUsersWithDetails(String idA, String idB);
}
