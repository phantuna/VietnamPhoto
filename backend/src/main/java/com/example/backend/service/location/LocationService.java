package com.example.backend.service.location;

import com.example.backend.dto.response.location.LocationsResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


public interface LocationService {

    LocationsResponse createLocation(
            String name,
            String province,
            String district,
            BigDecimal latitude,
            BigDecimal longitude,
            String description
    );

    LocationsResponse getLocationById(UUID id);

    List<LocationsResponse> getAllLocations();

    LocationsResponse updateLocation(
            UUID id,
            String name,
            String province,
            String district,
            BigDecimal latitude,
            BigDecimal longitude,
            String description
    );

    void deleteLocation(UUID id);
}