package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Photos extends Base{
    @Column(name = "image_url", nullable = false, columnDefinition = "TEXT")
    private String imageUrl;
    private String caption;
    private Integer width;
    private Integer height;
    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "location_verified")
    private Boolean locationVerified = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Locations location;

    @OneToOne(mappedBy = "photo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private PhotoMetadata metadata;

}
