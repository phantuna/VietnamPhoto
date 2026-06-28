package com.example.backend.service.banned;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.backend.entity.BannedWord;
import com.example.backend.repository.tag.BannedWordRepository;

@ExtendWith(MockitoExtension.class)
public class BadWordFilterServiceTest {

    @Mock
    private BannedWordRepository bannedWordRepository;

    @InjectMocks
    private BadWordFilterService badWordFilterService;

    @BeforeEach
    void setUp() {
        BannedWord word1 = new BannedWord();
        word1.setWord("dit");
        word1.setType("EXACT");
        word1.setLanguage("vi");

        BannedWord word2 = new BannedWord();
        word2.setWord("dit me");
        word2.setType("EXACT");
        word2.setLanguage("vi");

        when(bannedWordRepository.findAll()).thenReturn(List.of(word1, word2));
        
        badWordFilterService.reloadBadWords();
    }

    @Test
    void censorText_cleanText_returnsUnchanged() {
        String input = "Ảnh này chụp đẹp quá!";
        String output = badWordFilterService.censorText(input);
        assertThat(output).isEqualTo(input);
    }

    @Test
    void censorText_nullInput_returnsNull() {
        assertThat(badWordFilterService.censorText(null)).isNull();
    }

    @Test
    void censorText_emptyInput_returnsEmpty() {
        assertThat(badWordFilterService.censorText("")).isEqualTo("");
        assertThat(badWordFilterService.censorText("   ")).isEqualTo("   ");
    }

    @Test
    void censorText_directMatch_censorsWithAsterisks() {
        String input = "Thằng dit này";
        String output = badWordFilterService.censorText(input);
        assertThat(output).isEqualTo("Thằng *** này");
    }

    @Test
    void censorText_leetSpeakMatch_censorsWithAsterisks() {
        String input1 = "Thằng d.i.t này";
        String output1 = badWordFilterService.censorText(input1);
        assertThat(output1).isEqualTo("Thằng *** này");

        String input2 = "Thằng d1t này";
        String output2 = badWordFilterService.censorText(input2);
        assertThat(output2).isEqualTo("Thằng *** này");
    }

    @Test
    void censorText_multipleMatchesAndPriorities_censorsAll() {
        String input = "dit me mày, đồ dit";
        String output = badWordFilterService.censorText(input);
        assertThat(output).isEqualTo("****** mày, đồ ***");
    }
}
