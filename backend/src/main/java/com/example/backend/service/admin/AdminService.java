package com.example.backend.service.admin;

import com.example.backend.dto.response.admin.*;
import com.example.backend.entity.Locations;
import com.example.backend.entity.Posts;
import com.example.backend.entity.Users;
import org.springframework.data.domain.Page;
import java.util.List;

public interface AdminService {
    AdminStatsResponse getDashboardStats();
    
    Page<ReportResponse> getReports(String status, int page, int size);
    
    void resolveReport(String reportId);
    
    void dismissReport(String reportId);

    void banUser(String userId);

    void unbanUser(String userId);

    void updateUserRole(String userId, boolean isAdmin);

    Page<AdminUserResponse> getAllUsers(int page, int size);

    Page<AdminPostResponse> getAllPosts(int page, int size);
    
    Page<AdminPostResponse> getPendingPosts(int page, int size);
    
    void approvePost(String postId);
    
    void rejectPost(String postId);

    void togglePostStatus(String postId, int deleted);

    List<ReportResponse> getPostReportHistory(String postId);

    Page<AdminLocationResponse> getAllLocations(int page, int size);

    void toggleLocationStatus(String locationId, int deleted);
}
