package com.example.backend.entity;

import com.example.backend.enums.ReportStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "reports")
public class Report extends Base {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Posts post;

    // The user who reported. Null if auto-flagged by system.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id")
    private Users reporter;

    @Column(name = "reason", nullable = false, length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReportStatus status = ReportStatus.PENDING;
}
