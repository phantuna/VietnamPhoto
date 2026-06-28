package com.example.backend.service.photo.impl;

import com.example.backend.dto.response.photo.ProcessedImageResult;
import com.example.backend.exception.AppException;
import com.example.backend.exception.ErrorCode;
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
                throw new AppException(ErrorCode.INVALID_IMAGE);
            }
            BufferedImage resized = ImageUtil.resize(original, maxWidth);
            byte[] cleanBytes = ImageUtil.compressJpeg(resized, quality);

            return new ProcessedImageResult(
                    cleanBytes,
                    resized.getWidth(),
                    resized.getHeight(),
                    (long) cleanBytes.length
            );
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}