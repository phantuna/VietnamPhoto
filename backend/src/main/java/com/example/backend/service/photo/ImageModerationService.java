package com.example.backend.service.photo;

import com.example.backend.dto.response.photo.ModerationResult;
import org.springframework.web.multipart.MultipartFile;


public interface ImageModerationService {
    ModerationResult moderate(MultipartFile file);
}
