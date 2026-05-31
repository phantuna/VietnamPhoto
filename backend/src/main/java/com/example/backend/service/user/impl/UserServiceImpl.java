package com.example.backend.service.user.impl;

import com.example.backend.dto.request.user.UserRequest;
import com.example.backend.dto.response.user.UserResponse;
import com.example.backend.entity.Users;
import com.example.backend.exception.AppException;
import com.example.backend.exception.ErrorCode;
import com.example.backend.mapper.UserMapper;
import com.example.backend.repository.user.UserRepository;
import com.example.backend.service.user.FollowService;
import com.example.backend.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import com.example.backend.repository.user.RoleRepository;
import com.example.backend.entity.Role;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final FollowService followService;

    @Override
    @Transactional
    public UserResponse createUser(UserRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username đã tồn tại");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email đã tồn tại");
        }

        Users user = new Users();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setBirthday(request.getBirthday());

        // Assign standard USER role automatically
        Role userRole = roleRepository.findById("USER").orElse(null);
        if (userRole != null) {
            user.setRoles(List.of(userRole));
        }

        Users saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Override
    public List<UserResponse> getAll() {
        return userRepository.findAll()
                .stream()
                .map(user -> {
                    UserResponse res = userMapper.toResponse(user);
                    res.setFollowersCount(followService.countFollowers(user.getId()));
                    res.setFollowingCount(followService.countFollowing(user.getId()));
                    return res;
                })
                .toList();
    }

    @Override
    public UserResponse getById(String userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse getMe() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("Unauthenticated");
        }

        String userId = authentication.getName();

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return userMapper.toResponse(user);
    }



    @Override
    @Transactional
    public UserResponse updateUser(String userId, UserRequest request) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            user.setUsername(request.getUsername());
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            user.setEmail(request.getEmail());
        }

        if (request.getBirthday() != null) {
            user.setBirthday(request.getBirthday());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }

        if (request.getDescription() != null) {
            user.setDescription(request.getDescription());
        }

        Users saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteUser(String userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        userRepository.delete(user);
    }
}