package com.example.backend.service.tag;

import com.example.backend.entity.Tags;

public interface TagService {
    Tags getOrCreateTag(String tagName);
}
