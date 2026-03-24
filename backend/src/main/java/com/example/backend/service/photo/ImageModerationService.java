package com.example.backend.service.photo;

import com.example.backend.dto.response.photo.ModerationResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageModerationService {
    ModerationResult moderate(MultipartFile file);
}
