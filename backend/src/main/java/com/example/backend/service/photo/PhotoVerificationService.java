package com.example.backend.service.photo;

import com.example.backend.entity.Locations;
import com.example.backend.entity.PhotoMetadata;

public interface PhotoVerificationService {
    double calculateDistanceMeters(PhotoMetadata metadata, Locations location);
    boolean verifyPhotoLocation(PhotoMetadata metadata, Locations location, double allowedDistanceMeters);
}
