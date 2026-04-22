package com.example.backend.dto.response.location;

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
public class LocationsResponse {
    private String id;
    private String name;
    private String nameWithType;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Integer level;
    private String code;
    private String slug;
    private String description;
    private String category;
    private String coverPhoto;
    private Long postCount;
    private Long checkInCount;
    private String goldenHour;
    // Province name extracted from parent for easy filtering
    private String province;
    private LocationsResponse parent;
}
