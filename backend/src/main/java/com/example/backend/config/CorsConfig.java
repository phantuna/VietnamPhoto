package com.example.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Autowired
    private RateLimitInterceptor rateLimitInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns("/api/**"); // Chỉ áp dụng giới hạn cho API, tránh làm chậm web tĩnh
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Áp dụng cho tất cả các API
                .allowedOrigins(
                    "http://localhost:3000",             // Local development
                    "https://app.vnscout.io.vn",         // Production domain
                    "https://vnscout.io.vn"              // Production domain (optional)
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Các method được phép
                .allowedHeaders("*") // Cho phép tất cả các header
                .allowCredentials(true); // Cho phép gửi cookie/token
    }
}
