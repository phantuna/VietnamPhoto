package com.example.backend.repository.post.report;

import com.example.backend.entity.Report;
import com.example.backend.enums.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReportRepositoryCustom {
    Page<Report> findReportsByStatusWithDetails(ReportStatus status, Pageable pageable);
    Page<Report> findAllReportsWithDetails(Pageable pageable);
    List<Report> findReportsByPostIdWithDetails(String postId);
}
