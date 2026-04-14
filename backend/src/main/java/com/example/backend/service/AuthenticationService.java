package com.example.backend.service;

import com.example.backend.dto.request.AuthenticationRequest;
import com.example.backend.dto.response.AuthenticationResponse;
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

    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        Users driver = userRepository.findByEmail(request.getEmail());

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if (!encoder.matches(request.getPassword(), driver.getPassword())) {
            throw new AppException(ErrorCode.INVALID_PASSWORD);
        }

//        List<String> permissions = driver.getRoles()
//                .stream()
//                .flatMap(role -> role.getPermissions().stream())
//                .map(p -> p.getPermission_key().name() + "_" + p.getPermission_type().name())
//                .distinct()
//                .toList();


        String token = jwtService.generateToken(driver.getUsername(),driver.getId());

        return AuthenticationResponse.builder()
                .authenticated(true)
                .token(token)
//                .permissions(permissions)
                .build();
    }
}
