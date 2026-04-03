package com.example.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocationsRequest {
    private String name;
    private UUID parentId; // ID của Phường/Xã (Level 1)
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String description;

    // Thêm các trường cho Mini Hub
    private String category;
    private String coverPhoto;
    private String goldenHour;

}
