package com.example.backend.service;

import com.example.backend.dto.request.user.AuthenticationRequest;
import com.example.backend.dto.response.user.AuthenticationResponse;
import com.example.backend.entity.Users;
import com.example.backend.exception.AppException;
import com.example.backend.exception.ErrorCode;
import com.example.backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        Users user = userRepository.findByEmailIncludeBanned(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (user.getDeleted() != null && user.getDeleted() == 1) {
            throw new AppException(ErrorCode.USER_BANNED);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_PASSWORD);
        }

        java.util.List<String> permissions = new java.util.ArrayList<>();
        if (user.getRoles() != null) {
            user.getRoles().forEach(role -> {
                permissions.add("ROLE_" + role.getId().toUpperCase());
            });
        }
        if (permissions.isEmpty()) {
            permissions.add("ROLE_USER");
        }

        String token = jwtService.generateToken(user.getUsername(), user.getId(), permissions);

        return AuthenticationResponse.builder()
                .authenticated(true)
                .token(token)
                .build();
    }
}
//        List<String> permissions = driver.getRoles()
//                .stream()
//                .flatMap(role -> role.getPermissions().stream())
//                .map(p -> p.getPermission_key().name() + "_" + p.getPermission_type().name())
//                .distinct()
//                .toList();