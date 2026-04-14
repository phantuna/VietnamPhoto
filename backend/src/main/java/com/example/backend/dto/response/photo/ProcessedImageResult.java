package com.example.backend.dto.response.photo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProcessedImageResult {
    private byte[] bytes;
    private Integer width;
    private Integer height;
    private Long fileSize;
}
