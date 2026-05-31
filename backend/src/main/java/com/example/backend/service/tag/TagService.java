package com.example.backend.service.tag;

import com.example.backend.dto.response.tag.TagResponse;
import com.example.backend.entity.Tags;

import java.util.List;

public interface TagService {
    Tags getOrCreateTag(String tagName);
    List<TagResponse> suggestTags(String keyword);
    Tags createTagStrict(String tagName);
    List<TagResponse> getAllTags();
    List<TagResponse> searchTags(String keyword);
    TagResponse updateTag(String tagId, String newName);
    void deleteTag(String tagId);

}
