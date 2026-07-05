package com.example.backend.service.comment;

import com.example.backend.dto.response.comment.ToxicModerationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ToxicCommentModerationService {

    private final RestTemplate restTemplate;

    @Value("${moderation.toxic-comment-url:http://localhost:8000/check-toxic}")
    private String moderationUrl;

    public ToxicCommentModerationService() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(2000); 
        factory.setReadTimeout(2000);    
        this.restTemplate = new RestTemplate(factory);
    }

    public ToxicModerationResponse checkToxic(String text) {
        if (text == null || text.trim().isEmpty()) {
            return ToxicModerationResponse.builder()
                    .text(text)
                    .label("CLEAN")
                    .score(0.0)
                    .action("APPROVED")
                    .build();
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("text", text);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

            log.info("[Toxic Moderation] Requesting PhoBERT to check toxic for text: {}", text);
            ResponseEntity<ToxicModerationResponse> response = restTemplate.postForEntity(
                    moderationUrl,
                    request,
                    ToxicModerationResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("[Toxic Moderation] Response: label={}, action={}, score={}",
                        response.getBody().getLabel(), response.getBody().getAction(), response.getBody().getScore());
                return response.getBody();
            }

            log.warn("[Toxic Moderation] Received non-2xx response: {}", response.getStatusCode());
        } catch (Exception e) {
            log.error("[Toxic Moderation] Error calling moderation service (Fail-open mode active): {}", e.getMessage());
        }

        return ToxicModerationResponse.builder()
                .text(text)
                .label("CLEAN")
                .score(0.0)
                .action("APPROVED")
                .build();
    }
}
