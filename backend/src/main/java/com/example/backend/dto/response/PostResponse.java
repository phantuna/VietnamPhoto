package com.example.backend.dto.response;

import com.example.backend.dto.request.LocationsRequest;
import com.example.backend.dto.request.PhotosRequest;
import com.example.backend.dto.request.UserRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {

    private UUID id;
    private String caption;
    private String shootingTip;
    private Long likeCount;
    private LocalDate createdDate;

    private UserRequest author;
    private LocationsRequest location;
    private List<String> tags;
    private List<PhotosRequest> photos;

}