package com.example.backend.service;

import com.example.backend.dto.request.UserRequest;
import com.example.backend.dto.response.UserResponse;
import com.example.backend.entity.Users;
import com.example.backend.exception.AppException;
import com.example.backend.exception.ErrorCode;
import com.example.backend.repository.UserRepository;
import com.example.backend.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService  {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserResponse createUser(UserRequest response){
        Users user = new Users();
        user.setUsername(response.getUsername());
        user.setEmail(response.getEmail());
        user.setPassword(passwordEncoder.encode(response.getPassword()));
        user.setBirthday(response.getBirthday());

        Users saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    public List<UserResponse> getAll(){
        List<Users> saved = userRepository.findAll();
        return saved.stream()
                .map(userMapper::toResponse)
                .toList();
    }

    public UserResponse updateUser(UUID userId,UserRequest request){
        Users user = userRepository.findById(userId)
                .orElseThrow(()->new RuntimeException("Khong tim thay user"));
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setBirthday(request.getBirthday());

        Users saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    public void deleteUser(UUID userId){
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        userRepository.delete(user);
    }
}
