package com.example.backend.repository.notification;

import com.example.backend.entity.Notification;
import com.example.backend.entity.QNotification;
import com.example.backend.entity.QPosts;
import com.example.backend.entity.QUsers;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class NotificationRepositoryCustomImpl implements NotificationRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Notification> findNotificationsWithDetailsByReceiverId(String receiverId, Integer deleted, Pageable pageable) {
        QNotification notification = QNotification.notification;
        QUsers actor = new QUsers("actor");
        QUsers receiver = new QUsers("receiver");
        QPosts post = QPosts.posts;

        JPAQuery<Notification> query = queryFactory.selectFrom(notification)
                .leftJoin(notification.actor, actor).fetchJoin()
                .leftJoin(notification.receiver, receiver).fetchJoin()
                .leftJoin(notification.post, post).fetchJoin()
                .where(notification.receiver.id.eq(receiverId)
                        .and(notification.deleted.eq(deleted)));

        long total = query.fetchCount();

        List<Notification> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(notification.createdAtTime.desc())
                .fetch();

        return new PageImpl<>(content, pageable, total);
    }
}
