package com.example.backend.service.photo.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.backend.entity.Locations;
import com.example.backend.entity.PhotoMetadata;

@ExtendWith(MockitoExtension.class)
public class PhotoVerificationServiceImplTest {

    @InjectMocks
    private PhotoVerificationServiceImpl photoVerificationService;

    private PhotoMetadata metadata;
    private Locations location;

    @BeforeEach
    void setUp() {
        metadata = new PhotoMetadata();
        metadata.setGpsLatitude(BigDecimal.valueOf(21.0285));
        metadata.setGpsLongitude(BigDecimal.valueOf(105.8542));
        metadata.setProvince("Hà Nội");

        location = new Locations();
        location.setLatitude(BigDecimal.valueOf(21.0285));
        location.setLongitude(BigDecimal.valueOf(105.8542));
        location.setLevel(2);
    }

    @Test
    void calculateDistanceMeters_SameLocation_ReturnsZero() {
        double distance = photoVerificationService.calculateDistanceMeters(metadata, location);
        assertThat(distance).isCloseTo(0.0, org.assertj.core.data.Offset.offset(1.0));
    }

    @Test
    void calculateDistanceMeters_MissingCoords_ReturnsNegativeOne() {
        metadata.setGpsLatitude(null);
        double distance = photoVerificationService.calculateDistanceMeters(metadata, location);
        assertThat(distance).isEqualTo(-1.0);
    }

    @Test
    void verifyPhotoLocation_WithinRange_ReturnsTrue() {
        boolean verified = photoVerificationService.verifyPhotoLocation(metadata, location, 100.0);
        assertThat(verified).isTrue();
    }

    @Test
    void isProvinceMatch_ProvinceMatching_ReturnsTrue() {
        // Tạo cấu trúc phân cấp địa điểm: location (level 2) -> parent (level 0, Hà Nội)
        Locations parentProvince = new Locations();
        parentProvince.setLevel(0);
        parentProvince.setName("Thành phố Hà Nội");

        location.setParent(parentProvince);

        boolean match = photoVerificationService.isProvinceMatch(metadata, location);
        assertThat(match).isTrue();
    }
}
