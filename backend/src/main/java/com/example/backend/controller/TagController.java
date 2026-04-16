package com.example.backend.controller;

import com.example.backend.dto.response.TagResponse;
import com.example.backend.entity.Tags;
import com.example.backend.service.tag.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping
    public List<TagResponse> getAllTags(
            @RequestParam(required = false) String keyword
    ) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return tagService.getAllTags();
        }
        return tagService.searchTags(keyword);
    }

    @PostMapping
    public Tags createTag(@RequestParam String name) {
        return tagService.createTagStrict(name);
    }

    @PutMapping("/{id}")
    public TagResponse updateTag(
            @PathVariable String id,
            @RequestParam String name
    ) {
        return tagService.updateTag(id, name);
    }

    @DeleteMapping("/{id}")
    public void deleteTag(@PathVariable String id) {

        tagService.deleteTag(id);
    }

    @GetMapping("/suggest")
    public List<TagResponse> suggestTags(@RequestParam String keyword) {
        return tagService.suggestTags(keyword);
    }
}