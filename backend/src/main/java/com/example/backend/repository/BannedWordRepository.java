package com.example.backend.repository;

import com.example.backend.entity.BannedWord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BannedWordRepository extends JpaRepository<BannedWord, String> {
    Optional<BannedWord> findByWord(String word);
}
