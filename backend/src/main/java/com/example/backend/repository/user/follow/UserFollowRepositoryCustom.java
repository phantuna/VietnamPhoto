package com.example.backend.repository.user;

import com.example.backend.entity.UserFollow;

import java.util.List;

public interface UserFollowRepositoryCustom {
    List<UserFollow> findFollowersByUserIdWithDetails(String userId);
    List<UserFollow> findFollowingByUserIdWithDetails(String userId);
}
