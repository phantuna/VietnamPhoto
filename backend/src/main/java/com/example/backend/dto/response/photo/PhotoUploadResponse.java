package com.example.backend.dto.response.photo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PhotoUploadResponse {
    private String photoId;
    private String imageUrl;
    private Boolean locationVerified;
    private Double locationDistanceMeters;

    /** "SAFE" | "WARNING" | "UNSAFE" — kết quả từ Gemini */
    private String moderationStatus;

    /** Lý do kiểm duyệt (tiếng Việt, hiển thị cho user khi WARNING) */
    private String moderationMessage;

    /** 0.0 = safe, 0.5 = warning, 1.0 = unsafe */
    private Double moderationScore;

    private ExifDataDto exifData;
}

