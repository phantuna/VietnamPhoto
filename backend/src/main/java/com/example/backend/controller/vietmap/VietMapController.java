package com.example.backend.controller.vietmap;

import com.example.backend.config.app.VietMapConfig;
import com.example.backend.dto.response.location.VietMapLocationResponse;
import com.example.backend.service.location.VietMapLocationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vietmap")
public class VietMapController {

    private final VietMapLocationService vietMapLocationService;
    private final VietMapConfig vietMapConfig;

    @GetMapping("/reverse")
    public VietMapLocationResponse reverse(@RequestParam BigDecimal lat,
                                           @RequestParam BigDecimal lng) {
        return vietMapLocationService.reverse(lat, lng);
    }

    @GetMapping("/proxy")
    public ResponseEntity<byte[]> proxy(@RequestParam String path, HttpServletRequest request) {
        String url = "https://maps.vietmap.vn/" + path;

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
        
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String key = entry.getKey();
            // Bỏ qua path và apikey từ Frontend gửi lên, để dùng mapApiKey riêng của Backend
            if (!key.equals("path") && !key.equals("apikey")) {
                for (String value : entry.getValue()) {
                    builder.queryParam(key, value);
                }
            }
        }

        // Bắt buộc chèn mapApiKey vào request để tải Tile/Style
        String apiKeyToUse = vietMapConfig.getMapApiKey() != null ? vietMapConfig.getMapApiKey() : vietMapConfig.getApiKey();
        builder.queryParam("apikey", apiKeyToUse);

        try {
            ResponseEntity<byte[]> response = RestClient.create().get()
                    .uri(builder.build().toUri())
                    .header("Referer", "https://maps.vietmap.vn/")
                    .header("User-Agent", request.getHeader("User-Agent"))
                    .retrieve()
                    .toEntity(byte[].class);
                    
            return ResponseEntity.status(response.getStatusCode())
                    .header("Content-Type", response.getHeaders().getFirst("Content-Type"))
                    .body(response.getBody());
        } catch (HttpClientErrorException e) {
            log.error("VietMap Proxy Error: {} - Path: {}", e.getStatusCode(), path);
            return ResponseEntity.status(e.getStatusCode())
                    .body(e.getResponseBodyAsByteArray());
        } catch (Exception e) {
            log.error("VietMap Proxy Exception: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
