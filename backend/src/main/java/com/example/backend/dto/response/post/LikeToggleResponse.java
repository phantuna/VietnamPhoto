package com.example.backend.dto.response.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LikeToggleResponse {
    private boolean liked;
    private Long totalLikes;
}