package com.example.backend.controller;

import com.example.backend.dto.request.LocationsRequest;
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
    public LocationsResponse createLocation(@RequestBody LocationsRequest request) {
        return locationService.createLocation(request);
    }

    @GetMapping("/{id}")
    public LocationsResponse getLocation(@PathVariable String id) {
        return locationService.getLocationById(id);
    }

    @GetMapping
    public List<LocationsResponse> getAll() {
        return locationService.getAllLocations();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        locationService.deleteLocation(id);
    }
}
