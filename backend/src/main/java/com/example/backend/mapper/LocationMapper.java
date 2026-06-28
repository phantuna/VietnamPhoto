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
                .nameWithType(location.getNameWithType())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .description(location.getDescription())
                .level(location.getLevel())
                .code(location.getCode())
                .slug(location.getSlug())
                .category(location.getCategory())
                .coverPhoto(location.getCoverPhoto())
                .postCount(location.getPostCount())
                .checkInCount(location.getCheckInCount())
                .goldenHour(location.getGoldenHour())
                .locationType(location.getLocationType() != null ? location.getLocationType().name() : "SPOT")
                .creatorId(location.getCreatorId())
                .build();

        if (location.getParent() != null) {
            response.setParent(this.toResponse(location.getParent()));
            Locations root = location.getParent();
            while (root.getParent() != null) {
                root = root.getParent();
            }
            response.setProvince(root.getName());
        }

        return response;
    }
}
