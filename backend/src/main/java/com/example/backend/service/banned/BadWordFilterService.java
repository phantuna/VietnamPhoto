package com.example.backend.service.banned;

import com.example.backend.entity.BannedWord;
import com.example.backend.repository.tag.BannedWordRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class BadWordFilterService {

    private final BannedWordRepository bannedWordRepository;

    private List<String> badWords = new ArrayList<>();
    private List<Pattern> badWordsPatterns = new ArrayList<>();

    @PostConstruct
    public void init() {
        reloadBadWords();
    }

    public void reloadBadWords() {
        List<BannedWord> bannedWordsEntity = bannedWordRepository.findAll();
        
        badWords = bannedWordsEntity.stream()
                .map(BannedWord::getWord)
                .sorted((a, b) -> Integer.compare(b.length(), a.length()))
                .toList();
        
        badWordsPatterns = badWords.stream()
                .map(this::buildAdvancedRegex)
                .toList();

        log.info("Loaded {} bad words from database into memory.", badWords.size());
    }


    private Pattern buildAdvancedRegex(String word) {
        StringBuilder patternStr = new StringBuilder();
        
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            char lower = Character.toLowerCase(c);

            if (lower == 'đ' || lower == 'd') {
                patternStr.append("[đdĐD]");
            } else if (lower == 'c' || lower == 'k') {
                patternStr.append("[cKkC]");
            } else if (lower == 'i') {
                patternStr.append("[iI!1]");
            } else if (lower == 'e') {
                patternStr.append("[eE3]");
            } else if (lower == 'o') {
                patternStr.append("[oO0]");
            } else if (lower == 'a') {
                patternStr.append("[aA@4]");
            } else if ("[]\\^$.|?*+()".indexOf(c) != -1) {
                patternStr.append("\\").append(c);
            } else {
                patternStr.append(c);
            }
            
            if (i < word.length() - 1) {
                patternStr.append("[\\W_]*");
            }
        }
        
        return Pattern.compile("(?ui)(?<!\\p{L})" + patternStr.toString() + "(?!\\p{L})");
    }

    public String censorText(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }

        String censoredText = input;

        for (int i = 0; i < badWords.size(); i++) {
            Pattern pattern = badWordsPatterns.get(i);
            String word = badWords.get(i);
            String asterisks = "*".repeat(word.length());
            censoredText = pattern.matcher(censoredText).replaceAll(asterisks);
        }

        return censoredText;
    }
}
