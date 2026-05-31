package com.example.backend.dto.request.photo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PhotosRequest {
    private String id;
    private String imageUrl;
    private Integer width;
    private Integer height;
    private Boolean isLocationVerified;
    private String moderationStatus;
    private String cameraMake;
    private String cameraModel;
    private String lensModel;
    private Integer iso;
    private BigDecimal aperture;
    private String shutterSpeed;
    private BigDecimal focalLength;
    private BigDecimal gpsLatitude;
    private BigDecimal gpsLongitude;
    private String dateTaken;
}
