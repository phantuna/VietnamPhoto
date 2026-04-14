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
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Integer level;
    private String code;
    private String description;
    private LocationsResponse parent;
}
