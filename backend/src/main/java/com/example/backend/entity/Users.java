package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name="users")
@SQLDelete(sql = "UPDATE users SET deleted = 1, deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted = 0")
public class Users extends Base {
    @Column(name = "avatar_url")
    private String avatarUrl;
    private String username;
    private String email;
    private String password;
    private LocalDate birthday;
    private String description;
    @Column(name = "unread_notification_count", nullable = false)
    private Long unreadNotificationCount = 0L;

    @Column(name = "reputation_score", nullable = false)
    private Integer reputationScore = 0;

    @Column(name = "level", nullable = false)
    private Integer level = 1;
    @ManyToMany(cascade = {
            CascadeType.MERGE
    })
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles = new ArrayList<>();
}
