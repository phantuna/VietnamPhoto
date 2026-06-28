package com.example.backend.service.location.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.backend.dto.request.location.LocationsRequest;
import com.example.backend.dto.response.location.LocationsResponse;
import com.example.backend.entity.Locations;
import com.example.backend.enums.LocationType;
import com.example.backend.exception.AppException;
import com.example.backend.exception.ErrorCode;
import com.example.backend.mapper.LocationMapper;
import com.example.backend.repository.location.LocationsRepository;
import com.example.backend.service.location.VietMapLocationService;

@ExtendWith(MockitoExtension.class)
public class LocationServiceImplTest {

    @Mock
    private VietMapLocationService vietMapLocationService;

    @Mock
    private LocationsRepository locationsRepository;

    @Mock
    private LocationMapper locationMapper;

    @InjectMocks
    private LocationServiceImpl locationService;

    private Locations existingLocation;
    private LocationsRequest request;

    @BeforeEach
    void setUp() {
        existingLocation = new Locations();
        existingLocation.setId("loc-111");
        existingLocation.setName("Hồ Hoàn Kiếm");
        existingLocation.setLatitude(BigDecimal.valueOf(21.0285));
        existingLocation.setLongitude(BigDecimal.valueOf(105.8542));
        existingLocation.setLevel(2);

        request = new LocationsRequest();
        request.setName("Hồ Gươm");
        request.setLatitude(BigDecimal.valueOf(21.0286));
        request.setLongitude(BigDecimal.valueOf(105.8543));
        request.setLocationType(LocationType.SPOT);
    }

    @Test
    void createLocation_TooCloseAbsolute_ThrowsAppException() {
        // Khoảng cách cực gần (ví dụ vĩ độ/kinh độ lệch rất nhỏ, khoảng < 20m)
        request.setLatitude(BigDecimal.valueOf(21.028501));
        request.setLongitude(BigDecimal.valueOf(105.854201));

        when(locationsRepository.findAll()).thenReturn(List.of(existingLocation));

        assertThatThrownBy(() -> locationService.createLocation(request, "creator-123"))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.LOCATION_TOO_CLOSE.name());
    }

    @Test
    void createLocation_SimilarNameWithinRadius_ThrowsAppException() {
        // Nằm trong bán kính 300m và có tên tương tự nhau ("Hồ Gươm" vs "Hồ Hoàn Kiếm")
        // "hồ gươm" và "hồ hoàn kiếm" không tương tự nhau trong contains thuần,
        // hãy đổi tên request thành "Hồ Hoàn Kiếm" hoặc "Hoàn Kiếm" để test similarity.
        request.setName("Hoàn Kiếm");

        when(locationsRepository.findAll()).thenReturn(List.of(existingLocation));

        assertThatThrownBy(() -> locationService.createLocation(request, "creator-123"))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.LOCATION_TOO_CLOSE.name());
    }

    @Test
    void createLocation_ValidInput_Success() {
        // Khoảng cách xa (> 500m)
        request.setLatitude(BigDecimal.valueOf(21.0500));
        request.setLongitude(BigDecimal.valueOf(105.9000));
        request.setName("Địa điểm mới");

        when(locationsRepository.findAll()).thenReturn(List.of(existingLocation));
        when(locationsRepository.save(any(Locations.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(locationMapper.toResponse(any(Locations.class))).thenReturn(new LocationsResponse());

        LocationsResponse response = locationService.createLocation(request, "creator-123");

        assertThat(response).isNotNull();
        verify(locationsRepository, times(1)).save(any(Locations.class));
    }
}
