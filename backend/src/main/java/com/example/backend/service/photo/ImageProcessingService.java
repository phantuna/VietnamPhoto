package com.example.backend.service.photo;

import com.example.backend.dto.response.photo.ProcessedImageResult;
import org.springframework.web.multipart.MultipartFile;

public interface ImageProcessingService {
    ProcessedImageResult process(MultipartFile file, int maxWidth, float quality);
}
