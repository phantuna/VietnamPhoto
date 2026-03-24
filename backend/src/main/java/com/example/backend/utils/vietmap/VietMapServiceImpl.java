package com.example.backend.utils.vietmap;

import com.example.backend.config.VietMapConfig;
import com.example.backend.dto.response.vietmap.VietMapReverseResponse;
import com.example.backend.dto.response.vietmap.VietMapSearchResponse;
import jakarta.annotation.PostConstruct;
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
        System.out.println("Calling VietMap reverse with lat=" + lat + ", lng=" + lng);

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
            throw new RuntimeException("VietMap API key không hợp lệ hoặc chưa được cấp quyền Reverse API", e);
        } catch (Exception e) {
            throw new RuntimeException("Gọi VietMap Reverse thất bại", e);
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
