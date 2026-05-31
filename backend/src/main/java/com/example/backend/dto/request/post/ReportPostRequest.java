package com.example.backend.dto.request.post;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReportPostRequest {
    @NotBlank(message = "Reason is required")
    private String reason;
}
