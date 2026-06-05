package com.example.backend.repository.notification;

import com.example.backend.entity.Notification;
import com.example.backend.enums.NotificationType;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<@NonNull Notification, String>, NotificationRepositoryCustom {


    List<Notification> findByActorIdAndPostIdAndTypeAndDeleted(
            String actorId,
            String postId,
            NotificationType type,
            Integer deleted
    );

    @Modifying
    @Query("""
        UPDATE Notification n
        SET n.isRead = true
        WHERE n.id = :notificationId
        AND n.receiver.id = :userId
        AND n.isRead = false
    """)
    int markAsRead(
            @Param("notificationId") String notificationId,
            @Param("userId") String userId
    );

    @Modifying
    @Query("""
        UPDATE Notification n
        SET n.isRead = true
        WHERE n.receiver.id = :userId
        AND n.isRead = false
    """)
    int markAllAsRead(@Param("userId") String userId);
}