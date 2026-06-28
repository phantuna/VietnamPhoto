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
    private String parentId;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String description;

    private String category;
    private LocationType locationType;
}
