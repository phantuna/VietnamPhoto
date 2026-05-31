package com.example.backend.service.photo;


import com.example.backend.dto.response.photo.PhotoUploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface PhotoUploadService {
    List<PhotoUploadResponse> uploadPhotos(List<MultipartFile> files, String userId);

    PhotoUploadResponse getPhotoById(String photoId);

    void deletePhoto(String photoId, String userId);

    String uploadAvatar(MultipartFile file, String userId);
}
