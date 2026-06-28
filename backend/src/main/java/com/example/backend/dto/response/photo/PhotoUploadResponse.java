package com.example.backend.dto.response.photo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PhotoUploadResponse {
    private String photoId;
    private String imageUrl;
    private Boolean locationVerified;
    private Double locationDistanceMeters;

    private String moderationStatus;
    private String moderationMessage;

    private Double moderationScore;

    private ExifDataDto exifData;
}

