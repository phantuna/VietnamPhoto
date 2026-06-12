package com.example.backend.service.banned;

import com.example.backend.entity.BannedWord;
import com.example.backend.exception.AppException;
import com.example.backend.exception.ErrorCode;
import com.example.backend.repository.tag.BannedWordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BannedWordService {

    private final BannedWordRepository bannedWordRepository;
    private final BannedWordCacheService bannedWordCacheService;
    private final BadWordFilterService badWordFilterService;

    @Transactional(readOnly = true)
    public Page<BannedWord> searchWords(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("word").ascending());
        if (keyword == null || keyword.isBlank()) {
            return bannedWordRepository.findAll(pageable);
        }
        return bannedWordRepository.findByWordContainingIgnoreCase(keyword.trim(), pageable);
    }

    @Transactional
    public BannedWord addWord(String word, String type, String language) {
        String cleanWord = normalizeWordForStore(word);
        String cleanType = normalizeType(type);
        String cleanLanguage = normalizeLanguage(language);

        bannedWordRepository.findByWord(cleanWord)
                .ifPresent(existing -> {
                    throw new com.example.backend.exception.AppException(com.example.backend.exception.ErrorCode.BANNED_WORD_EXISTED);
                });

        BannedWord bannedWord = new BannedWord();
        bannedWord.setWord(cleanWord);
        bannedWord.setType(cleanType);
        bannedWord.setLanguage(cleanLanguage);

        BannedWord saved = bannedWordRepository.save(bannedWord);
        bannedWordCacheService.addToCache(saved);
        badWordFilterService.reloadBadWords();

        return saved;
    }

    @Transactional
    public void deleteWord(String id) {
        BannedWord bannedWord = bannedWordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy từ cấm"));

        bannedWordRepository.delete(bannedWord);
        bannedWordCacheService.removeFromCache(bannedWord);
        badWordFilterService.reloadBadWords();
    }

    private String normalizeWordForStore(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new AppException(ErrorCode.VALIDATION_FAILED);
        }

        return input.toLowerCase()
                .trim()
                .replaceAll("\\s+", " ");
    }

    private String normalizeType(String type) {
        if (type == null || type.isBlank()) {
            return "EXACT";
        }

        String cleanType = type.trim().toUpperCase();
        if (!type.equalsIgnoreCase("EXACT") && !type.equalsIgnoreCase("CONTAINS")) {
            throw new AppException(ErrorCode.VALIDATION_FAILED);
        }

        return cleanType;
    }

    private String normalizeLanguage(String language) {
        if (language == null || language.isBlank()) {
            return "vi";
        }

        return language.trim().toLowerCase();
    }
}