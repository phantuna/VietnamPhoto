package com.example.backend.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "photo_metadata")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhotoMetadata {

    @Id
    @Column(name = "photo_id")
    private UUID photoId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "photo_id")
    private Photos photo;

    @Column(name = "camera_make", length = 100)
    private String cameraMake;

    @Column(name = "camera_model", length = 100)
    private String cameraModel;

    @Column(name = "lens_model", length = 100)
    private String lensModel;

    @Column(name = "iso")
    private Integer iso;

    @Column(name = "aperture", precision = 4, scale = 2)
    private BigDecimal aperture;

    @Column(name = "shutter_speed",  columnDefinition = "TEXT")
    private String shutterSpeed;

    @Column(name = "focal_length", precision = 5, scale = 2)
    private BigDecimal focalLength;

    @Column(name = "gps_latitude", precision = 10, scale = 7)
    private BigDecimal gpsLatitude;

    @Column(name = "gps_longitude", precision = 10, scale = 7)
    private BigDecimal gpsLongitude;
}