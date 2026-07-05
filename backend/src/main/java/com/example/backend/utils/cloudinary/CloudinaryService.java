package com.example.backend.utils.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.example.backend.exception.AppException;
import com.example.backend.exception.ErrorCode;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public String uploadImage(byte[] imageBytes, String publicId) {
        try {
            Map uploadResult = cloudinary.uploader().upload(
                    imageBytes,
                    ObjectUtils.asMap(
                            "public_id", publicId,
                            "folder", "photos",
                            "resource_type", "image"
                    )
            );
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new AppException(ErrorCode.EXTERNAL_SERVICE_ERROR);
        }
    }

    public Map<?, ?> uploadHeicAndConvert(byte[] imageBytes, String publicId) {
        try {
            return cloudinary.uploader().upload(
                    imageBytes,
                    ObjectUtils.asMap(
                            "public_id", publicId,
                            "folder", "photos",
                            "resource_type", "image",
                            "format", "jpg", 
                            "transformation", "c_limit,w_1600/q_82" 
                    )
            );
        } catch (IOException e) {
            throw new AppException(ErrorCode.EXTERNAL_SERVICE_ERROR);
        }
    }

    public static String extractPublicId(String imageUrl) {
        try {
            String path = URI.create(imageUrl).getPath();

            String marker = "/upload/";
            int idx = path.indexOf(marker);
            if (idx < 0) throw new AppException(ErrorCode.VALIDATION_FAILED);

            String afterUpload = path.substring(idx + marker.length());
            if (afterUpload.startsWith("v")) {
                int slash = afterUpload.indexOf('/');
                if (slash > 0) afterUpload = afterUpload.substring(slash + 1);
            }

            int dot = afterUpload.lastIndexOf('.');
            if (dot > 0) afterUpload = afterUpload.substring(0, dot);

            return afterUpload;
        } catch (Exception e) {
            throw new AppException(ErrorCode.VALIDATION_FAILED);
        }
    }

    public void deleteImageByPublicId(String publicId) {
        try {
            cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.asMap("resource_type", "image")
            );
        } catch (IOException e) {
            throw new AppException(ErrorCode.EXTERNAL_SERVICE_ERROR);
        }
    }
    public void deleteImageByUrl(String imageUrl) {
        String publicId = extractPublicId(imageUrl);
        deleteImageByPublicId(publicId);
    }
}