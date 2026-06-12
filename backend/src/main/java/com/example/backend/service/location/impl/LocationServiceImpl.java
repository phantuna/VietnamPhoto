package com.example.backend.service.location.impl;

import com.example.backend.dto.request.location.LocationsRequest;
import com.example.backend.dto.response.location.LocationsResponse;
import com.example.backend.dto.response.location.VietMapLocationResponse;
import com.example.backend.entity.Locations;
import com.example.backend.enums.LocationType;
import com.example.backend.exception.AppException;
import com.example.backend.exception.ErrorCode;
import com.example.backend.repository.location.LocationsRepository;
import com.example.backend.service.location.LocationService;
import com.example.backend.mapper.LocationMapper;
import com.example.backend.service.location.VietMapLocationService;
import com.example.backend.utils.GeoUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final VietMapLocationService vietMapLocationService;
    private final LocationsRepository locationsRepository;
    private final LocationMapper locationMapper;

    // Tăng bán kính kiểm tra để tránh rác (Spot: 3km, Service: 500m)
    private static final double MIN_DISTANCE_SPOT    = 300.0; // SPOT: 3km
    private static final double MIN_DISTANCE_SERVICE = 500.0;  // SERVICE: 500m

    @Override
    @Transactional
    @CacheEvict(value = "locations", allEntries = true)
    public LocationsResponse createLocation(LocationsRequest request, String creatorId) {
        // 0. Xác định loại địa điểm (mặc định SPOT nếu FE không gửi)
        LocationType locationType = request.getLocationType() != null
                ? request.getLocationType()
                : LocationType.SPOT;

        double minDist = locationType == LocationType.SERVICE
                ? MIN_DISTANCE_SERVICE
                : MIN_DISTANCE_SPOT;

        // 1. Kiểm tra khoảng cách tránh trùng lặp (chỉ so sánh với các điểm check-in/dịch vụ thực tế, level >= 2)
        List<Locations> existingLocations = locationsRepository.findAll();
        for (Locations existing : existingLocations) {
            boolean isCheckinPoint = existing.getLevel() != null && existing.getLevel() >= 2;
            
            if (isCheckinPoint && existing.getLatitude() != null && existing.getLongitude() != null) {
                double dist = GeoUtils.distanceInMeters(
                        request.getLatitude().doubleValue(), request.getLongitude().doubleValue(),
                        existing.getLatitude().doubleValue(), existing.getLongitude().doubleValue()
                );
                if (dist < minDist) {
                    throw new AppException(ErrorCode.LOCATION_TOO_CLOSE);
                }
            }
        }

        Locations newLocation = new Locations();
        newLocation.setName(request.getName());
        newLocation.setLatitude(request.getLatitude());
        newLocation.setLongitude(request.getLongitude());
        newLocation.setDescription(request.getDescription());
        newLocation.setCategory(request.getCategory());
        newLocation.setLocationType(locationType);
        newLocation.setCreatorId(creatorId); 

        if (request.getLatitude() != null && request.getLongitude() != null) {
            try {
                VietMapLocationResponse resolved = vietMapLocationService.reverse(request.getLatitude(), request.getLongitude());
                
                if (resolved != null) {
                    if (resolved.getWard() != null) {
                        locationsRepository.findFirstByNameWithTypeContainingAndLevel(resolved.getWard(), Integer.valueOf(1))
                                .ifPresent(newLocation::setParent);
                    }
                    
                    if (newLocation.getParent() == null && resolved.getDistrict() != null) {
                        locationsRepository.findFirstByNameWithTypeContainingAndLevel(resolved.getDistrict(), Integer.valueOf(1))
                                .ifPresent(newLocation::setParent);
                    }

                    if (newLocation.getParent() == null && resolved.getProvince() != null) {
                        locationsRepository.findFirstByNameWithTypeContainingAndLevel(resolved.getProvince(), Integer.valueOf(0))
                                .ifPresent(newLocation::setParent);
                    }
                    
                    newLocation.setNameWithType(request.getName());
                }
            } catch (Exception e) {
                System.err.println("Lỗi tự động tra cứu địa giới: " + e.getMessage());
            }
        }
        if (request.getParentId() != null) {
            locationsRepository.findById(request.getParentId())
                    .ifPresent(newLocation::setParent);
        }

        newLocation.setLevel(2); 
        newLocation.setType("check-in"); 
        newLocation.setCode("LOC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        
        if (newLocation.getNameWithType() == null) {
            newLocation.setNameWithType(request.getName());
        }

        String baseSlug = request.getName().toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .trim()
                .replaceAll("\\s+", "-");
        newLocation.setSlug(baseSlug + "-" + System.currentTimeMillis() % 100000);

        Locations savedLocation = locationsRepository.save(newLocation);
        return locationMapper.toResponse(savedLocation);
    }

    @Override
    public LocationsResponse getLocationById(String id) {

        Locations location = locationsRepository.findById(id)
                .filter(l -> l.getDeleted() == null || l.getDeleted() == 0)
                .orElseThrow(() -> new RuntimeException("Location not found"));

        return locationMapper.toResponse(location);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "locations", key = "#page + '-' + #size + '-' + (#level != null ? #level : 'all')")
    public Page<LocationsResponse> getAllLocations(int page, int size, Integer level) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Locations> locationsPage;
        if (level != null) {
            locationsPage = locationsRepository.findByDeletedAndLevel(0, level, pageable);
        } else {
            locationsPage = locationsRepository.findByDeleted(0, pageable);
        }

        return locationsPage.map(loc -> {
            LocationsResponse res = locationMapper.toResponse(loc);
            // Tối ưu hóa tải trọng JSON cho danh sách lớn (ví dụ Bản đồ gọi size = 10000)
            if (size >= 1000) {
                res.setParent(null);
                res.setDescription(null);
            }
            return res;
        });
    }

    @Override
    @Transactional
    @CacheEvict(value = "locations", allEntries = true)
    public LocationsResponse updateLocation(
            String id,
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

        return locationMapper.toResponse(location);
    }

    @Override
    @Transactional
    @CacheEvict(value = "locations", allEntries = true)
    public void deleteLocation(String id) {

        Locations location = locationsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found"));

        locationsRepository.delete(location);
    }
}
