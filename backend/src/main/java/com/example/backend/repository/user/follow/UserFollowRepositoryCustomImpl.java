package com.example.backend.repository.user.follow;

import com.example.backend.entity.QUserFollow;
import com.example.backend.entity.QUsers;
import com.example.backend.entity.UserFollow;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class UserFollowRepositoryCustomImpl implements UserFollowRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<UserFollow> findFollowersByUserIdWithDetails(String userId) {
        QUserFollow userFollow = QUserFollow.userFollow;
        QUsers follower = new QUsers("follower");
        QUsers following = new QUsers("following");

        return queryFactory.selectFrom(userFollow)
                .join(userFollow.follower, follower).fetchJoin()
                .join(userFollow.following, following).fetchJoin()
                .where(userFollow.following.id.eq(userId)
                        .and(userFollow.deleted.eq(0)))
                .fetch();
    }

}
