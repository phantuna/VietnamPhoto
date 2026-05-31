package com.example.backend.dto.response.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminStatsResponse {
    private long totalUsers;
    private long totalPosts;
    private long pendingReports;
    private List<DailyStat> postsPerDay;
    private List<DailyStat> usersPerDay;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class DailyStat {
        private String day;
        private long count;
    }
}
