package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Photos extends Base{
    @Column(name = "image_url", nullable = false, columnDefinition = "TEXT")
    private String imageUrl;
    private Integer width;
    private Integer height;
    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "location_verified")
    private Boolean isLocationVerified = false;

    @OneToOne(mappedBy = "photo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private PhotoMetadata metadata;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Posts post;
}
