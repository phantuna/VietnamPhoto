package com.example.backend.repository.notification;

import com.example.backend.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationRepositoryCustom {
    Page<Notification> findNotificationsWithDetailsByReceiverId(String receiverId, Integer deleted, Pageable pageable);
}
