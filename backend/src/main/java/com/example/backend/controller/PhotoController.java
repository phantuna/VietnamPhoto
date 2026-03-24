package com.example.backend.controller;

import com.example.backend.dto.response.photo.PhotoUploadResponse;
import com.example.backend.service.photo.PhotoUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/photos")
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoUploadService photoUploadService;

    @PostMapping("/upload")
    public ResponseEntity<List<PhotoUploadResponse>> uploadMultiple(
            // CHÚ Ý CHỖ NÀY: Phải là List<MultipartFile> thay vì MultipartFile
            @RequestParam("file") List<MultipartFile> files,
            @RequestParam("userId") UUID userId,
            @RequestParam("locationId") UUID locationId,
            @RequestParam(value = "caption", required = false) String caption
    ) {

        // GỌI ĐÚNG HÀM uploadMultiplePhotos TRONG SERVICE
        List<PhotoUploadResponse> result = photoUploadService.uploadMultiplePhotos(files, userId, locationId, caption);

        return ResponseEntity.ok(result);
    }
}