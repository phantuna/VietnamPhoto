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
    private UUID id;
    private String name;
    private String province;
    private String district;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String description;
}
