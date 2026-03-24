package com.example.backend.entity;

import com.example.backend.enums.PermissionKey;
import com.example.backend.enums.PermissionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Permission {
    @Id
    @Column(length = 50)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "permission_key")
    private PermissionKey permissionKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "permission_type")
    private PermissionType permissionType;
}
