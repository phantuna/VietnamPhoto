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
@RequestMapping("/api/photos") // Đồng bộ version API với Posts
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoUploadService photoUploadService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<PhotoUploadResponse> upload(
            @RequestParam("files") List<MultipartFile> files,
            @AuthenticationPrincipal String userId
    ) {
        return photoUploadService.uploadPhotos(files, userId);
    }

    @GetMapping("/{id}")
    public PhotoUploadResponse getPhotoById(@NotBlank @PathVariable String id) {
        return photoUploadService.getPhotoById(id);
    }

    @DeleteMapping("/{id}")
    public void deletePhoto(
            @NotBlank @PathVariable String id,
            @AuthenticationPrincipal String userId
    ) {
        photoUploadService.deletePhoto(id, userId);
    }
}