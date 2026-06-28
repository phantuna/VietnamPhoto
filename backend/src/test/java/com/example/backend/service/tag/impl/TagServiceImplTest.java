package com.example.backend.service.tag.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.backend.entity.Tags;
import com.example.backend.exception.AppException;
import com.example.backend.exception.ErrorCode;
import com.example.backend.mapper.TagMapper;
import com.example.backend.repository.tag.TagsRepository;
import com.example.backend.service.banned.BannedWordCacheService;

@ExtendWith(MockitoExtension.class)
public class TagServiceImplTest {

    @Mock
    private TagsRepository tagsRepository;

    @Mock
    private TagMapper tagMapper;

    @Mock
    private BannedWordCacheService bannedWordCacheService;

    @InjectMocks
    private TagServiceImpl tagService;

    private Tags tag;

    @BeforeEach
    void setUp() {
        tag = new Tags();
        tag.setId("tag-123");
        tag.setName("saigon");
    }

    @Test
    void getOrCreateTag_BannedWord_ThrowsAppException() {
        // Mock tagName chứa từ cấm
        when(bannedWordCacheService.isBanned("dit")).thenReturn(true);

        assertThatThrownBy(() -> tagService.getOrCreateTag("#dit"))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.INVALID_TAG.name());
    }

    @Test
    void getOrCreateTag_ExistingTag_ReturnsTag() {
        when(bannedWordCacheService.isBanned("saigon")).thenReturn(false);
        when(tagsRepository.findByName("saigon")).thenReturn(Optional.of(tag));

        Tags result = tagService.getOrCreateTag("#Saigon  ");

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("saigon");
        verify(tagsRepository, times(0)).save(any());
    }

    @Test
    void createTagStrict_TagExisted_ThrowsAppException() {
        when(bannedWordCacheService.isBanned("saigon")).thenReturn(false);
        when(tagsRepository.findByName("saigon")).thenReturn(Optional.of(tag));

        assertThatThrownBy(() -> tagService.createTagStrict("#Saigon"))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.TAG_EXISTED.name());
    }

    @Test
    void deleteTag_TagNotFound_ThrowsAppException() {
        when(tagsRepository.findById("tag-999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tagService.deleteTag("tag-999"))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.TAG_NOT_FOUND.name());
    }
}
