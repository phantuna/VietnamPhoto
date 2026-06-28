package com.example.backend.service.user.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.backend.dto.request.user.ChangePasswordRequest;
import com.example.backend.dto.request.user.UserRequest;
import com.example.backend.dto.response.user.UserResponse;
import com.example.backend.entity.Users;
import com.example.backend.exception.AppException;
import com.example.backend.exception.ErrorCode;
import com.example.backend.mapper.UserMapper;
import com.example.backend.repository.user.RoleRepository;
import com.example.backend.repository.user.UserRepository;
import com.example.backend.service.user.FollowService;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private FollowService followService;

    @InjectMocks
    private UserServiceImpl userService;

    private Users user;

    @BeforeEach
    void setUp() {
        user = new Users();
        user.setId("user-123");
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("encoded_pass");
        user.setDeleted(0);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createUser_ValidRequest_Success() {
        UserRequest request = new UserRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("plain_pass");

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("plain_pass")).thenReturn("encoded_pass");
        when(roleRepository.findById("USER")).thenReturn(Optional.empty());
        when(userRepository.save(any(Users.class))).thenReturn(user);

        UserResponse mockResponse = new UserResponse();
        mockResponse.setId("user-123");
        mockResponse.setUsername("testuser");
        when(userMapper.toResponse(any(Users.class))).thenReturn(mockResponse);

        UserResponse response = userService.createUser(request);

        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo("testuser");
    }

    @Test
    void createUser_UsernameExists_ThrowsAppException() {
        UserRequest request = new UserRequest();
        request.setUsername("testuser");

        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.USER_EXISTED.name());
    }

    @Test
    void createUser_EmailExists_ThrowsAppException() {
        UserRequest request = new UserRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.EMAIL_EXISTED.name());
    }

    @Test
    void getById_UserExists_ReturnsUserResponse() {
        when(userRepository.findById("user-123")).thenReturn(Optional.of(user));
        
        UserResponse mockResponse = new UserResponse();
        mockResponse.setId("user-123");
        when(userMapper.toResponse(user)).thenReturn(mockResponse);

        UserResponse response = userService.getById("user-123");

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("user-123");
    }

    @Test
    void getById_UserNotFound_ThrowsAppException() {
        when(userRepository.findById("user-999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById("user-999"))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.USER_NOT_FOUND.name());
    }

    @Test
    void getMe_Authenticated_ReturnsUserResponse() {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken("user-123", null, java.util.Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(userRepository.findById("user-123")).thenReturn(Optional.of(user));
        
        UserResponse mockResponse = new UserResponse();
        mockResponse.setId("user-123");
        when(userMapper.toResponse(user)).thenReturn(mockResponse);

        UserResponse response = userService.getMe();

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("user-123");
    }

    @Test
    void updateUser_UsernameAlreadyTaken_ThrowsAppException() {
        UserRequest request = new UserRequest();
        request.setUsername("taken_username");

        when(userRepository.findById("user-123")).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("taken_username")).thenReturn(true);

        assertThatThrownBy(() -> userService.updateUser("user-123", request))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.USER_EXISTED.name());
    }

    @Test
    void changePassword_IncorrectOldPassword_ThrowsAppException() {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken("user-123", null, java.util.Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);

        ChangePasswordRequest request = new ChangePasswordRequest("wrong_old_pass", "new_pass", "new_pass");

        when(userRepository.findById("user-123")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong_old_pass", "encoded_pass")).thenReturn(false);

        assertThatThrownBy(() -> userService.changePassword(request))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.INVALID_PASSWORD.name());
    }
}
