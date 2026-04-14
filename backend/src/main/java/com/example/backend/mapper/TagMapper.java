package com.example.backend.mapper;

import com.example.backend.dto.response.TagResponse;
import com.example.backend.entity.Tags;
import org.springframework.stereotype.Component;

@Component
public class TagMapper {

    public TagResponse toResponse(Tags tag) {
        if (tag == null) return null;

        return TagResponse.builder()
                .id(tag.getId())
                .name(tag.getName())
                .build();
    }
}
