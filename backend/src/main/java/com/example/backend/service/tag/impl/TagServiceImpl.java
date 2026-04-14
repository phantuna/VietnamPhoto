package com.example.backend.service.tag.impl;

import com.example.backend.dto.response.TagResponse;
import com.example.backend.entity.Tags;
import com.example.backend.mapper.TagMapper;
import com.example.backend.repository.TagsRepository;
import com.example.backend.service.banned.BannedWordCacheService;
import com.example.backend.service.tag.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagsRepository tagsRepository;
    private final TagMapper tagMapper;
    private final BannedWordCacheService bannedWordCacheService;

    // alias cơ bản để bắt mấy kiểu viết lách luật phổ biến
    private static final Set<String> HARD_BLOCK_ALIASES = Set.of(
            "ditme", "ditmee", "ditmemay", "dm", "dmm", "dcm", "dtm", "d3tm", "djtm",
            "fuck", "fck", "shit", "loz", "lol", "vl", "vcl"
    );

    @Override
    @Transactional
    public Tags getOrCreateTag(String tagName) {
        String cleanName = normalizeTagName(tagName);

        validateTag(cleanName);

        return tagsRepository.findByName(cleanName)
                .orElseGet(() -> createTagSafely(cleanName));
    }

    @Override
    @Transactional
    public Tags createTagStrict(String tagName) {
        String cleanName = normalizeTagName(tagName);

        validateTag(cleanName);

        tagsRepository.findByName(cleanName)
                .ifPresent(existing -> {
                    throw new RuntimeException("Tag đã tồn tại");
                });

        return createTagSafely(cleanName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TagResponse> getAllTags() {
        return tagsRepository.findAll().stream()
                .map(tagMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TagResponse> searchTags(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllTags();
        }

        String cleanKeyword = keyword.trim().toLowerCase();

        if (cleanKeyword.startsWith("#")) {
            cleanKeyword = cleanKeyword.substring(1).trim();
        }

        return tagsRepository.findByNameContainingIgnoreCase(cleanKeyword).stream()
                .map(tagMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public TagResponse updateTag(String tagId, String newName) {
        Tags tag = tagsRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tag"));

        String cleanName = normalizeTagName(newName);

        validateTag(cleanName);

        tagsRepository.findByName(cleanName)
                .filter(existingTag -> !existingTag.getId().equals(tag.getId()))
                .ifPresent(existingTag -> {
                    throw new RuntimeException("Tên tag này đã tồn tại");
                });

        tag.setName(cleanName);
        return tagMapper.toResponse(tagsRepository.save(tag));
    }

    @Override
    @Transactional
    public void deleteTag(String tagId) {
        Tags tag = tagsRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tag"));

        tagsRepository.delete(tag);
    }

    private String normalizeTagName(String tagName) {
        if (tagName == null || tagName.trim().isEmpty()) {
            throw new RuntimeException("Tag không được để trống");
        }

        String cleanName = tagName.toLowerCase().trim();

        if (cleanName.startsWith("#")) {
            cleanName = cleanName.substring(1).trim();
        }

        cleanName = cleanName.replaceAll("\\s+", "");

        if (cleanName.isEmpty()) {
            throw new RuntimeException("Tag không hợp lệ");
        }

        return cleanName;
    }

    private void validateTag(String tagName) {
        String normalized = normalizeForModeration(tagName);

        // 1. check alias cứng trước
        if (HARD_BLOCK_ALIASES.contains(normalized)) {
            throw new RuntimeException("Tag chứa nội dung không phù hợp");
        }

        // 2. check alias kiểu chứa chuỗi
        for (String alias : HARD_BLOCK_ALIASES) {
            if (normalized.contains(alias)) {
                throw new RuntimeException("Tag chứa nội dung không phù hợp");
            }
        }

        // 3. check DB cache
        if (bannedWordCacheService.isBanned(normalized)) {
            throw new RuntimeException("Tag chứa nội dung không phù hợp");
        }
    }

    private String normalizeForModeration(String input) {
        if (input == null) {
            return "";
        }

        return input.toLowerCase()
                .trim()
                .replace("!", "i")
                .replace("@", "a")
                .replace("$", "s")
                .replace("€", "e")
                .replaceAll("#", "")
                .replaceAll("[^a-z0-9]", "")
                .replaceAll("0", "o")
                .replaceAll("1", "i")
                .replaceAll("3", "e")
                .replaceAll("4", "a")
                .replaceAll("5", "s")
                .replaceAll("7", "t");
    }

    private Tags createTagSafely(String cleanName) {
        try {
            Tags newTag = new Tags();
            newTag.setName(cleanName);
            return tagsRepository.saveAndFlush(newTag);
        } catch (DataIntegrityViolationException e) {
            return tagsRepository.findByName(cleanName)
                    .orElseThrow(() -> e);
        }
    }
}