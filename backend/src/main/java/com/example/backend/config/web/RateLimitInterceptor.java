package com.example.backend.config.web;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("Authorization");
        String clientIp = getClientIP(request);

        String key;
        boolean isGuest = true;

        if (token != null && token.startsWith("Bearer ")) {
            key = token;
            isGuest = false;
        } else {
            key = clientIp;
        }

        Bucket bucket = resolveBucket(key, isGuest);

        if (bucket.tryConsume(1)) {
            return true;
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json;charset=UTF-8");
            
            String origin = request.getHeader("Origin");
            if (origin != null && (origin.equals("http://localhost:3000") || origin.equals("https://app.vnscout.io.vn") || origin.equals("https://vnscout.io.vn"))) {
                response.setHeader("Access-Control-Allow-Origin", origin);
                response.setHeader("Access-Control-Allow-Credentials", "true");
            }
            
            response.getWriter().write("{\"error\": \"Bạn đang thao tác quá nhanh, vui lòng thử lại sau giây lát.\"}");
            return false;
        }
    }

    private Bucket resolveBucket(String key, boolean isGuest) {
        return cache.computeIfAbsent(key, k -> newBucket(isGuest));
    }

    private Bucket newBucket(boolean isGuest) {
        int limit = isGuest ? 60 : 120;
        Bandwidth limitBandwidth = Bandwidth.classic(limit, Refill.greedy(limit, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limitBandwidth).build();
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
