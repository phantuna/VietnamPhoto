package com.example.backend.mapper;

import com.example.backend.dto.response.photo.ExifDataDto;
import com.example.backend.dto.response.photo.PhotoUploadResponse;
import com.example.backend.entity.PhotoMetadata;
import com.example.backend.entity.Photos;
import org.springframework.stereotype.Component;

@Component
public class PhotoMapper {

    public PhotoUploadResponse toResponse(Photos photo) {
        return toResponse(photo, null);
    }

    public PhotoUploadResponse toResponse(Photos photo, String moderationMessage) {
        if (photo == null) {
            return null;
        }

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
            exifDto.setAddress(meta.getAddress());
            exifDto.setProvince(meta.getProvince());
            exifDto.setDistrict(meta.getDistrict());
            exifDto.setWard(meta.getWard());
        }

        return PhotoUploadResponse.builder()
                .photoId(photo.getId() != null ? photo.getId().toString() : null)
                .imageUrl(photo.getImageUrl())
                .locationVerified(photo.getIsLocationVerified())
                .moderationStatus(photo.getModerationStatus())
                .moderationMessage(moderationMessage)
                .moderationScore(photo.getModerationScore())
                .exifData(exifDto)
                .build();
    }

    public PhotoMetadata toPhotoMetadata(ExifDataDto exifData, com.example.backend.dto.response.location.VietMapLocationResponse resolvedAddress) {
        PhotoMetadata metadata = new PhotoMetadata();
        metadata.setCameraMake(exifData.getCameraMake());
        metadata.setCameraModel(exifData.getCameraModel());
        metadata.setLensModel(exifData.getLensModel());
        metadata.setIso(exifData.getIso());
        metadata.setAperture(exifData.getAperture());
        metadata.setShutterSpeed(exifData.getShutterSpeed());
        metadata.setFocalLength(exifData.getFocalLength());
        metadata.setGpsLatitude(exifData.getGpsLatitude());
        metadata.setGpsLongitude(exifData.getGpsLongitude());
        metadata.setDateTaken(exifData.getDateTaken());

        if (resolvedAddress != null) {
            metadata.setAddress(resolvedAddress.getDisplay());
            metadata.setProvince(resolvedAddress.getProvince());
            metadata.setDistrict(resolvedAddress.getDistrict());
            metadata.setWard(resolvedAddress.getWard());
        }

        return metadata;
    }
}