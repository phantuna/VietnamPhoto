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
}
