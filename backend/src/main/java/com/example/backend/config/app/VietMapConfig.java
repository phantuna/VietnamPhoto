package com.example.backend.config.app;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "vietmap")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VietMapConfig {
    private String apiKey;
    private String mapApiKey;
    private String baseUrl;
    private Integer reverseDisplayType = 6;
    private Integer searchDisplayType = 5;

}
