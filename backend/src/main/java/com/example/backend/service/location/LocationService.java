package com.example.backend.service.location;

import com.example.backend.dto.request.LocationsRequest;
import com.example.backend.dto.response.location.LocationsResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


public interface LocationService {

    LocationsResponse createLocation(LocationsRequest request);

    LocationsResponse getLocationById(String id);

    List<LocationsResponse> getAllLocations();

    LocationsResponse updateLocation(
            String id,
            String name,
            String province,
            String district,
            BigDecimal latitude,
            BigDecimal longitude,
            String description
    );

    void deleteLocation(String id);
}