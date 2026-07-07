package com.example.backend.controller;

import com.example.backend.dto.response.ApiResponse;
import com.example.backend.dto.response.admin.*;
import com.example.backend.entity.Locations;
import com.example.backend.entity.Users;
import com.example.backend.service.admin.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<AdminStatsResponse> getStats() {
        ApiResponse<AdminStatsResponse> response = new ApiResponse<>();
        response.setResult(adminService.getDashboardStats());
        return response;
    }

    @GetMapping("/reports")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Page<ReportResponse>> getReports(
            @RequestParam(required = false, defaultValue = "PENDING") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        ApiResponse<Page<ReportResponse>> response = new ApiResponse<>();
        response.setResult(adminService.getReports(status, page, size));
        return response;
    }

    @PutMapping("/reports/{id}/resolve")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> resolveReport(@PathVariable("id") String reportId) {
        adminService.resolveReport(reportId);
        ApiResponse<String> response = new ApiResponse<>();
        response.setResult("Đã xử lý vi phạm: Ẩn bài viết và trừ 20 điểm uy tín của tác giả.");
        return response;
    }

    @PutMapping("/reports/{id}/dismiss")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> dismissReport(@PathVariable("id") String reportId) {
        adminService.dismissReport(reportId);
        ApiResponse<String> response = new ApiResponse<>();
        response.setResult("Đã bỏ qua báo cáo này.");
        return response;
    }

    @PutMapping("/users/{id}/ban")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> banUser(@PathVariable("id") String userId) {
        adminService.banUser(userId);
        ApiResponse<String> response = new ApiResponse<>();
        response.setResult("Tài khoản đã bị khóa thành công.");
        return response;
    }

    @PutMapping("/users/{id}/unban")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> unbanUser(@PathVariable("id") String userId) {
        adminService.unbanUser(userId);
        ApiResponse<String> response = new ApiResponse<>();
        response.setResult("Tài khoản đã được mở khóa thành công.");
        return response;
    }

    @PutMapping("/users/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> updateUserRole(@PathVariable("id") String userId, @RequestParam("isAdmin") boolean isAdmin) {
        adminService.updateUserRole(userId, isAdmin);
        ApiResponse<String> response = new ApiResponse<>();
        response.setResult(isAdmin ? "Đã cấp quyền Admin thành công." : "Đã hủy quyền Admin thành công.");
        return response;
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Page<AdminUserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        ApiResponse<Page<AdminUserResponse>> response = new ApiResponse<>();
        response.setResult(adminService.getAllUsers(page, size));
        return response;
    }

    @GetMapping("/posts")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Page<AdminPostResponse>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "false", required = false) boolean pending) {
        ApiResponse<Page<AdminPostResponse>> response = new ApiResponse<>();
        if (pending) {
            response.setResult(adminService.getPendingPosts(page, size));
        } else {
            response.setResult(adminService.getAllPosts(page, size));
        }
        return response;
    }

    @PutMapping("/posts/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> approvePost(@PathVariable("id") String postId) {
        adminService.approvePost(postId);
        ApiResponse<String> response = new ApiResponse<>();
        response.setResult("Duyệt bài viết thành công.");
        return response;
    }

    @PutMapping("/posts/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> rejectPost(@PathVariable("id") String postId) {
        adminService.rejectPost(postId);
        ApiResponse<String> response = new ApiResponse<>();
        response.setResult("Đã từ chối bài viết.");
        return response;
    }

    @PutMapping("/posts/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> togglePostStatus(@PathVariable("id") String postId, @RequestParam("deleted") int deleted) {
        adminService.togglePostStatus(postId, deleted);
        ApiResponse<String> response = new ApiResponse<>();
        response.setResult("Cập nhật trạng thái bài viết thành công.");
        return response;
    }

    @GetMapping("/posts/{id}/reports")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<ReportResponse>> getPostReportHistory(@PathVariable("id") String postId) {
        ApiResponse<List<ReportResponse>> response = new ApiResponse<>();
        response.setResult(adminService.getPostReportHistory(postId));
        return response;
    }

    @GetMapping("/locations")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Page<AdminLocationResponse>> getAllLocations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        ApiResponse<Page<AdminLocationResponse>> response = new ApiResponse<>();
        response.setResult(adminService.getAllLocations(page, size));
        return response;
    }

    @PutMapping("/locations/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> toggleLocationStatus(@PathVariable("id") String locationId, @RequestParam("deleted") int deleted) {
        adminService.toggleLocationStatus(locationId, deleted);
        ApiResponse<String> response = new ApiResponse<>();
        response.setResult("Cập nhật trạng thái địa điểm thành công.");
        return response;
    }
}
