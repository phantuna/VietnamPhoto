package com.example.backend.dto.response.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminLocationResponse {
    private String id;
    private String code;
    private String name;
    private String nameWithType;
    private String type;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Integer deleted;
}
