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

    /**
     * Tải lại danh sách từ cấm từ database vào memory.
     * Có thể gọi hàm này qua API của Admin khi có thay đổi từ cấm.
     */
    public void reloadBadWords() {
        List<BannedWord> bannedWordsEntity = bannedWordRepository.findAll();
        
        // Sắp xếp các từ cấm theo độ dài giảm dần để ưu tiên quét các cụm từ dài trước (ví dụ: "địt mẹ" trước khi quét "dit")
        badWords = bannedWordsEntity.stream()
                .map(BannedWord::getWord)
                .sorted((a, b) -> Integer.compare(b.length(), a.length()))
                .toList();
        
        badWordsPatterns = badWords.stream()
                .map(this::buildAdvancedRegex)
                .toList();

        log.info("Loaded {} bad words from database into memory.", badWords.size());
    }

    /**
     * Chuyển đổi một từ cấm thành Regex có khả năng chống lách luật (Leet Speak, chèn ký tự đặc biệt).
     */
    private Pattern buildAdvancedRegex(String word) {
        StringBuilder patternStr = new StringBuilder();
        
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            // Loại bỏ dấu tiếng Việt để đưa về chữ cái cơ bản
            String normalizedChar = java.text.Normalizer.normalize(String.valueOf(c), java.text.Normalizer.Form.NFD)
                                        .replaceAll("\\p{M}", "").toLowerCase();
            if (normalizedChar.isEmpty()) continue;
            char baseChar = normalizedChar.charAt(0);

            switch (baseChar) {
                case 'a': patternStr.append("[aA@4àáảãạăằắẳẵặâầấẩẫậ]"); break;
                case 'i': patternStr.append("[iI!1|ìíỉĩị]"); break;
                case 'e': patternStr.append("[eE3èéẻẽẹêềếểễệ]"); break;
                case 'o': patternStr.append("[oO0òóỏõọôồốổỗộơờớởỡợ]"); break;
                case 'u': patternStr.append("[uUvVùúủũụưừứửữự]"); break;
                case 'y': patternStr.append("[yYỳýỷỹỵ]"); break;
                case 'd':
                case 'đ': patternStr.append("(đ|d|dđ)"); break;
                case 'c': patternStr.append("[cKk]"); break;
                default:
                    // Escape các ký tự đặc biệt của regex nếu có
                    if ("[]\\^$.|?*+()".indexOf(c) != -1) {
                        patternStr.append("\\").append(c);
                    } else {
                        patternStr.append(c);
                    }
                    break;
            }
            
            // Cho phép chèn các ký tự đặc biệt (dấu chấm, phẩy, khoảng trắng, gạch dưới...) giữa các chữ cái
            if (i < word.length() - 1) {
                patternStr.append("[\\W_]*");
            }
        }
        
        // Dùng (?ui) thay vì (?i) để hỗ trợ Unicode Case Insensitive toàn diện cho tiếng Việt
        return Pattern.compile("(?ui)(?<!\\p{L})" + patternStr.toString() + "(?!\\p{L})");
    }

    /**
     * Lọc và che giấu các từ cấm trong chuỗi đầu vào.
     *
     * @param input Nội dung văn bản cần lọc
     * @return Văn bản đã được thay thế từ cấm bằng dấu sao (***)
     */
    public String censorText(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }

        String censoredText = input;

        for (int i = 0; i < badWords.size(); i++) {
            Pattern pattern = badWordsPatterns.get(i);
            String word = badWords.get(i);
            // Tạo chuỗi dấu sao có độ dài bằng từ gốc
            String asterisks = "*".repeat(word.length());
            // Replace toàn bộ cụm từ match được bằng số dấu sao tương ứng với độ dài từ gốc
            censoredText = pattern.matcher(censoredText).replaceAll(asterisks);
        }

        return censoredText;
    }
}
