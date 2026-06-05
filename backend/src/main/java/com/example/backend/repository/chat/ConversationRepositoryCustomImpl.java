package com.example.backend.repository.chat;

import com.example.backend.entity.Conversation;
import com.example.backend.entity.QConversation;
import com.example.backend.entity.QUsers;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class ConversationRepositoryCustomImpl implements ConversationRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Conversation> findAllConversationsWithDetailsByUserId(String userId) {
        QConversation conversation = QConversation.conversation;
        QUsers user1 = new QUsers("user1");
        QUsers user2 = new QUsers("user2");

        return queryFactory.selectFrom(conversation)
                .leftJoin(conversation.user1, user1).fetchJoin()
                .leftJoin(conversation.user2, user2).fetchJoin()
                .where(conversation.user1.id.eq(userId)
                        .or(conversation.user2.id.eq(userId)))
                .orderBy(conversation.modifiedDate.desc())
                .fetch();
    }

    @Override
    public Optional<Conversation> findConversationBetweenUsersWithDetails(String idA, String idB) {
        QConversation conversation = QConversation.conversation;
        QUsers user1 = new QUsers("user1");
        QUsers user2 = new QUsers("user2");

        Conversation result = queryFactory.selectFrom(conversation)
                .leftJoin(conversation.user1, user1).fetchJoin()
                .leftJoin(conversation.user2, user2).fetchJoin()
                .where(conversation.user1.id.eq(idA).and(conversation.user2.id.eq(idB))
                        .or(conversation.user1.id.eq(idB).and(conversation.user2.id.eq(idA))))
                .fetchFirst();

        return Optional.ofNullable(result);
    }
}
