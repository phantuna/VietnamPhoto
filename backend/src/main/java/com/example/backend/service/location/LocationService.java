package com.example.backend.service.location;

import com.example.backend.dto.request.location.LocationsRequest;
import com.example.backend.dto.response.location.LocationsResponse;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;


public interface LocationService {

    LocationsResponse createLocation(LocationsRequest request, String creatorId);

    LocationsResponse getLocationById(String id);

    Page<LocationsResponse> getAllLocations(int page, int size);

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