package com.example.backend.dto.response.post;

import com.example.backend.dto.response.user.UserResponse;
import com.example.backend.dto.response.location.LocationsResponse;
import com.example.backend.dto.request.photo.PhotosRequest;
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
public class PostResponse {

    private String id;
    private String caption;
    private String shootingTip;
    private Long likeCount;
    private Long commentCount;
    private Boolean liked;
    private Boolean isSaved;
    private LocalDate createdDate;

    private UserResponse author;
    private LocationsResponse location;
    private List<String> tags;
    private List<PhotosRequest> photos;

}