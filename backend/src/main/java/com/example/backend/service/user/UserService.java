package com.example.backend.service.user;


import com.example.backend.dto.request.user.UserRequest;
import com.example.backend.dto.response.user.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse createUser(UserRequest request);

    org.springframework.data.domain.Page<UserResponse> getAllUsers(int page, int size);

    UserResponse getById(String userId);

    UserResponse getMe();

    UserResponse updateUser(String userId, UserRequest request);

    void deleteUser(String userId);

    void changePassword(com.example.backend.dto.request.user.ChangePasswordRequest request);
}
