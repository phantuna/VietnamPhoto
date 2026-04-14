package com.example.backend.dto.response.photo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExifDataDto {
    private String cameraMake;
    private String cameraModel;
    private String lensModel;

    private Integer iso;
    private BigDecimal aperture;
    private String shutterSpeed;
    private BigDecimal focalLength;

    private BigDecimal gpsLatitude;
    private BigDecimal gpsLongitude;
    private LocalDateTime dateTaken;
}