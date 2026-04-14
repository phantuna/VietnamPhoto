package com.example.backend.entity;


import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "banned_words")
public class BannedWord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String word;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String language;
}
