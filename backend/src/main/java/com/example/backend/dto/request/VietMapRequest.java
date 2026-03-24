package com.example.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class VietMapRequest {
    private Integer type;
    private Long id;
    private String name;
    private String prefix;
    private String full_name;
}
