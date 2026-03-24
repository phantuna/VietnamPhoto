package com.example.backend.service.photo;


import com.example.backend.dto.response.photo.PhotoUploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface PhotoUploadService {
    PhotoUploadResponse uploadPhoto(MultipartFile file, UUID userId, UUID locationId, String caption);

    List<PhotoUploadResponse> uploadMultiplePhotos(List<MultipartFile> files, UUID userId, UUID locationId, String caption);
    void deletePhoto(UUID photoId, UUID userId);

}
