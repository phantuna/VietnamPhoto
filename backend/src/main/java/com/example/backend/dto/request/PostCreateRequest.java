package com.example.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostCreateRequest {

    @NotNull(message = "Địa điểm không được để trống")
    private UUID locationId;

    private String caption;

    private String shootingTip;

    // Client sẽ gửi mảng các string tag, ví dụ: ["hoanghon", "bienhalong"]
    private List<String> tags;
}