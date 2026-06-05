package com.example.backend.repository.tag;

import com.example.backend.entity.BannedWord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BannedWordRepository extends JpaRepository<BannedWord, String> {
    Optional<BannedWord> findByWord(String word);
    Page<BannedWord> findByWordContainingIgnoreCase(String keyword, Pageable pageable);
}
