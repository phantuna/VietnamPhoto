package com.example.backend.dto.request.post;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostCreateRequest {

    @NotNull(message = "Địa điểm không được để trống")
    private String locationId;

    private String caption;

    private String shootingTip;

    private Boolean forceCreate;
    
    private Double manualLatitude;
    private Double manualLongitude;
    
    // Client sẽ gửi mảng các string tag, ví dụ: ["hoanghon", "bienhalong"]
    private List<String> tags;
    private List<String> photoIds;
}