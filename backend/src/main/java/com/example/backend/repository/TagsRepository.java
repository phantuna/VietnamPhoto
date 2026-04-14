package com.example.backend.repository;

import com.example.backend.entity.Tags;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TagsRepository extends JpaRepository<Tags, String> {
    Optional<Tags> findByName(String tagName);
}
