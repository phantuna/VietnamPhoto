package com.example.backend.dto.response.photo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModerationResult {
    private boolean blocked;
    private boolean warning;
    private String reason;
    private Double score;
}