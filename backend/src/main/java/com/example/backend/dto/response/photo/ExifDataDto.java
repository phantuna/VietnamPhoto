package com.example.backend.dto.response.photo;

import lombok.Data;

import java.math.BigDecimal;

@Data
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
}