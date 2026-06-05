package com.example.backend.service.user;

import com.example.backend.dto.response.user.FollowStatusResponse;
import com.example.backend.entity.Users;

import java.util.List;

public interface FollowService {

    FollowStatusResponse toggleFollow(String followerId, String followingId);

    boolean isFollowing(String followerId, String followingId);

    long countFollowers(String userId);

    long countFollowing(String userId);

    List<Users> getFollowers(String userId);

    FollowStatusResponse getFollowStatus(String followerId, String followingId);

    /** Trả về danh sách userId đã follow nhau 2 chiều với currentUserId */
    List<String> getMutualFollowUserIds(String currentUserId);

    /** Trả về danh sách userId mà currentUserId đang follow */
    List<String> getFollowingUserIds(String currentUserId);
}
