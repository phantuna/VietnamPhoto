package com.example.backend.dto.response.location;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VietMapLocationResponse {
    private String name;
    private String address;
    private String display;
    private String province;
    private String district;
    private String ward;
    private String refId;
}
