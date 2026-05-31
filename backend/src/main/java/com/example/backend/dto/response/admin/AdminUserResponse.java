package com.example.backend.dto.response.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminUserResponse {
    private String id;
    private String username;
    private String email;
    private String avatarUrl;
    private Integer reputationScore;
    private Integer level;
    private Integer deleted;
}
