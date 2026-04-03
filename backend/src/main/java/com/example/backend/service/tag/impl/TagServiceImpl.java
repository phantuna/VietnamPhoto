package com.example.backend.service.tag.impl;

import com.example.backend.entity.Tags;
import com.example.backend.repository.TagsRepository;
import com.example.backend.service.tag.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagsRepository tagsRepository;

    @Override
    public Tags getOrCreateTag(String tagName) {
        String cleanName = tagName.toLowerCase().trim();
        return tagsRepository.findByName(cleanName)
                .orElseGet(() -> {
                    Tags newTag = new Tags();
                    newTag.setName(cleanName);
                    return tagsRepository.save(newTag);
                });
    }
}
