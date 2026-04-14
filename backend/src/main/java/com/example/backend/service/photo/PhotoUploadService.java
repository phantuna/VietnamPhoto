package com.example.backend.service.photo;


import com.example.backend.dto.response.photo.PhotoUploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface PhotoUploadService {
    PhotoUploadResponse uploadPhoto(MultipartFile file, String userId);

    // 2. Upload nhiều ảnh cùng lúc
    List<PhotoUploadResponse> uploadMultiplePhotos(List<MultipartFile> files, String userId);

    // 3. Lấy thông tin 1 ảnh đã upload
    PhotoUploadResponse getPhotoById(String photoId);

    // 4. Xóa ảnh
    void deletePhoto(String photoId, String userId);
}
