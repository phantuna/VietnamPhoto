package com.example.backend.dto.response.tag;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TagResponse {

    private String id;
    private String name;
}