package com.example.backend.service.banned;

import com.example.backend.entity.BannedWord;
import com.example.backend.repository.BannedWordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BannedWordService {

    private final BannedWordRepository bannedWordRepository;
    private final BannedWordCacheService bannedWordCacheService;

    @Transactional
    public BannedWord addWord(String word, String type, String language) {
        String cleanWord = normalizeWordForStore(word);
        String cleanType = normalizeType(type);
        String cleanLanguage = normalizeLanguage(language);

        bannedWordRepository.findByWord(cleanWord)
                .ifPresent(existing -> {
                    throw new RuntimeException("Từ cấm đã tồn tại");
                });

        BannedWord bannedWord = new BannedWord();
        bannedWord.setWord(cleanWord);
        bannedWord.setType(cleanType);
        bannedWord.setLanguage(cleanLanguage);

        BannedWord saved = bannedWordRepository.save(bannedWord);
        bannedWordCacheService.addToCache(saved);

        return saved;
    }

    @Transactional
    public void deleteWord(String id) {
        BannedWord bannedWord = bannedWordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy từ cấm"));

        bannedWordRepository.delete(bannedWord);
        bannedWordCacheService.removeFromCache(bannedWord);
    }

    private String normalizeWordForStore(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new RuntimeException("Từ cấm không được để trống");
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
        if (!cleanType.equals("EXACT") && !cleanType.equals("CONTAINS")) {
            throw new RuntimeException("Type chỉ được là EXACT hoặc CONTAINS");
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