package com.example.backend.service.location.impl;

import com.example.backend.dto.request.VietMapRequest;
import com.example.backend.dto.response.location.VietMapLocationResponse;
import com.example.backend.dto.response.vietmap.VietMapReverseResponse;
import com.example.backend.dto.response.vietmap.VietMapSearchResponse;
import com.example.backend.service.location.VietMapLocationService;
import com.example.backend.utils.vietmap.VietMapService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VietMapLocationServiceImpl implements VietMapLocationService {
    private final VietMapService vietMapService;

    @Override
    public VietMapLocationResponse reverse(BigDecimal lat, BigDecimal lng) {
        VietMapReverseResponse dto = vietMapService.reverse(lat, lng);
        if (dto == null) {
            return null;
        }

        String ward = null;
        String district = null;
        String province = null;

        if (dto.getBoundaries() != null) {
            for (VietMapRequest b : dto.getBoundaries()) {
                if (b.getType() == null) continue;

                if (b.getType() == 2 && ward == null) {
                    ward = b.getFull_name();
                } else if (b.getType() == 1 && district == null) {
                    district = b.getFull_name();
                } else if (b.getType() == 0 && province == null) {
                    province = b.getFull_name();
                }
            }
        }

        return VietMapLocationResponse.builder()
                .name(dto.getName())
                .address(dto.getAddress())
                .display(dto.getDisplay())
                .province(province)
                .district(district)
                .ward(ward)
                .refId(dto.getRef_id())
                .build();
    }

    @Override
    public List<VietMapSearchResponse> searchNearby(BigDecimal lat, BigDecimal lng, int radiusMeters, String text) {
        return vietMapService.searchNearby(lat, lng, radiusMeters, text);
    }
}
