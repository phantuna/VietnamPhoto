package com.example.backend.dto.response.post;

import com.example.backend.dto.response.user.UserResponse;
import com.example.backend.dto.response.location.LocationsResponse;
import com.example.backend.dto.request.photo.PhotosRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {

    private String id;
    private String caption;
    private String shootingTip;
    private Long likeCount;
    private Long commentCount;
    private Boolean liked;
    private Boolean isSaved;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;
    
    private Double manualLatitude;
    private Double manualLongitude;

    private UserResponse author;
    private LocationsResponse location;
    private List<String> tags;
    private List<PhotosRequest> photos;

}