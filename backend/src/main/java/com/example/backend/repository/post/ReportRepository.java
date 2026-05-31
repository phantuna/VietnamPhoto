package com.example.backend.repository.post;

import com.example.backend.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.enums.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface ReportRepository extends JpaRepository<Report, String> {
    long countByStatus(ReportStatus status);
    Page<Report> findByStatus(ReportStatus status, Pageable pageable);
    java.util.List<Report> findByPostId(String postId);
}
