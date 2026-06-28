package com.example.backend.dto.response.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    private String id;
    private String avatarUrl;
    private String username;
    private String email;
    private String password;
    private LocalDate birthday;
    private String description;
    private Long followersCount;
    private Long followingCount;
    private Long postsCount;
    private Integer level;
    private Integer reputationScore;
    private java.util.List<String> roles;
}
