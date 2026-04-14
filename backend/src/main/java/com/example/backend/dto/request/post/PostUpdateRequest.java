package com.example.backend.dto.request.post;

import lombok.Data;
import java.util.List;

@Data
public class PostUpdateRequest {
    private String caption;
    private String shootingTip;
    private List<String> tags;
}
