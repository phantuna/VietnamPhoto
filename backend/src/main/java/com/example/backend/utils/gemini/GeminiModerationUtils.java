package com.example.backend.utils.gemini;

import com.example.backend.dto.response.photo.ModerationResult;
import com.example.backend.service.photo.ImageModerationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Primary
@Service
public class GeminiModerationUtils implements ImageModerationService {

    private static final String MODEL      = "gemini-flash-lite-latest";
    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/"
            + MODEL + ":generateContent?key=";

    private static final String PROMPT = """
            Bạn là hệ thống kiểm duyệt nội dung ảnh cho nền tảng chia sẻ ảnh phong cảnh Việt Nam.
            Phân loại ảnh này theo một trong ba nhãn sau:

            - SAFE: Ảnh phong cảnh, thiên nhiên, kiến trúc, du lịch, đời sống bình thường.
            - WARNING: Ảnh có nội dung nhạy cảm nhẹ (bạo lực nhẹ, gợi cảm nhưng không khỏa thân).
            - UNSAFE: Ảnh vi phạm rõ ràng: khỏa thân, nội dung tình dục, bạo lực nghiêm trọng, kích động thù địch.

            Trả lời CHÍNH XÁC theo định dạng JSON sau, không thêm bất kỳ nội dung nào khác:
            {"label": "SAFE", "reason": "Mô tả ngắn gọn lý do bằng tiếng Việt"}
            """;

    private final String apiKey;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GeminiModerationUtils(
            @Value("${gemini.api.key}") String apiKey) {
        this.apiKey       = apiKey;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public ModerationResult moderate(MultipartFile file) {
        try {
            byte[] imageBytes  = file.getBytes();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            String mimeType    = resolveMimeType(file);

            Map<String, Object> inlineData = new HashMap<>();
            inlineData.put("mime_type", mimeType);
            inlineData.put("data", base64Image);

            Map<String, Object> imagePart = new HashMap<>();
            imagePart.put("inline_data", inlineData);

            Map<String, Object> textPart = new HashMap<>();
            textPart.put("text", PROMPT);

            Map<String, Object> content = new HashMap<>();
            content.put("parts", List.of(imagePart, textPart));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", List.of(content));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> httpRequest = new HttpEntity<>(requestBody, headers);

            String url = GEMINI_URL + apiKey;
            ResponseEntity<String> response = restTemplate.postForEntity(url, httpRequest, String.class);

            log.debug("[Gemini Moderation] HTTP status={}", response.getStatusCode());

            return parseGeminiResponse(response.getBody());

        } catch (Exception e) {

            log.error("[Gemini Moderation] Lỗi khi kiểm duyệt ảnh '{}': {}",
                    file.getOriginalFilename(), e.getMessage(), e);
            return ModerationResult.builder()
                    .blocked(false)
                    .warning(true)
                    .reason("Hệ thống kiểm duyệt tạm thời không khả dụng")
                    .score(0.0)
                    .build();
        }
    }

    private ModerationResult parseGeminiResponse(String responseBody) {
        try {
            JsonNode root    = objectMapper.readTree(responseBody);
            String   rawText = root.at("/candidates/0/content/parts/0/text").asText("").trim();

            log.debug("[Gemini Moderation] raw text: {}", rawText);

            String json = rawText
                    .replaceAll("(?s)```json\\s*", "")
                    .replaceAll("(?s)```\\s*", "")
                    .trim();

            JsonNode parsed = objectMapper.readTree(json);
            String   label  = parsed.path("label").asText("SAFE");
            String   reason = parsed.path("reason").asText(label);

            boolean blocked = "UNSAFE".equalsIgnoreCase(label);
            boolean warning = "WARNING".equalsIgnoreCase(label);
            double  score   = blocked ? 1.0 : (warning ? 0.5 : 0.0);

            log.info("[Gemini Moderation] label={} score={} reason={}", label, score, reason);

            return ModerationResult.builder()
                    .blocked(blocked)
                    .warning(warning)
                    .reason(reason)
                    .score(score)
                    .build();

        } catch (Exception e) {
            log.warn("[Gemini Moderation] Không parse được response: {}", responseBody);
            return ModerationResult.builder()
                    .blocked(false)
                    .warning(true)
                    .reason("Không xác định được kết quả kiểm duyệt")
                    .score(0.0)
                    .build();
        }
    }

    private String resolveMimeType(MultipartFile file) {
        String ct = file.getContentType();
        if (ct != null && ct.startsWith("image/")) return ct;

        String name = file.getOriginalFilename();
        if (name != null) {
            String lower = name.toLowerCase();
            if (lower.endsWith(".png"))  return "image/png";
            if (lower.endsWith(".webp")) return "image/webp";
            if (lower.endsWith(".heic")) return "image/heic";
            if (lower.endsWith(".heif")) return "image/heif";
            if (lower.endsWith(".gif"))  return "image/gif";
        }
        return "image/jpeg";
    }
}
