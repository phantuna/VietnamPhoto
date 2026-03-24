package com.example.backend.dto.response.vietmap;

import com.example.backend.dto.request.VietMapRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class VietMapSearchResponse {
    private BigDecimal lat;
    private BigDecimal lng;
    private String ref_id;
    private String address;
    private String name;
    private String display;
    private List<VietMapRequest> boundaries;
    private VietMapSearchResponse data_old;
    private VietMapSearchResponse data_new;
}
