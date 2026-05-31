package com.example.backend.service.location;

import com.example.backend.dto.request.location.LocationsRequest;
import com.example.backend.dto.response.location.LocationsResponse;

import java.math.BigDecimal;
import java.util.List;


public interface LocationService {

    LocationsResponse createLocation(LocationsRequest request, String creatorId);

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