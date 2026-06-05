package com.example.backend.mapper;

import com.example.backend.dto.response.user.UserResponse;
import com.example.backend.entity.Users;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponse toResponse(Users user){
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .avatarUrl(user.getAvatarUrl())
                .birthday(user.getBirthday())
                .description(user.getDescription())
                .level(user.getLevel() != null ? user.getLevel() : 1)
                .reputationScore(Math.max(0, user.getReputationScore() != null ? user.getReputationScore() : 0))
                .roles(user.getRoles() != null ? user.getRoles().stream().map(com.example.backend.entity.Role::getName).toList() : java.util.Collections.emptyList())
                .build();
    }
}
