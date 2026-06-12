package com.example.backend.service.user.impl;

import com.example.backend.dto.response.user.FollowStatusResponse;
import com.example.backend.entity.UserFollow;
import com.example.backend.entity.Users;
import com.example.backend.event.NewFollowerEvent;
import com.example.backend.exception.AppException;
import com.example.backend.exception.ErrorCode;
import com.example.backend.repository.user.UserFollowRepository;
import com.example.backend.repository.user.UserRepository;
import com.example.backend.service.user.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final UserFollowRepository userFollowRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public FollowStatusResponse toggleFollow(String followerId, String followingId) {
        if (followerId.equals(followingId)) {
            throw new AppException(ErrorCode.CANNOT_FOLLOW_YOURSELF);
        }

        Users follower = userRepository.findById(followerId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Users following = userRepository.findById(followingId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Tìm kể cả record đã soft-delete
        Optional<UserFollow> existing = userFollowRepository.findByFollowerIdAndFollowingId(followerId, followingId);

        if (existing.isPresent()) {
            UserFollow follow = existing.get();

            if (follow.getDeleted() == 0) {
                // Đang follow → unfollow (soft delete)
                follow.setDeleted(1);
                userFollowRepository.save(follow);

                long followers = userFollowRepository.countByFollowingIdAndDeleted(followingId, 0);
                long following2 = userFollowRepository.countByFollowerIdAndDeleted(followerId, 0);
                return FollowStatusResponse.builder()
                        .following(false)
                        .followersCount(followers)
                        .followingCount(following2)
                        .build();
            } else {
                // Đã unfollow trước đó → reactivate (tránh vi phạm unique constraint)
                follow.setDeleted(0);
                userFollowRepository.save(follow);

                eventPublisher.publishEvent(new NewFollowerEvent(this, follower, following));

                long followers = userFollowRepository.countByFollowingIdAndDeleted(followingId, 0);
                long following2 = userFollowRepository.countByFollowerIdAndDeleted(followerId, 0);
                return FollowStatusResponse.builder()
                        .following(true)
                        .followersCount(followers)
                        .followingCount(following2)
                        .build();
            }
        } else {
            // Chưa follow lần nào → tạo mới
            UserFollow newFollow = new UserFollow();
            newFollow.setFollower(follower);
            newFollow.setFollowing(following);
            userFollowRepository.save(newFollow);

            eventPublisher.publishEvent(new NewFollowerEvent(this, follower, following));

            long followers = userFollowRepository.countByFollowingIdAndDeleted(followingId, 0);
            long following2 = userFollowRepository.countByFollowerIdAndDeleted(followerId, 0);
            return FollowStatusResponse.builder()
                    .following(true)
                    .followersCount(followers)
                    .followingCount(following2)
                    .build();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFollowing(String followerId, String followingId) {
        return userFollowRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countFollowers(String userId) {
        return userFollowRepository.countByFollowingIdAndDeleted(userId, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public long countFollowing(String userId) {
        return userFollowRepository.countByFollowerIdAndDeleted(userId, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Users> getFollowers(String userId) {
        return userFollowRepository.findFollowersByUserIdWithDetails(userId)
                .stream().map(UserFollow::getFollower).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public FollowStatusResponse getFollowStatus(String followerId, String followingId) {
        boolean following = userFollowRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
        long followersCount = userFollowRepository.countByFollowingIdAndDeleted(followingId, 0);
        long followingCount = userFollowRepository.countByFollowerIdAndDeleted(followerId, 0);
        return FollowStatusResponse.builder()
                .following(following)
                .followersCount(followersCount)
                .followingCount(followingCount)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getMutualFollowUserIds(String currentUserId) {
        return userFollowRepository.findMutualFollowUserIds(currentUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getFollowingUserIds(String currentUserId) {
        return userFollowRepository.findFollowingUserIds(currentUserId);
    }
}
