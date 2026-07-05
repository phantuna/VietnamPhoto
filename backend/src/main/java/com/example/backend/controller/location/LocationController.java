package com.example.backend.controller.location;

import com.example.backend.dto.request.location.LocationsRequest;
import com.example.backend.dto.response.location.LocationsResponse;
import com.example.backend.service.location.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @PostMapping
    public LocationsResponse createLocation(
            @RequestBody LocationsRequest request,
            @AuthenticationPrincipal String userId
    ) {
        return locationService.createLocation(request, userId);
    }

    @GetMapping("/{id}")
    public LocationsResponse getLocation(@PathVariable String id) {
        return locationService.getLocationById(id);
    }

    @GetMapping
    public Page<LocationsResponse> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer level
    ) {
        return locationService.getAllLocations(page, size, level);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        locationService.deleteLocation(id);
    }
}
