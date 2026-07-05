package com.example.backend.dto.response.admin;

import com.example.backend.enums.ReportStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportResponse {
    private String id;
    private String postId;
    private String postCaption;
    private String postAuthorId;
    private String postAuthorUsername;
    private String reporterId;
    private String reporterUsername;
    private String reason;
    private ReportStatus status;
    private LocalDateTime createdAt;
}
