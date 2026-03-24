package com.example.backend.service.photo.impl;

import com.example.backend.dto.response.photo.ProcessedImageResult;
import com.example.backend.service.photo.ImageProcessingService;
import com.example.backend.utils.ImageUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;

@Service
public class ImageProcessingServiceImpl implements ImageProcessingService {

    @Override
    public ProcessedImageResult process(MultipartFile file, int maxWidth, float quality) {
        try {

            BufferedImage original = ImageUtil.readImage(file);
            if (original == null) {
                throw new RuntimeException("Không thể đọc dữ liệu hình ảnh.");
            }
            BufferedImage resized = ImageUtil.resize(original, maxWidth);
            byte[] cleanBytes = ImageUtil.compressJpeg(resized, quality);

            return new ProcessedImageResult(
                    cleanBytes,
                    resized.getWidth(),
                    resized.getHeight(),
                    (long) cleanBytes.length
            );
        } catch (Exception e) {
        throw new RuntimeException("Lỗi khi xử lý/convert ảnh: " + e.getMessage());
        }
    }
}