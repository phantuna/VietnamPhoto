package com.example.backend.controller;

import com.example.backend.dto.response.photo.PhotoUploadResponse;
import com.example.backend.service.photo.PhotoUploadService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/photos") // Đồng bộ version API với Posts
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoUploadService photoUploadService;

    // 1. TẢI LÊN NHIỀU ẢNH
    @PostMapping(value = "/upload-multiple", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public List<PhotoUploadResponse> uploadMultiple(
            @RequestParam("files") List<MultipartFile> files, // Đổi tên tham số thành files cho chuẩn
            @AuthenticationPrincipal String userId
    ) {
        return photoUploadService.uploadMultiplePhotos(files, userId);
    }

    // 2. TẢI LÊN 1 ẢNH (Dành cho việc up Avatar hoặc Cover)
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public PhotoUploadResponse uploadSingle(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal String userId
    ) {
        return photoUploadService.uploadPhoto(file, userId);
    }

    // 3. XEM THÔNG TIN 1 ẢNH
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public PhotoUploadResponse getPhotoById(@NotBlank @PathVariable String id) {
        return photoUploadService.getPhotoById(id);
    }

    // 4. XÓA ẢNH
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public void deletePhoto(
            @NotBlank @PathVariable String id,
            @AuthenticationPrincipal String userId
    ) {
        photoUploadService.deletePhoto(id, userId);
    }
}