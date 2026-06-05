package com.example.backend.repository.chat;

import com.example.backend.entity.ChatMessage;
import com.example.backend.entity.QChatMessage;
import com.example.backend.entity.QUsers;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class ChatMessageRepositoryCustomImpl implements ChatMessageRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ChatMessage> findMessagesWithDetailsByConversationId(String conversationId, Pageable pageable) {
        QChatMessage message = QChatMessage.chatMessage;
        QUsers sender = new QUsers("sender");

        JPAQuery<ChatMessage> query = queryFactory.selectFrom(message)
                .join(message.sender, sender).fetchJoin()
                .where(message.conversation.id.eq(conversationId));

        long total = query.fetchCount();

        List<ChatMessage> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(message.sentAt.desc())
                .fetch();

        return new PageImpl<>(content, pageable, total);
    }
}
