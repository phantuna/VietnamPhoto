package com.example.backend.dto.response.photo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PhotoUploadResponse {
    private String photoId;
    private String imageUrl;
    private Boolean locationVerified;
    private Double locationDistanceMeters;
    private String moderationMessage;
}
