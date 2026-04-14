package com.example.backend.service.photo;

import com.example.backend.dto.response.photo.PhotoUploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface SinglePhotoUploadService {
    PhotoUploadResponse uploadSingle(
            MultipartFile file,
            String userId
    );
}
