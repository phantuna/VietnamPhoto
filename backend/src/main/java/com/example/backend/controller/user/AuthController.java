package com.example.backend.controller.user;

import com.example.backend.dto.request.user.AuthenticationRequest;
import com.example.backend.dto.response.ApiResponse;
import com.example.backend.dto.response.user.AuthenticationResponse;
import com.example.backend.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    ApiResponse<AuthenticationResponse> login( @RequestBody AuthenticationRequest request) {

        ApiResponse< AuthenticationResponse> response = new ApiResponse<>();
        response.setResult(authenticationService.authenticate(request));
        return response;
    }
}