package com.example.backend.controller;

import com.example.backend.dto.response.NotificationResponse;
import com.example.backend.service.notification.NotificationService;
import com.example.backend.service.notification.NotificationSseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationSseService notificationSseService;

    @GetMapping

    public Page<NotificationResponse> getMyNotifications(
            @AuthenticationPrincipal String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAtTime"));
        return notificationService.getMyNotifications(userId, pageable);
    }

    @GetMapping("/unread-count")
    public Long getUnreadCount(@AuthenticationPrincipal String userId) {
        return notificationService.getUnreadCount(userId);
    }

    @PutMapping("/{id}/read")
    public void markAsRead(
            @PathVariable String id,
            @AuthenticationPrincipal String userId) {
        notificationService.markAsRead(userId, id);
    }

    @PutMapping("/read-all")
    public void markAllAsRead(@AuthenticationPrincipal String userId) {
        notificationService.markAllAsRead(userId);
    }

    @GetMapping(value = "/stream", produces = "text/event-stream")
    public SseEmitter streamNotifications(@AuthenticationPrincipal String userId) {
        return notificationSseService.connect(userId);
    }
}
