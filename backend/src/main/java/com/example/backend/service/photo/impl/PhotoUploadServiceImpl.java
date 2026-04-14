package com.example.backend.service.photo.impl;

import com.example.backend.dto.response.photo.PhotoUploadResponse;
import com.example.backend.entity.Photos;
import com.example.backend.repository.photo.PhotosRepository;
import com.example.backend.service.photo.PhotoUploadService;
import com.example.backend.service.photo.SinglePhotoUploadService;
import com.example.backend.utils.cloudinary.CloudinaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhotoUploadServiceImpl implements PhotoUploadService {

    private final PhotosRepository photosRepository;
    private final CloudinaryService cloudinaryService;
    private final SinglePhotoUploadService singlePhotoUploadService;

    @Qualifier("photoUploadExecutor")
    private final Executor photoUploadExecutor;

    @Override
    public PhotoUploadResponse uploadPhoto(MultipartFile file, String userId) {
        return singlePhotoUploadService.uploadSingle(file, userId);
    }

    @Override
    public List<PhotoUploadResponse> uploadMultiplePhotos(List<MultipartFile> files, String userId) {
        if (files == null || files.isEmpty()) throw new RuntimeException("No files");
        if (files.size() > 10) throw new RuntimeException("Tối đa 10 ảnh");

        List<CompletableFuture<PhotoUploadResponse>> futures = files.stream()
                .map(file -> CompletableFuture.supplyAsync(() -> {
                    try {
                        // CHỈ truyền file và userId
                        return singlePhotoUploadService.uploadSingle(file, userId);
                    } catch (Exception e) {
                        log.error("Lỗi ảnh {}: {}", file.getOriginalFilename(), e.getMessage(), e);
                        throw new RuntimeException("Lỗi: " + file.getOriginalFilename(), e);
                    }
                }, photoUploadExecutor))
                .toList();

        try {
            return futures.stream().map(CompletableFuture::join).toList();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tải lên hàng loạt", e);
        }

    }

    @Override
    @Transactional
    public void deletePhoto(String photoId, String userId) {
        Photos photo = photosRepository.findById(photoId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ảnh với ID này"));



        try {
            String publicId = CloudinaryService.extractPublicId(photo.getImageUrl());
            cloudinaryService.deleteImageByPublicId(publicId);
        } catch (Exception e) {
            log.error("Lỗi khi xóa ảnh trên Cloudinary: {}", photo.getImageUrl(), e);
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