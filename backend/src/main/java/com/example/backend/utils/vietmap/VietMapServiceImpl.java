package com.example.backend.utils.vietmap;

import com.example.backend.config.VietMapConfig;
import com.example.backend.dto.response.vietmap.VietMapReverseResponse;
import com.example.backend.dto.response.vietmap.VietMapSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VietMapServiceImpl implements VietMapService{

    private final VietMapConfig vietMapConfig;

    private RestClient restClient() {
        return RestClient.builder()
                .baseUrl(vietMapConfig.getBaseUrl())
                .build();
    }

    @Override
    public VietMapReverseResponse reverse(BigDecimal lat, BigDecimal lng) {
        List<VietMapReverseResponse> response = restClient().get()
                .uri(uriBuilder -> uriBuilder
                        .path("/reverse/v4")
                        .queryParam("apikey", vietMapConfig.getApiKey())
                        .queryParam("lat", lat)
                        .queryParam("lng", lng)
                        .queryParam("display_type", vietMapConfig.getReverseDisplayType())
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<List<VietMapReverseResponse>>() {});

        if (response == null || response.isEmpty()) {
            return null;
        }
        return response.get(0);
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
                        .queryParam("display_type", vietMapConfig.getSearchDisplayType())
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<List<VietMapSearchResponse>>() {});
    }
}
