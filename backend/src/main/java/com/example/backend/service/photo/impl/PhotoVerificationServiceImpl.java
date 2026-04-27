package com.example.backend.service.photo.impl;

import com.example.backend.entity.Locations;
import com.example.backend.entity.PhotoMetadata;
import com.example.backend.service.photo.PhotoVerificationService;
import com.example.backend.utils.GeoUtils;
import org.springframework.stereotype.Service;

@Service
public class PhotoVerificationServiceImpl implements PhotoVerificationService {

    @Override
    public double calculateDistanceMeters(PhotoMetadata metadata, Locations location) {
        if (metadata == null
                || metadata.getGpsLatitude() == null
                || metadata.getGpsLongitude() == null
                || location == null
                || location.getLatitude() == null
                || location.getLongitude() == null) {
            return -1;
        }

        return GeoUtils.distanceInMeters(
                metadata.getGpsLatitude().doubleValue(),
                metadata.getGpsLongitude().doubleValue(),
                location.getLatitude().doubleValue(),
                location.getLongitude().doubleValue()
        );
    }

    @Override
    public boolean verifyPhotoLocation(PhotoMetadata metadata, Locations location, double allowedDistanceMeters) {
        double distance = calculateDistanceMeters(metadata, location);
        return distance >= 0 && distance <= allowedDistanceMeters;
    }

    @Override
    public boolean isProvinceMatch(PhotoMetadata metadata, Locations location) {
        if (metadata == null || metadata.getProvince() == null || location == null) {
            return false;
        }

        String locationProvince = extractProvinceName(location);
        if (locationProvince == null) return false;

        // So sánh tương đối (ví dụ: "Thành phố Hà Nội" vs "Hà Nội")
        String photoProvince = metadata.getProvince().toLowerCase();
        String locProvince = locationProvince.toLowerCase();

        return photoProvince.contains(locProvince) || locProvince.contains(photoProvince);
    }

    private String extractProvinceName(Locations location) {
        Locations current = location;
        while (current != null) {
            if (current.getLevel() != null && current.getLevel() == 0) {
                return current.getName();
            }
            current = current.getParent();
        }
        return null;
    }
}
