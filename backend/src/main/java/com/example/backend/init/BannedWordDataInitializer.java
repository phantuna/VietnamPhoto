package com.example.backend.init;


import com.example.backend.entity.BannedWord;
import com.example.backend.repository.tag.BannedWordRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class BannedWordDataInitializer implements CommandLineRunner {

    private final BannedWordRepository bannedWordRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void run(String... args) throws Exception {
        if (bannedWordRepository.count() > 0) {
            log.info("Banned words already initialized. Skip import.");
            return;
        }

        List<BannedWord> allWords = new ArrayList<>();
        allWords.addAll(loadWordsFromJson("vi.json", "vi"));
        allWords.addAll(loadWordsFromJson("en.json", "en"));

        List<BannedWord> uniqueWords = allWords.stream()
                .collect(Collectors.toMap(
                        BannedWord::getWord,
                        bw -> bw,
                        (existing, replacement) -> existing
                ))
                .values()
                .stream()
                .toList();

        bannedWordRepository.saveAll(uniqueWords);

        log.info("Initialized {} banned words into database", uniqueWords.size());
    }

    private List<BannedWord> loadWordsFromJson(String fileName, String language) throws Exception {
        ClassPathResource resource = new ClassPathResource(fileName);

        try (InputStream inputStream = resource.getInputStream()) {
            List<String> words = objectMapper.readValue(inputStream, new TypeReference<List<String>>() {});

            return words.stream()
                    .map(this::normalizeWordForStore)
                    .filter(word -> !word.isBlank())
                    .distinct()
                    .map(word -> {
                        BannedWord bannedWord = new BannedWord();
                        bannedWord.setWord(word);
                        bannedWord.setType("EXACT");
                        bannedWord.setLanguage(language);
                        return bannedWord;
                    })
                    .toList();
        }
    }

    private String normalizeWordForStore(String input) {
        if (input == null) {
            return "";
        }

        return input.toLowerCase()
                .trim()
                .replaceAll("\\s+", " ");
    }
}
