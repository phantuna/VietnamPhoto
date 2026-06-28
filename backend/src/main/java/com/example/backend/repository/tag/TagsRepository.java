package com.example.backend.repository.tag;

import com.example.backend.entity.Tags;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TagsRepository extends JpaRepository<Tags, String> {
    Optional<Tags> findByName(String tagName);
    List<Tags> findByNameContainingIgnoreCase(String keyword);

    List<Tags> findTop10ByNameStartingWithIgnoreCaseOrderByNameAsc(String prefix);

    List<Tags> findTop10ByNameContainingIgnoreCaseOrderByNameAsc(String keyword);
}
