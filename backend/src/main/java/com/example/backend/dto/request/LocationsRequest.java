package com.example.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationsRequest {
    private String name;
    private String province;
    private String district;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String description;

}
