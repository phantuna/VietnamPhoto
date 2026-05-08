package com.example.backend.repository.user;

import com.example.backend.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;


public interface UserRepository extends JpaRepository<Users, String>,UserRepositoryCustom {
     Optional<Users> findByEmail(String email);
     Optional<Users> findByUsername(String username);
     boolean existsByUsername(String username);
     boolean existsByEmail(String email);

     @Modifying
     @Query("""
        UPDATE Users u
        SET u.unreadNotificationCount = COALESCE(u.unreadNotificationCount, 0) + 1
        WHERE u.id = :userId
    """)
     void increaseUnreadNotificationCount(@Param("userId") String userId);

     @Modifying
     @Query("""
        UPDATE Users u
        SET u.unreadNotificationCount =
            CASE
                WHEN COALESCE(u.unreadNotificationCount, 0) > 0
                THEN COALESCE(u.unreadNotificationCount, 0) - 1
                ELSE 0
            END
        WHERE u.id = :userId
    """)
     void decreaseUnreadNotificationCount(@Param("userId") String userId);

     @Modifying
     @Query("""
        UPDATE Users u
        SET u.unreadNotificationCount = 0
        WHERE u.id = :userId
    """)
     void resetUnreadNotificationCount(@Param("userId") String userId);
}
