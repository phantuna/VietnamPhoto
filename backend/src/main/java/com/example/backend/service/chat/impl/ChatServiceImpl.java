package com.example.backend.service.chat.impl;

import com.example.backend.dto.response.chat.ChatMessageResponse;
import com.example.backend.dto.response.chat.ConversationResponse;
import com.example.backend.entity.ChatMessage;
import com.example.backend.entity.Conversation;
import com.example.backend.entity.Users;
import com.example.backend.repository.chat.ChatMessageRepository;
import com.example.backend.repository.chat.ConversationRepository;
import com.example.backend.repository.user.UserRepository;
import com.example.backend.service.chat.ChatService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ConversationRepository conversationRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    // ─────────────────────────────────────────────────────
    //  Lấy hoặc tạo conversation
    // ─────────────────────────────────────────────────────
    @Override
    @Transactional
    public ConversationResponse getOrCreateConversation(String currentUserId, String otherUserId) {
        Conversation conv = conversationRepository
                .findConversationBetweenUsersWithDetails(currentUserId, otherUserId)
                .orElseGet(() -> createNewConversation(currentUserId, otherUserId));

        Users other = getOtherUser(conv, currentUserId);
        long unread = chatMessageRepository.countUnread(conv.getId(), currentUserId);
        var lastMsg = chatMessageRepository.findLatestMessage(conv.getId()).orElse(null);

        return buildConversationResponse(conv, other, unread, lastMsg);
    }

    // ─────────────────────────────────────────────────────
    //  Inbox
    // ─────────────────────────────────────────────────────
    @Override
    public List<ConversationResponse> getMyConversations(String userId) {
        return conversationRepository.findAllConversationsWithDetailsByUserId(userId).stream()
                .map(conv -> {
                    Users other = getOtherUser(conv, userId);
                    long unread = chatMessageRepository.countUnread(conv.getId(), userId);
                    var lastMsg = chatMessageRepository.findLatestMessage(conv.getId()).orElse(null);
                    return buildConversationResponse(conv, other, unread, lastMsg);
                })
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────────
    //  Lịch sử tin nhắn
    // ─────────────────────────────────────────────────────
    @Override
    public Page<ChatMessageResponse> getMessages(String conversationId, String currentUserId, Pageable pageable) {
        return chatMessageRepository
                .findMessagesWithDetailsByConversationId(conversationId, pageable)
                .map(this::toMessageResponse);
    }

    // ─────────────────────────────────────────────────────
    //  Lưu và gửi tin nhắn
    // ─────────────────────────────────────────────────────
    @Override
    @Transactional
    public ChatMessageResponse saveMessage(String senderId, String receiverId, String content) {
        // Lấy hoặc tạo conversation
        Conversation conv = conversationRepository
                .findConversationBetweenUsersWithDetails(senderId, receiverId)
                .orElseGet(() -> createNewConversation(senderId, receiverId));

        Users sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("User not found: " + senderId));

        ChatMessage msg = ChatMessage.builder()
                .conversation(conv)
                .sender(sender)
                .content(content)
                .isRead(false)
                .build();

        ChatMessage saved = chatMessageRepository.save(msg);
        return toMessageResponse(saved);
    }

    // ─────────────────────────────────────────────────────
    //  Đánh dấu đã đọc
    // ─────────────────────────────────────────────────────
    @Override
    @Transactional
    public void markAsRead(String conversationId, String currentUserId) {
        chatMessageRepository.markMessagesAsRead(conversationId, currentUserId);
    }

    // ─────────────────────────────────────────────────────
    //  Helper methods
    // ─────────────────────────────────────────────────────

    /**
     * Tạo mới conversation, đảm bảo user1.id <= user2.id.
     */
    private Conversation createNewConversation(String idA, String idB) {
        String smaller = idA.compareTo(idB) <= 0 ? idA : idB;
        String larger  = idA.compareTo(idB) <= 0 ? idB : idA;

        Users user1 = userRepository.findById(smaller)
                .orElseThrow(() -> new RuntimeException("User not found: " + smaller));
        Users user2 = userRepository.findById(larger)
                .orElseThrow(() -> new RuntimeException("User not found: " + larger));

        Conversation conv = Conversation.builder()
                .user1(user1)
                .user2(user2)
                .build();
        return conversationRepository.save(conv);
    }

    /** Lấy user còn lại trong conversation (không phải current user). */
    private Users getOtherUser(Conversation conv, String currentUserId) {
        return conv.getUser1().getId().equals(currentUserId)
                ? conv.getUser2()
                : conv.getUser1();
    }

    private ConversationResponse buildConversationResponse(Conversation conv,
                                                            Users other,
                                                            long unread,
                                                            ChatMessage lastMsg) {
        return ConversationResponse.builder()
                .id(conv.getId())
                .otherUserId(other.getId())
                .otherUserUsername(other.getUsername())
                .otherUserAvatarUrl(other.getAvatarUrl())
                .lastMessageContent(lastMsg != null ? lastMsg.getContent() : null)
                .lastMessageAt(lastMsg != null ? lastMsg.getSentAt() : null)
                .unreadCount(unread)
                .build();
    }

    private ChatMessageResponse toMessageResponse(ChatMessage msg) {
        return ChatMessageResponse.builder()
                .id(msg.getId())
                .conversationId(msg.getConversation().getId())
                .senderId(msg.getSender().getId())
                .senderUsername(msg.getSender().getUsername())
                .senderAvatarUrl(msg.getSender().getAvatarUrl())
                .content(msg.getContent())
                .isRead(msg.getIsRead())
                .sentAt(msg.getSentAt())
                .build();
    }
}
