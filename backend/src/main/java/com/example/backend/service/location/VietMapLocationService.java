package com.example.backend.service.location;

import com.example.backend.dto.response.location.VietMapLocationResponse;
import com.example.backend.dto.response.vietmap.VietMapSearchResponse;

import java.math.BigDecimal;
import java.util.List;

public interface VietMapLocationService {
    VietMapLocationResponse reverse(BigDecimal lat, BigDecimal lng);
    List<VietMapSearchResponse> searchNearby(BigDecimal lat, BigDecimal lng, int radiusMeters, String text);
}
