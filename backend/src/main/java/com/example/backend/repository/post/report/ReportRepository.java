package com.example.backend.repository.post.report;

import com.example.backend.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.enums.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface ReportRepository extends JpaRepository<Report, String>, ReportRepositoryCustom {
    long countByStatus(ReportStatus status);

    /**
     * Kiểm tra đã có auto-report (reporter=null) với status cho trước chưa.
     * Dùng để tránh spam auto-flag khi nhiều người vote thấp liên tiếp.
     */
    boolean existsByPostAndReporterIsNullAndStatus(com.example.backend.entity.Posts post, ReportStatus status);
}
