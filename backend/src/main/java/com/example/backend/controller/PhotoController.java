package com.example.backend.controller;

import com.example.backend.dto.response.photo.PhotoUploadResponse;
import com.example.backend.service.photo.PhotoUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<PhotoUploadResponse>> uploadMultiple(
            @RequestParam("file") List<MultipartFile> files,
            @RequestParam("userId") String userId
    ) {
        // Chỉ truyền files và userId
        List<PhotoUploadResponse> result = photoUploadService.uploadMultiplePhotos(files, userId);
        return ResponseEntity.ok(result);
    }
}