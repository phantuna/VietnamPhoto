package com.example.backend.utils.vietmap;


import com.example.backend.dto.response.vietmap.VietMapReverseResponse;
import com.example.backend.dto.response.vietmap.VietMapSearchResponse;

import java.math.BigDecimal;
import java.util.List;
public interface VietMapService {
    VietMapReverseResponse reverse(BigDecimal lat, BigDecimal lng);
    List<VietMapSearchResponse> searchNearby(BigDecimal lat, BigDecimal lng, int radiusMeters, String text);
}
