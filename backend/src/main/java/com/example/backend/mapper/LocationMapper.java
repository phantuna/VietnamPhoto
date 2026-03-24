package com.example.backend.mapper;

import com.example.backend.dto.response.location.LocationsResponse;
import com.example.backend.entity.Locations;
import org.springframework.stereotype.Component;

@Component
public class LocationMapper {

    public LocationsResponse toResponse(Locations location) {
        return LocationsResponse.builder()
                .id(location.getId())
                .name(location.getName())
                .province(location.getProvince())
                .district(location.getDistrict())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .description(location.getDescription())
                .build();
    }
}
