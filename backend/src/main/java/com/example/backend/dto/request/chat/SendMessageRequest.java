package com.example.backend.dto.request.chat;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendMessageRequest {

    @NotBlank
    private String receiverId;

    @NotBlank
    private String content;
}
