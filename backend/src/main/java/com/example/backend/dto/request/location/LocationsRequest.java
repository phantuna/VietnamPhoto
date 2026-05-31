package com.example.backend.dto.request.location;

import com.example.backend.enums.LocationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocationsRequest {
    private String id;
    private String name;
    private String parentId; // ID của Phường/Xã (Level 1)
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String description;

    // Thêm các trường cho Mini Hub
    private String category;
    private LocationType locationType; // SPOT hoặc SERVICE (FE gửi lên)
    // creatorId không nhận từ FE — sẽ được gán từ JWT ở tầng Service
}
