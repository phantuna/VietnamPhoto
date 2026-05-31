package com.example.backend.dto.response.user;

import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class AuthenticationResponse {
    private boolean authenticated;
    private String token;
    private List<String> permissions;
}
