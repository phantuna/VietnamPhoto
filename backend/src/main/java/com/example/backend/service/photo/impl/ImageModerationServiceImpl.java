package com.example.backend.service.photo.impl;

import com.example.backend.dto.response.photo.ModerationResult;
import com.example.backend.service.photo.ImageModerationService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageModerationServiceImpl implements ImageModerationService {

    @Override
    public ModerationResult moderate(MultipartFile file) {
        return ModerationResult.builder()
                .blocked(false)
                .warning(false)
                .reason("Image is safe")
                .score(0.0)
                .build();
    }
}
