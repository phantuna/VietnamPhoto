package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Photos extends Base{
    @Column(name = "image_url", nullable = false, columnDefinition = "TEXT")
    private String imageUrl;
    private Integer width;
    private Integer height;
    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "location_verified")
    private Boolean isLocationVerified = false;

    /**
     * Kết quả kiểm duyệt từ Gemini Vision API.
     * Giá trị: "SAFE" | "WARNING" | "UNSAFE"
     * null = chưa được kiểm duyệt (ảnh cũ trước khi tích hợp Gemini)
     */
    @Column(name = "moderation_status", length = 10)
    private String moderationStatus;

    /** Lý do Gemini trả về (bằng tiếng Việt) */
    @Column(name = "moderation_reason", columnDefinition = "TEXT")
    private String moderationReason;

    /** Score: 0.0 = safe, 0.5 = warning, 1.0 = unsafe */
    @Column(name = "moderation_score")
    private Double moderationScore;

    @OneToOne(mappedBy = "photo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private PhotoMetadata metadata;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Posts post;
}
