package com.example.backend.dto.request.user;

import lombok.Data;

@Data
public class TokenRefreshRequest {
    private String refreshToken;
}
