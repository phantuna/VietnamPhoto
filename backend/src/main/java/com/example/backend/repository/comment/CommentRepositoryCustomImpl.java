package com.example.backend.repository.comment;

import com.example.backend.entity.Comment;
import com.example.backend.entity.QComment;
import com.example.backend.entity.QUsers;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class CommentRepositoryCustomImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Comment> findCommentsByPostIdWithDetails(String postId, Pageable pageable) {
        QComment comment = QComment.comment;
        QUsers user = new QUsers("user");

        // Fetch parent comments with their authors eagerly
        // Note: we don't fetch join the replies here to avoid HHH000104 pagination in memory issue
        JPAQuery<Comment> query = queryFactory.selectFrom(comment)
                .leftJoin(comment.user, user).fetchJoin()
                .where(comment.post.id.eq(postId)
                        .and(comment.parentComment.isNull()));

        long total = query.fetchCount();

        List<Comment> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(comment.createdDate.desc())
                .fetch();

        return new PageImpl<>(content, pageable, total);
    }
}
