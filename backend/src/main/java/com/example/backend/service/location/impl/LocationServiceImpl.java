package com.example.backend.service.location.impl;

import com.example.backend.dto.request.location.LocationsRequest;
import com.example.backend.dto.response.location.LocationsResponse;
import com.example.backend.dto.response.location.VietMapLocationResponse;
import com.example.backend.entity.Locations;
import com.example.backend.enums.LocationType;
import com.example.backend.repository.location.LocationsRepository;
import com.example.backend.service.location.LocationService;
import com.example.backend.mapper.LocationMapper;
import com.example.backend.service.location.VietMapLocationService;
import com.example.backend.utils.GeoUtils;
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

    // SERVICE cho phép địa điểm gần nhau hơn (nhà hàng cạnh nhau là bình thường)
    private static final double MIN_DISTANCE_SPOT    = 50.0;  // SPOT: không cho tạo trong bán kính 50m
    private static final double MIN_DISTANCE_SERVICE = 10.0;  // SERVICE: chỉ giới hạn 10m

    @Override
    @Transactional
    public LocationsResponse createLocation(LocationsRequest request, String creatorId) {
        // 0. Xác định loại địa điểm (mặc định SPOT nếu FE không gửi)
        LocationType locationType = request.getLocationType() != null
                ? request.getLocationType()
                : LocationType.SPOT;

        double minDist = locationType == LocationType.SERVICE
                ? MIN_DISTANCE_SERVICE
                : MIN_DISTANCE_SPOT;

        // 1. Kiểm tra khoảng cách tránh trùng lặp
        List<Locations> existingLocations = locationsRepository.findAll();
        for (Locations existing : existingLocations) {
            if (existing.getLatitude() != null && existing.getLongitude() != null) {
                double dist = GeoUtils.distanceInMeters(
                        request.getLatitude().doubleValue(), request.getLongitude().doubleValue(),
                        existing.getLatitude().doubleValue(), existing.getLongitude().doubleValue()
                );
                if (dist < minDist) {
                    throw new RuntimeException("Địa điểm này quá gần với '" + existing.getName() + "' (cách " + (int)dist + "m). Vui lòng chọn vị trí khác!");
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
        newLocation.setCreatorId(creatorId);  // Gán creatorId từ JWT

        // 1. Tự động tra cứu địa giới hành chính qua Vietmap
        if (request.getLatitude() != null && request.getLongitude() != null) {
            try {
                VietMapLocationResponse resolved = vietMapLocationService.reverse(request.getLatitude(), request.getLongitude());
                
                if (resolved != null) {
                    // Ưu tiên 1: Tìm Phường/Xã (Level 1)
                    if (resolved.getWard() != null) {
                        locationsRepository.findFirstByNameWithTypeContainingAndLevel(resolved.getWard(), Integer.valueOf(1))
                                .ifPresent(newLocation::setParent);
                    }
                    
                    // Ưu tiên 2: Nếu chưa tìm thấy Parent, tìm theo Quận/Huyện (Cũng thường là Level 1 hoặc cấp trung gian)
                    if (newLocation.getParent() == null && resolved.getDistrict() != null) {
                        locationsRepository.findFirstByNameWithTypeContainingAndLevel(resolved.getDistrict(), Integer.valueOf(1))
                                .ifPresent(newLocation::setParent);
                    }

                    // Ưu tiên 3: Nếu vẫn chưa thấy, tìm theo Tỉnh/Thành (Level 0) để đảm bảo luôn có tỉnh
                    if (newLocation.getParent() == null && resolved.getProvince() != null) {
                        locationsRepository.findFirstByNameWithTypeContainingAndLevel(resolved.getProvince(), Integer.valueOf(0))
                                .ifPresent(newLocation::setParent);
                    }
                    
                    // nameWithType cho Level 2 thường chỉ là tên địa điểm hoặc "Điểm check-in + Tên"
                    // Để giống Hồ Hoàn Kiếm nhất, ta chỉ để tên địa điểm, UI sẽ tự lấy Parent để hiện địa chỉ
                    newLocation.setNameWithType(request.getName());
                }
            } catch (Exception e) {
                System.err.println("Lỗi tự động tra cứu địa giới: " + e.getMessage());
            }
        }

        // 2. Nếu người dùng chọn Parent thủ công thì ghi đè (ưu tiên thủ công)
        if (request.getParentId() != null) {
            locationsRepository.findById(request.getParentId())
                    .ifPresent(newLocation::setParent);
        }

        // 3. Thiết lập thuộc tính mặc định khớp với cấu trúc DB của bạn
        newLocation.setLevel(2); // Cấp độ điểm check-in (dưới Xã level 1)
        newLocation.setType("check-in"); 
        newLocation.setCode("LOC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        
        // Nếu nameWithType chưa được set ở trên thì set mặc định
        if (newLocation.getNameWithType() == null) {
            newLocation.setNameWithType(request.getName());
        }

        
        // Tạo slug thân thiện (name + timestamp ngắn)
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
    public List<LocationsResponse> getAllLocations() {

        return locationsRepository.findAll()
                .stream()
                .filter(l -> l.getDeleted() == null || l.getDeleted() == 0)
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
