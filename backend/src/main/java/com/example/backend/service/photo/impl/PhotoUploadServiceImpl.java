package com.example.backend.service.photo.impl;

import com.example.backend.dto.response.location.VietMapLocationResponse;
import com.example.backend.dto.response.photo.ExifDataDto;
import com.example.backend.dto.response.photo.ModerationResult;
import com.example.backend.dto.response.photo.PhotoUploadResponse;
import com.example.backend.dto.response.photo.ProcessedImageResult;
import com.example.backend.dto.response.photo.UploadedImageInfo;
import com.example.backend.entity.PhotoMetadata;
import com.example.backend.entity.Photos;
import com.example.backend.entity.Users;
import com.example.backend.mapper.PhotoMapper;
import com.example.backend.repository.photo.PhotosRepository;
import com.example.backend.repository.user.UserRepository;
import com.example.backend.service.location.VietMapLocationService;
import com.example.backend.service.photo.ExifExtractorService;
import com.example.backend.service.photo.ImageModerationService;
import com.example.backend.service.photo.ImageProcessingService;
import com.example.backend.service.photo.PhotoUploadService;
import com.example.backend.utils.cloudinary.CloudinaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhotoUploadServiceImpl implements PhotoUploadService {

    private static final int MAX_FILES_PER_UPLOAD = 10;
    private static final int MAX_WIDTH = 1600;
    private static final float JPEG_QUALITY = 0.82f;

    private final PhotosRepository photosRepository;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final PhotoMapper photoMapper;

    private final VietMapLocationService vietMapLocationService;
    private final ImageModerationService imageModerationService;
    private final ExifExtractorService exifExtractorService;
    private final ImageProcessingService imageProcessingService;

    @Qualifier("photoUploadExecutor")
    private final Executor photoUploadExecutor;

    @Override
    public List<PhotoUploadResponse> uploadPhotos(List<MultipartFile> files, String userId) {
        validateFiles(files);

        Users user = getUserOrThrow(userId);

        List<CompletableFuture<PhotoUploadResponse>> futures = files.stream()
                .map(file -> CompletableFuture.supplyAsync(
                        () -> uploadSingleInternal(file, user),
                        photoUploadExecutor
                ))
                .toList();

        try {
            return futures.stream()
                    .map(CompletableFuture::join)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tải ảnh lên", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PhotoUploadResponse getPhotoById(String photoId) {
        Photos photo = photosRepository.findById(photoId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ảnh"));

        return photoMapper.toResponse(photo);
    }

    @Override
    @Transactional
    public void deletePhoto(String photoId, String userId) {
        Photos photo = photosRepository.findById(photoId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ảnh"));

//        validatePhotoOwnership(photo, userId);

        deleteFromCloudinaryQuietly(photo.getImageUrl());
        photosRepository.delete(photo);
    }

    @Transactional
    protected PhotoUploadResponse uploadSingleInternal(MultipartFile file, Users user) {
        try {
            validateSingleFile(file);

            ModerationResult moderation = moderateImage(file);   // UNSAFE → throws here
            ExifDataDto exifData = exifExtractorService.extract(file);
            VietMapLocationResponse resolvedAddress = resolveAddress(exifData);

            PhotoMetadata metadata = buildMetadata(exifData, resolvedAddress);
            UploadedImageInfo uploadedImage = uploadImage(file, user);
            Photos savedPhoto = savePhoto(metadata, uploadedImage, moderation); // ← truyền moderation

            log.info("Uploaded photo file={} thread={} moderation={}",
                    file.getOriginalFilename(),
                    Thread.currentThread().getName(),
                    moderation.isWarning() ? "WARNING" : "SAFE");

            String moderationMsg = moderation.isWarning() ? moderation.getReason() : null;
            return photoMapper.toResponse(savedPhoto, moderationMsg);
        } catch (Exception e) {
            log.error("Lỗi khi upload ảnh {}: {}", file.getOriginalFilename(), e.getMessage(), e);
            throw new RuntimeException("Lỗi upload ảnh: " + file.getOriginalFilename(), e);
        }
    }


    private void validateFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new RuntimeException("Danh sách ảnh không được để trống");
        }

        if (files.size() > MAX_FILES_PER_UPLOAD) {
            throw new RuntimeException("Tối đa " + MAX_FILES_PER_UPLOAD + " ảnh mỗi lần tải");
        }
    }

    private void validateSingleFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File ảnh không hợp lệ");
        }
    }

    private Users getUserOrThrow(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
    }


    private ModerationResult moderateImage(MultipartFile file) {
        ModerationResult moderation = imageModerationService.moderate(file);

        if (moderation.isBlocked()) {
            throw new RuntimeException(
                    moderation.getReason() != null
                            ? moderation.getReason()
                            : "Nội dung ảnh vi phạm"
            );
        }

        return moderation;
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

        // Nếu sau này cần lưu địa chỉ từ reverse geocode thì mở lại đoạn này
        if (resolvedAddress != null) {
            metadata.setAddress(resolvedAddress.getDisplay());
            metadata.setProvince(resolvedAddress.getProvince());
            metadata.setDistrict(resolvedAddress.getDistrict());
            metadata.setWard(resolvedAddress.getWard());
        }

        return metadata;
    }

    private UploadedImageInfo uploadImage(MultipartFile file, Users user) {
        String originalFilename = file.getOriginalFilename();
        boolean isHeic = originalFilename != null
                && originalFilename.toLowerCase().matches(".*\\.(heic|heif)$");

        String publicId = "user_" + user.getId() + "/photo_" + UUID.randomUUID();

        if (isHeic) {
            return uploadHeicImage(file, publicId);
        }

        return uploadStandardImage(file, publicId);
    }

    private UploadedImageInfo uploadHeicImage(MultipartFile file, String publicId) {
        try {
            Map<?, ?> uploadResult = cloudinaryService.uploadHeicAndConvert(file.getBytes(), publicId);

            return new UploadedImageInfo(
                    uploadResult.get("secure_url").toString(),
                    Integer.parseInt(uploadResult.get("width").toString()),
                    Integer.parseInt(uploadResult.get("height").toString()),
                    Long.parseLong(uploadResult.get("bytes").toString())
            );
        } catch (Exception e) {
            throw new RuntimeException("Lỗi xử lý file HEIC", e);
        }
    }

    private UploadedImageInfo uploadStandardImage(MultipartFile file, String publicId) {
        ProcessedImageResult processed = imageProcessingService.process(file, MAX_WIDTH, JPEG_QUALITY);
        String imageUrl = cloudinaryService.uploadImage(processed.getBytes(), publicId);

        return new UploadedImageInfo(
                imageUrl,
                processed.getWidth(),
                processed.getHeight(),
                processed.getFileSize()
        );
    }

    private Photos savePhoto(PhotoMetadata metadata, UploadedImageInfo uploadedImage) {
        return savePhoto(metadata, uploadedImage, null);
    }

    private Photos savePhoto(PhotoMetadata metadata, UploadedImageInfo uploadedImage,
                             ModerationResult moderation) {
        Photos photo = new Photos();
        photo.setImageUrl(uploadedImage.getImageUrl());
        photo.setWidth(uploadedImage.getWidth());
        photo.setHeight(uploadedImage.getHeight());
        photo.setFileSize(uploadedImage.getFileSize());
        photo.setIsLocationVerified(false);

        // Lưu kết quả kiểm duyệt Gemini vào DB
        if (moderation != null) {
            if (moderation.isWarning()) {
                photo.setModerationStatus("WARNING");
            } else {
                photo.setModerationStatus("SAFE");
            }
            photo.setModerationReason(moderation.getReason());
            photo.setModerationScore(moderation.getScore());
        }

        metadata.setPhoto(photo);
        photo.setMetadata(metadata);

        return photosRepository.save(photo);
    }



    private void deleteFromCloudinaryQuietly(String imageUrl) {
        try {
            String publicId = CloudinaryService.extractPublicId(imageUrl);
            cloudinaryService.deleteImageByPublicId(publicId);
        } catch (Exception e) {
            log.error("Lỗi khi xóa ảnh trên Cloudinary: {}", imageUrl, e);
        }
    }
}