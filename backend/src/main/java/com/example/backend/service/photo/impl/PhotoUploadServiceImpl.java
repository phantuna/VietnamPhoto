package com.example.backend.service.photo.impl;

import com.example.backend.dto.response.location.VietMapLocationResponse;
import com.example.backend.dto.response.photo.*;
import com.example.backend.entity.Locations;
import com.example.backend.entity.PhotoMetadata;
import com.example.backend.entity.Photos;
import com.example.backend.entity.Users;
import com.example.backend.repository.LocationsRepository;
import com.example.backend.repository.PhotosRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.location.VietMapLocationService;
import com.example.backend.service.photo.*;
import com.example.backend.utils.cloudinary.CloudinaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
@Slf4j
@Service
@RequiredArgsConstructor
public class PhotoUploadServiceImpl implements PhotoUploadService {

    private final UserRepository usersRepository;
    private final LocationsRepository locationsRepository;
    private final PhotosRepository photosRepository;

    private final VietMapLocationService vietMapLocationService;
    private final ImageModerationService imageModerationService;
    private final ExifExtractorService exifExtractorService;
    private final ImageProcessingService imageProcessingService;
    private final CloudinaryService cloudinaryService;
    private final PhotoVerificationService photoVerificationService;

    @Qualifier("photoUploadExecutor")
    private final Executor photoUploadExecutor;

    @Override
    @Transactional
    public PhotoUploadResponse uploadPhoto(MultipartFile file, UUID userId, UUID locationId, String caption) {
        return processSinglePhoto(file, userId, locationId, caption);
    }

    @Override
    public List<PhotoUploadResponse> uploadMultiplePhotos(
            List<MultipartFile> files, UUID userId, UUID locationId, String caption) {

        if (files == null || files.isEmpty()) throw new RuntimeException("No files");
        if (files.size() > 10) throw new RuntimeException("Tối đa 10 ảnh");

        List<CompletableFuture<PhotoUploadResponse>> futures = files.stream()
                .map(file -> CompletableFuture.supplyAsync(() -> {
                    try {
                        return processSinglePhoto(file, userId, locationId, caption);
                    } catch (Exception e) {
                        log.error("Lỗi ảnh {}: {}", file.getOriginalFilename(), e.getMessage());
                        throw new RuntimeException("Lỗi: " + file.getOriginalFilename(), e);
                    }
                }, photoUploadExecutor))
                .toList();

        try {
            return futures.stream()
                    .map(CompletableFuture::join)
                    .toList();} catch (Exception e) {
            throw new RuntimeException("Lỗi khi tải lên hàng loạt", e);
        }
    }


    private PhotoUploadResponse processSinglePhoto(
            MultipartFile file,
            UUID userId,
            UUID locationId,
            String caption
    ) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Locations location = locationsRepository.findById(locationId)
                .orElseThrow(() -> new RuntimeException("Location not found"));

        ModerationResult moderation = imageModerationService.moderate(file);
        if (moderation.isBlocked()) {
            throw new RuntimeException(
                    moderation.getReason() != null
                            ? moderation.getReason()
                            : "Image contains sensitive content"
            );
        }

        ExifDataDto exifData = exifExtractorService.extract(file);
        VietMapLocationResponse resolvedAddress = null;
        if (exifData.getGpsLatitude() != null && exifData.getGpsLongitude() != null) {
            resolvedAddress = vietMapLocationService.reverse(
                    exifData.getGpsLatitude(),
                    exifData.getGpsLongitude()
            );
        }
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

        boolean verified = false;
        if (metadata.getGpsLatitude() != null && metadata.getGpsLongitude() != null) {
            double distanceMeters = photoVerificationService.calculateDistanceMeters(metadata, location);
            verified = distanceMeters >= 0 && distanceMeters <= 300;
        }
        if (resolvedAddress != null) {
            metadata.setAddress(resolvedAddress.getDisplay());
            metadata.setProvince(resolvedAddress.getProvince());
            metadata.setDistrict(resolvedAddress.getDistrict());
            metadata.setWard(resolvedAddress.getWard());
        }
        String originalFilename = file.getOriginalFilename();
        boolean isHeic = originalFilename != null && originalFilename.toLowerCase().matches(".*\\.(heic|heif)$");
        String publicId = "user_" + user.getId() + "/photo_" + UUID.randomUUID().toString();

        String imageUrl;
        int finalWidth;
        int finalHeight;
        long finalFileSize;

        if (isHeic) {
            try {
                Map<?, ?> uploadResult = cloudinaryService.uploadHeicAndConvert(file.getBytes(), publicId);
                imageUrl = uploadResult.get("secure_url").toString();
                finalWidth = Integer.parseInt(uploadResult.get("width").toString());
                finalHeight = Integer.parseInt(uploadResult.get("height").toString());
                finalFileSize = Long.parseLong(uploadResult.get("bytes").toString());
            } catch (Exception e) {
                throw new RuntimeException("Lỗi khi xử lý file HEIC: " + e.getMessage(), e);
            }
        } else {
            ProcessedImageResult processed = imageProcessingService.process(file, 1600, 0.82f);
            imageUrl = cloudinaryService.uploadImage(processed.getBytes(), publicId);
            finalWidth = processed.getWidth();
            finalHeight = processed.getHeight();
            finalFileSize = processed.getFileSize();
        }

        Photos photo = new Photos();
        photo.setUser(user);
        photo.setLocation(location);
        photo.setCaption(caption);
        photo.setImageUrl(imageUrl);
        photo.setWidth(finalWidth);
        photo.setHeight(finalHeight);
        photo.setFileSize(finalFileSize);
        photo.setLocationVerified(verified);

        metadata.setPhoto(photo);
        photo.setMetadata(metadata);

        Photos savedPhoto = photosRepository.save(photo);

        return PhotoUploadResponse.builder()
                .photoId(savedPhoto.getId().toString())
                .imageUrl(savedPhoto.getImageUrl())
                .locationVerified(savedPhoto.getLocationVerified())
                .moderationMessage(moderation.isWarning() ? moderation.getReason() : null)
                .build();
    }

    @Override
    @Transactional
    public void deletePhoto(UUID photoId, UUID userId) {
        Photos photo = photosRepository.findById(photoId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ảnh với ID này"));

        if (!photo.getUser().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền xóa bức ảnh này");
        }

        String publicId = extractPublicIdFromUrl(photo.getImageUrl());

        if (publicId != null && !publicId.isEmpty()) {
            try {
                cloudinaryService.deleteImage(publicId);
            } catch (Exception e) {
                log.error("Lỗi khi xóa ảnh trên Cloudinary (Public ID: {}): {}", publicId, e.getMessage());
            }
        }

        photosRepository.delete(photo);
    }
    private String extractPublicIdFromUrl(String imageUrl) {
        if (imageUrl == null || !imageUrl.contains("/upload/")) {
            return null;
        }
        try {
            String afterUpload = imageUrl.substring(imageUrl.indexOf("/upload/") + 8);
            if (afterUpload.matches("v\\d+/.*")) {
                afterUpload = afterUpload.replaceFirst("v\\d+/", "");
            }

            int lastDotIndex = afterUpload.lastIndexOf(".");
            if (lastDotIndex != -1) {
                return afterUpload.substring(0, lastDotIndex);
            }
            return afterUpload;
        } catch (Exception e) {
            log.error("Không thể bóc tách publicId từ URL: {}", imageUrl, e);
            return null;
        }
    }
}