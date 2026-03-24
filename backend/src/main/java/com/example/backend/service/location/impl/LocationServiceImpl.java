package com.example.backend.service.location.impl;

import com.example.backend.dto.response.location.LocationsResponse;
import com.example.backend.dto.response.location.VietMapLocationResponse;
import com.example.backend.entity.Locations;
import com.example.backend.repository.LocationsRepository;
import com.example.backend.service.location.LocationService;
import com.example.backend.mapper.LocationMapper;
import com.example.backend.service.location.VietMapLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final VietMapLocationService vietMapLocationService;
    private final LocationsRepository locationsRepository;
    private final LocationMapper locationMapper;

    @Override
    @Transactional
    public LocationsResponse createLocation(
            String name,
            String province,
            String district,
            BigDecimal latitude,
            BigDecimal longitude,
            String description
    ) {

        VietMapLocationResponse resolved = null;
        if (latitude != null && longitude != null) {
            resolved = vietMapLocationService.reverse(latitude, longitude);
        }

        Locations location = new Locations();
        location.setName(name);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setDescription(description);

        if (resolved != null) {
            location.setAddress(resolved.getDisplay() != null ? resolved.getDisplay() : resolved.getAddress());
            location.setProvince(resolved.getProvince() != null ? resolved.getProvince() : province);
            location.setDistrict(resolved.getDistrict() != null ? resolved.getDistrict() : district);
            location.setWard(resolved.getWard());
        } else {
            location.setProvince(province);
            location.setDistrict(district);
        }

        Locations saved = locationsRepository.save(location);

        return locationMapper.toResponse(saved);
    }

    @Override
    public LocationsResponse getLocationById(UUID id) {

        Locations location = locationsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found"));

        return locationMapper.toResponse(location);
    }

    @Override
    public List<LocationsResponse> getAllLocations() {

        return locationsRepository.findAll()
                .stream()
                .map(locationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public LocationsResponse updateLocation(
            UUID id,
            String name,
            String province,
            String district,
            BigDecimal latitude,
            BigDecimal longitude,
            String description
    ) {

        Locations location = locationsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found"));

        VietMapLocationResponse resolved = null;
        if (latitude != null && longitude != null) {
            resolved = vietMapLocationService.reverse(latitude, longitude);
        }

        location.setName(name);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setDescription(description);

        if (resolved != null) {
            location.setAddress(resolved.getDisplay() != null ? resolved.getDisplay() : resolved.getAddress());
            location.setProvince(resolved.getProvince() != null ? resolved.getProvince() : province);
            location.setDistrict(resolved.getDistrict() != null ? resolved.getDistrict() : district);
            location.setWard(resolved.getWard());
        } else {
            location.setProvince(province);
            location.setDistrict(district);
        }

        return locationMapper.toResponse(location);
    }

    @Override
    @Transactional
    public void deleteLocation(UUID id) {

        Locations location = locationsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found"));

        locationsRepository.delete(location);
    }
}
