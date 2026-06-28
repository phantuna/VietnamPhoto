package com.example.backend.dto.response.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToxicModerationResponse {
    private String text;
    private String label;
    private Double score;
    private String action;

    @JsonProperty("all_scores")
    private Map<String, Double> allScores;
}
