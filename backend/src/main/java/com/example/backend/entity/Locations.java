package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class Locations extends Base {

    @Column(nullable = false)
    private String name;

    // 🔑 CÁC TRƯỜNG MỚI ĐỂ KHỚP VỚI DỮ LIỆU TỪ JSON
    @Column(unique = true, nullable = false)
    private String code; // Ví dụ: "11" (Hà Nội), "267" (Minh Châu)

    private String type; // Ví dụ: "thanh-pho", "tinh", "xa", "phuong"

    private String slug; // Ví dụ: "ha-noi", "minh-chau"

    @Column(name = "name_with_type")
    private String nameWithType; // Ví dụ: "Thành phố Hà Nội"

    // 🌳 Nhóm Phân cấp (Hierarchy)
    @Column(nullable = false)
    private Integer level; // 0: Tỉnh/Thành phố, 1: Xã/Địa điểm cụ thể

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Locations parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Locations> children = new ArrayList<>();

    // 📍 Nhóm Tọa độ địa lý
    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;

    // 📸 Nhóm Thông tin Mini Social Hub
    @Column(name = "cover_photo")
    private String coverPhoto;

    private String category;

    @Column(name = "golden_hour")
    private String goldenHour;

    @Column(name = "post_count")
    private Long postCount = 0L;

    @Enumerated(EnumType.STRING)
    @Column(name = "location_type")
    private com.example.backend.enums.LocationType locationType = com.example.backend.enums.LocationType.SPOT;

    @Column(name = "creator_id")
    private String creatorId;

    @Column(name = "check_in_count")
    private Long checkInCount = 0L;

    @Column(columnDefinition = "TEXT")
    private String description;
}