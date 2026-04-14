package com.example.backend.service.location.impl;

import com.example.backend.dto.request.LocationsRequest;
import com.example.backend.dto.response.location.LocationsResponse;
import com.example.backend.dto.response.location.VietMapLocationResponse;
import com.example.backend.entity.Locations;
import com.example.backend.repository.location.LocationsRepository;
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
    public LocationsResponse createLocation(LocationsRequest request) {

        Locations newLocation = new Locations();
        newLocation.setName(request.getName());
        newLocation.setLatitude(request.getLatitude());
        newLocation.setLongitude(request.getLongitude());
        newLocation.setDescription(request.getDescription());

        // 1. Thiết lập Cây Phân Cấp (Gắn Hồ Hoàn Kiếm vào Phường/Xã)
        if (request.getParentId() != null) {
            Locations parentWard = locationsRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Phường/Xã chứa địa điểm này"));
            newLocation.setParent(parentWard);
        }

        // 2. Thiết lập Mặc định cho Địa Điểm Cụ Thể
        newLocation.setLevel(2); // Theo logic của bạn: 0=Tỉnh, 1=Phường/Xã, 2=Địa điểm
        newLocation.setType("dia-diem-checkin");

        // Vì trường `code` là unique và NOT NULL, ta tạo ngẫu nhiên cho các địa điểm (ví dụ: LOC-A1B2C3D4)
        newLocation.setCode("LOC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        newLocation.setNameWithType(request.getName()); // "Hồ Hoàn Kiếm"

        // Tạo slug tự động từ name (Bạn có thể viết 1 hàm utils để chuyển "Hồ Hoàn Kiếm" -> "ho-hoan-kiem")
        newLocation.setSlug("ho-hoan-kiem-" + System.currentTimeMillis());

        // 3. (Tuỳ chọn) Gọi Vietmap để lấy thêm data nếu muốn
        if (request.getLatitude() != null && request.getLongitude() != null) {
            VietMapLocationResponse resolved = vietMapLocationService.reverse(request.getLatitude(), request.getLongitude());
            // Bạn có thể dùng resolved để cập nhật thêm các field khác nếu cần
        }

        Locations savedLocation = locationsRepository.save(newLocation);
        return locationMapper.toResponse(savedLocation);
    }

    @Override
    public LocationsResponse getLocationById(String id) {

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
    public void deleteLocation(String id) {

        Locations location = locationsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found"));

        locationsRepository.delete(location);
    }
}
