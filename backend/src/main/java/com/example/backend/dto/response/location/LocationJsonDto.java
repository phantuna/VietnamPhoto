package com.example.backend.dto.response.location;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class LocationJsonDto {
    private String name;
    private String type;
    private String slug;

    @JsonProperty("name_with_type")
    private String nameWithType;

    private String code;

    @JsonProperty("parent_code")
    private String parentCode;
}