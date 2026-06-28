package com.example.backend.repository.chat;

import com.example.backend.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, String>, ChatMessageRepositoryCustom {


    @Modifying
    @Query("""
            UPDATE ChatMessage m
            SET m.isRead = true
            WHERE m.conversation.id = :convId
              AND m.sender.id != :userId
              AND m.isRead = false
            """)
    int markMessagesAsRead(@Param("convId") String convId, @Param("userId") String userId);

    @Query("""
            SELECT COUNT(m) FROM ChatMessage m
            WHERE m.conversation.id = :convId
              AND m.sender.id != :userId
              AND m.isRead = false
            """)
    long countUnread(@Param("convId") String convId, @Param("userId") String userId);


    @Query("""
            SELECT m FROM ChatMessage m
            WHERE m.conversation.id = :convId
            ORDER BY m.sentAt DESC
            LIMIT 1
            """)
    Optional<ChatMessage> findLatestMessage(@Param("convId") String convId);
}
