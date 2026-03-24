package com.example.backend.mapper;

import com.example.backend.dto.response.UserResponse;
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
                .build();
    }
}
