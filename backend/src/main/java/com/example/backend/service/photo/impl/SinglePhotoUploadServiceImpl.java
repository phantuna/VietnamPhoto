package com.example.backend.service.photo.impl;

import com.example.backend.dto.response.location.VietMapLocationResponse;
import com.example.backend.dto.response.photo.*;
import com.example.backend.entity.Locations;
import com.example.backend.entity.PhotoMetadata;
import com.example.backend.entity.Photos;
import com.example.backend.entity.Users;
import com.example.backend.repository.location.LocationsRepository;
import com.example.backend.repository.photo.PhotosRepository;
import com.example.backend.repository.user.UserRepository;
import com.example.backend.service.location.VietMapLocationService;
import com.example.backend.service.photo.ExifExtractorService;
import com.example.backend.service.photo.ImageModerationService;
import com.example.backend.service.photo.ImageProcessingService;
import com.example.backend.service.photo.PhotoVerificationService;
import com.example.backend.service.photo.SinglePhotoUploadService;
import com.example.backend.utils.cloudinary.CloudinaryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SinglePhotoUploadServiceImpl implements SinglePhotoUploadService {

    private final UserRepository usersRepository;
    private final LocationsRepository locationsRepository;
    private final PhotosRepository photosRepository;

    private final VietMapLocationService vietMapLocationService;
    private final ImageModerationService imageModerationService;
    private final ExifExtractorService exifExtractorService;
    private final ImageProcessingService imageProcessingService;
    private final CloudinaryService cloudinaryService;
    private final PhotoVerificationService photoVerificationService;

    @Override
    @Transactional
    public PhotoUploadResponse uploadSingle(
            MultipartFile file,
            String userId
    ) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ModerationResult moderation = imageModerationService.moderate(file);
        if (moderation.isBlocked()) {
            throw new RuntimeException(moderation.getReason() != null ? moderation.getReason() : "Nội dung vi phạm");
        }

        ExifDataDto exifData = exifExtractorService.extract(file);

        VietMapLocationResponse resolvedAddress = null;
        if (exifData.getGpsLatitude() != null && exifData.getGpsLongitude() != null) {
            resolvedAddress = vietMapLocationService.reverse(exifData.getGpsLatitude(), exifData.getGpsLongitude());
        }

        PhotoMetadata metadata = buildMetadata(exifData, resolvedAddress);
        UploadedImageInfo uploadedImage = uploadImage(file, user);

        Photos savedPhoto = savePhoto(metadata, uploadedImage, false);
        log.info("uploadSingle file={} thread={}",
                file.getOriginalFilename(),
                Thread.currentThread().getName());
        return buildResponse(savedPhoto, moderation,exifData);
    }

    private Users getUser(String userId) {
        return usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Locations getLocation(String locationId) {
        return locationsRepository.findById(locationId)
                .orElseThrow(() -> new RuntimeException("Location not found"));
    }

    private ModerationResult moderateImage(MultipartFile file) {
        ModerationResult moderation = imageModerationService.moderate(file);
        if (moderation.isBlocked()) {
            throw new RuntimeException(
                    moderation.getReason() != null
                            ? moderation.getReason()
                            : "Image contains sensitive content"
            );
        }
        return moderation;
    }

    private ExifDataDto extractExif(MultipartFile file) {
        return exifExtractorService.extract(file);
    }

    private VietMapLocationResponse resolveAddress(ExifDataDto exifData) {
        if (exifData.getGpsLatitude() == null || exifData.getGpsLongitude() == null) {
            return null;
        }
        return vietMapLocationService.reverse(
                exifData.getGpsLatitude(),
                exifData.getGpsLongitude()
        );
    }

    private PhotoMetadata buildMetadata(ExifDataDto exifData, VietMapLocationResponse resolvedAddress) {
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

//        if (resolvedAddress != null) {
//            metadata.setAddress(resolvedAddress.getDisplay());
//            metadata.setProvince(resolvedAddress.getProvince());
//            metadata.setDistrict(resolvedAddress.getDistrict());
//            metadata.setWard(resolvedAddress.getWard());
//        }

        return metadata;
    }


    private UploadedImageInfo uploadImage(MultipartFile file, Users user) {
        String originalFilename = file.getOriginalFilename();
        boolean isHeic = originalFilename != null && originalFilename.toLowerCase().matches(".*\\.(heic|heif)$");
        String publicId = "user_" + user.getId() + "/photo_" + UUID.randomUUID();

        if (isHeic) {
            try {
                Map<?, ?> uploadResult = cloudinaryService.uploadHeicAndConvert(file.getBytes(), publicId);
                return new UploadedImageInfo(
                        uploadResult.get("secure_url").toString(),
                        Integer.parseInt(uploadResult.get("width").toString()),
                        Integer.parseInt(uploadResult.get("height").toString()),
                        Long.parseLong(uploadResult.get("bytes").toString())
                );
            } catch (Exception e) {
                throw new RuntimeException("Lỗi file HEIC: " + e.getMessage(), e);
            }
        }

        ProcessedImageResult processed = imageProcessingService.process(file, 1600, 0.82f);
        String imageUrl = cloudinaryService.uploadImage(processed.getBytes(), publicId);
        return new UploadedImageInfo(imageUrl, processed.getWidth(), processed.getHeight(), processed.getFileSize());
    }

    private Photos savePhoto(
            PhotoMetadata metadata,
            UploadedImageInfo uploadedImage,
            boolean verified
    ) {
        Photos photo = new Photos();

        photo.setImageUrl(uploadedImage.getImageUrl());
        photo.setWidth(uploadedImage.getWidth());
        photo.setHeight(uploadedImage.getHeight());
        photo.setFileSize(uploadedImage.getFileSize());
        photo.setIsLocationVerified(verified);

        metadata.setPhoto(photo);
        photo.setMetadata(metadata);

        return photosRepository.save(photo);
    }

    private PhotoUploadResponse buildResponse(Photos savedPhoto, ModerationResult moderation , ExifDataDto exifData) {
        return PhotoUploadResponse.builder()
                .photoId(savedPhoto.getId().toString())
                .imageUrl(savedPhoto.getImageUrl())
                .locationVerified(savedPhoto.getIsLocationVerified())
                .moderationMessage(moderation.isWarning() ? moderation.getReason() : null)
                .exifData(exifData)
                .build();
    }
}