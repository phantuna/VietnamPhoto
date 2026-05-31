package com.example.backend.dto.request.chat;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendMessageRequest {

    /** ID của người nhận tin nhắn */
    @NotBlank
    private String receiverId;

    /** Nội dung tin nhắn */
    @NotBlank
    private String content;
}
