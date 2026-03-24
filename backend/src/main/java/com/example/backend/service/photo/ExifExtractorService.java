package com.example.backend.service.photo;

import com.example.backend.dto.response.photo.ExifDataDto;
import org.springframework.web.multipart.MultipartFile;

public interface ExifExtractorService {
    ExifDataDto extract(MultipartFile file);
}
