package com.example.backend.dto.response.photo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadedImageInfo {
    private String imageUrl;
    private int width;
    private int height;
    private long fileSize;
}
