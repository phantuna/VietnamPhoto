package com.example.backend.controller.tag;


import com.example.backend.entity.BannedWord;
import com.example.backend.service.banned.BannedWordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.example.backend.dto.response.ApiResponse;
import org.springframework.data.domain.Page;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/banned-words")
@RequiredArgsConstructor
public class BannedWordController {

    private final BannedWordService bannedWordService;

    @GetMapping
    public ApiResponse<Page<BannedWord>> getAllWords(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        ApiResponse<Page<BannedWord>> response = new ApiResponse<>();
        response.setResult(bannedWordService.searchWords(keyword, page, size));
        return response;
    }

    @PostMapping
    public ApiResponse<BannedWord> addWord(
            @RequestParam String word,
            @RequestParam(defaultValue = "EXACT") String type,
            @RequestParam(defaultValue = "vi") String language
    ) {
        ApiResponse<BannedWord> response = new ApiResponse<>();
        response.setResult(bannedWordService.addWord(word, type, language));
        return response;
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteWord(@PathVariable String id) {
        bannedWordService.deleteWord(id);
        ApiResponse<String> response = new ApiResponse<>();
        response.setResult("Deleted successfully");
        return response;
    }
}
