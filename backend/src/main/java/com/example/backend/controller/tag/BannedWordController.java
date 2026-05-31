package com.example.backend.controller.tag;


import com.example.backend.entity.BannedWord;
import com.example.backend.service.banned.BannedWordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/banned-words")
@RequiredArgsConstructor
public class BannedWordController {

    private final BannedWordService bannedWordService;

    @PostMapping
    public BannedWord addWord(
            @RequestParam String word,
            @RequestParam(defaultValue = "EXACT") String type,
            @RequestParam(defaultValue = "vi") String language
    ) {
        return bannedWordService.addWord(word, type, language);
    }

    @DeleteMapping("/{id}")
    public void deleteWord(@PathVariable String id) {
        bannedWordService.deleteWord(id);
    }
}
