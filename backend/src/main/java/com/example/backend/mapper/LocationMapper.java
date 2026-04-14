package com.example.backend.mapper;

import com.example.backend.dto.response.location.LocationsResponse;
import com.example.backend.entity.Locations;
import org.springframework.stereotype.Component;

@Component
public class LocationMapper {

    public LocationsResponse toResponse(Locations location) {
        if (location == null) {
            return null;
        }

        LocationsResponse response = LocationsResponse.builder()
                .id(location.getId())
                .name(location.getName())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .description(location.getDescription())
                .level(location.getLevel())
                .code(location.getCode())
                .build();

        // 🌟 MAP THÔNG TIN THẰNG CHA BẰNG ĐỆ QUY
        if (location.getParent() != null) {
            // Tự động gọi lại chính hàm này để map thằng Phường/Xã (và Tỉnh/Thành)
            response.setParent(this.toResponse(location.getParent()));
        }

        return response;
    }
}
