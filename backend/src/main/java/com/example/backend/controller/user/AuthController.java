package com.example.backend.controller.user;

import com.example.backend.dto.request.user.AuthenticationRequest;
import com.example.backend.dto.request.user.TokenRefreshRequest;
import com.example.backend.dto.request.user.ForgotPasswordRequest;
import com.example.backend.dto.request.user.ResetPasswordRequest;
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

    @PostMapping("/refresh")
    ApiResponse<AuthenticationResponse> refreshToken(@RequestBody TokenRefreshRequest request) {
        ApiResponse<AuthenticationResponse> response = new ApiResponse<>();
        response.setResult(authenticationService.refreshToken(request.getRefreshToken()));
        return response;
    }

    @PostMapping("/forgot-password")
    ApiResponse<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authenticationService.forgotPassword(request.getEmail());
        ApiResponse<String> response = new ApiResponse<>();
        response.setResult("Đã gửi mã OTP qua email");
        return response;
    }

    @PostMapping("/reset-password")
    ApiResponse<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        authenticationService.resetPassword(request.getEmail(), request.getOtp(), request.getNewPassword());
        ApiResponse<String> response = new ApiResponse<>();
        response.setResult("Đổi mật khẩu thành công");
        return response;
    }
}