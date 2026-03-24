package com.example.backend.controller;

import com.example.backend.dto.response.location.LocationsResponse;
import com.example.backend.service.location.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @PostMapping
    public LocationsResponse createLocation(
            @RequestParam String name,
            @RequestParam String province,
            @RequestParam String district,
            @RequestParam BigDecimal latitude,
            @RequestParam BigDecimal longitude,
            @RequestParam(required = false) String description
    ) {
        return locationService.createLocation(
                name,
                province,
                district,
                latitude,
                longitude,
                description
        );
    }

    @GetMapping("/{id}")
    public LocationsResponse getLocation(@PathVariable UUID id) {
        return locationService.getLocationById(id);
    }

    @GetMapping
    public List<LocationsResponse> getAll() {
        return locationService.getAllLocations();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        locationService.deleteLocation(id);
    }
}
