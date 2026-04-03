package com.example.backend.service.user;


import com.example.backend.dto.request.UserRequest;
import com.example.backend.dto.response.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponse createUser(UserRequest request);

    List<UserResponse> getAll();

    UserResponse getById(UUID userId);

    UserResponse getMe();

    UserResponse updateUser(UUID userId, UserRequest request);

    void deleteUser(UUID userId);
}
