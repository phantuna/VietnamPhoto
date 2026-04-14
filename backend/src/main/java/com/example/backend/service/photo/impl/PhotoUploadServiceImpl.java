package com.example.backend.service.photo.impl;

import com.example.backend.dto.response.photo.PhotoUploadResponse;
import com.example.backend.entity.Photos;
import com.example.backend.mapper.PhotoMapper;
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
    private final PhotoMapper photoMapper;

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
    public PhotoUploadResponse getPhotoById(String photoId) {
        Photos photo = photosRepository.findById(photoId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ảnh"));

        return photoMapper.toResponse(photo);
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

}