package com.example.backend.repository.post;

import com.example.backend.entity.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import com.querydsl.jpa.impl.JPAQuery;

import java.util.List;

@RequiredArgsConstructor
public class SavedPostRepositoryCustomImpl implements SavedPostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<SavedPost> findAllSavedPostsWithDetailsByUserId(String userId, Pageable pageable) {
        QSavedPost savedPost = QSavedPost.savedPost;
        QPosts post = QPosts.posts;
        QUsers postUser = new QUsers("postUser");
        QLocations location = QLocations.locations;

        // Note: Do NOT fetchJoin multiple collections like photos and tags at once.
        // Doing so causes MultipleBagFetchException in Hibernate. We let Hibernate lazy load them in the transaction.
        
        JPAQuery<SavedPost> query = queryFactory.selectFrom(savedPost).distinct()
                .leftJoin(savedPost.post, post).fetchJoin()
                .leftJoin(post.user, postUser).fetchJoin()
                .leftJoin(post.location, location).fetchJoin()
                .where(savedPost.user.id.eq(userId)
                        .and(savedPost.deleted.eq(0)));
        
        long total = query.fetchCount();

        List<SavedPost> content = query
                .orderBy(savedPost.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total);
    }
}
