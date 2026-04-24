package com.example.backend.mapper;

import com.example.backend.dto.response.photo.ExifDataDto;
import com.example.backend.dto.response.photo.PhotoUploadResponse;
import com.example.backend.entity.PhotoMetadata;
import com.example.backend.entity.Photos;
import org.springframework.stereotype.Component;

@Component
public class PhotoMapper {

    public PhotoUploadResponse toResponse(Photos photo) {
        if (photo == null) {
            return null;
        }

        // 1. Chuyển đổi Metadata sang ExifDataDto
        ExifDataDto exifDto = null;
        if (photo.getMetadata() != null) {
            PhotoMetadata meta = photo.getMetadata();
            exifDto = new ExifDataDto();
            exifDto.setCameraMake(meta.getCameraMake());
            exifDto.setCameraModel(meta.getCameraModel());
            exifDto.setLensModel(meta.getLensModel());
            exifDto.setIso(meta.getIso());
            exifDto.setAperture(meta.getAperture());
            exifDto.setShutterSpeed(meta.getShutterSpeed());
            exifDto.setFocalLength(meta.getFocalLength());
            exifDto.setGpsLatitude(meta.getGpsLatitude());
            exifDto.setGpsLongitude(meta.getGpsLongitude());
            exifDto.setDateTaken(meta.getDateTaken());
        }

        // 2. Build Response
        return PhotoUploadResponse.builder()
                .photoId(photo.getId() != null ? photo.getId().toString() : null)
                .imageUrl(photo.getImageUrl())
                .locationVerified(photo.getIsLocationVerified())

                .moderationMessage(null)
                .exifData(exifDto)
                .build();
    }
}