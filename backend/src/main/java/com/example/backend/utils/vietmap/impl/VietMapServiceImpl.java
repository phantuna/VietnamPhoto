package com.example.backend.utils.vietmap.impl;

import com.example.backend.config.app.VietMapConfig;
import com.example.backend.dto.response.vietmap.VietMapReverseResponse;
import com.example.backend.dto.response.vietmap.VietMapSearchResponse;
import com.example.backend.utils.vietmap.VietMapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import com.example.backend.exception.AppException;
import com.example.backend.exception.ErrorCode;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VietMapServiceImpl implements VietMapService {

    private final VietMapConfig vietMapConfig;

    private RestClient restClient() {
        return RestClient.builder()
                .baseUrl(vietMapConfig.getBaseUrl())
                .build();
    }

    @Override
    public VietMapReverseResponse reverse(BigDecimal lat, BigDecimal lng) {
        log.debug("Calling VietMap reverse with lat={}, lng={}", lat, lng);
        try {
            List<VietMapReverseResponse> response = restClient().get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/reverse/v4")
                            .queryParam("apikey", vietMapConfig.getApiKey())
                            .queryParam("lat", lat)
                            .queryParam("lng", lng)
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<VietMapReverseResponse>>() {});

            if (response == null || response.isEmpty()) {
                return null;
            }
            return response.get(0);
        } catch (org.springframework.web.client.HttpClientErrorException.Unauthorized e) {
            throw new AppException(ErrorCode.EXTERNAL_SERVICE_ERROR);
        } catch (Exception e) {
            throw new AppException(ErrorCode.EXTERNAL_SERVICE_ERROR);
        }
    }

    @Override
    public List<VietMapSearchResponse> searchNearby(BigDecimal lat, BigDecimal lng, int radiusMeters, String text) {
        return restClient().get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search/v4")
                        .queryParam("apikey", vietMapConfig.getApiKey())
                        .queryParam("text", text)
                        .queryParam("focus", lat + "," + lng)
                        .queryParam("circle_center", lat + "," + lng)
                        .queryParam("circle_radius", radiusMeters)
                        .queryParam("layers", "POI")
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<List<VietMapSearchResponse>>() {});
    }
}
