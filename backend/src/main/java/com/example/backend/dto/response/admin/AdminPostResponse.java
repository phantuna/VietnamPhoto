package com.example.backend.dto.response.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminPostResponse {
    private String id;
    private String caption;
    private LocalDate createdDate;
    private UserInfo user;
    private LocationInfo location;
    private Float averageRating;
    private Integer totalRatings;
    private Integer deleted;
    private List<PhotoInfo> photos;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserInfo {
        private String id;
        private String username;
        private String email;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LocationInfo {
        private String id;
        private String name;
        private String nameWithType;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PhotoInfo {
        private String id;
        private String imageUrl;
    }
}
