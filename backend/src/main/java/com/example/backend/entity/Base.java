package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@EntityListeners(AuditingEntityListener.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public class Base {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36, nullable = false, updatable = false)
    private String id;

    @CreatedDate
    @Column(updatable = false)
    private java.time.LocalDate createdDate;

    @LastModifiedDate
    private java.time.LocalDate modifiedDate;

    @LastModifiedBy
    private String modifiedBy;
    @CreatedBy
    private String createdBy;

    @Column(nullable = false)
    private Integer deleted = 0;

    private LocalDateTime deletedAt;
}
