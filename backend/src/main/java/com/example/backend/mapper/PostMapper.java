package com.example.backend.mapper;

import com.example.backend.dto.request.LocationsRequest;
import com.example.backend.dto.request.PhotosRequest;
import com.example.backend.dto.request.UserRequest;
import com.example.backend.dto.response.PostResponse;
import com.example.backend.entity.PhotoMetadata;
import com.example.backend.entity.Photos;
import com.example.backend.entity.Posts;
import com.example.backend.entity.Tags;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.stream.Collectors;

@Component
public class PostMapper {

    public PostResponse toResponse(Posts post) {
        if (post == null) return null;

        PostResponse response = new PostResponse();
        response.setId(post.getId());
        response.setCaption(post.getCaption());
        response.setShootingTip(post.getShootingTip());
        response.setLikeCount(post.getLikeCount());

        // Cần đảm bảo post.getCreatedDate() trả về LocalDate để khớp với PostResponse
        response.setCreatedDate(post.getCreatedDate());

        if (post.getUser() != null) {
            // Map sang UserRequest (Lưu ý: Không có ID và Avatar)
            response.setAuthor(UserRequest.builder()
                    .username(post.getUser().getUsername())
                    .email(post.getUser().getEmail())
                    .birthday(post.getUser().getBirthday())
                    // KHÔNG NÊN map password: .password(post.getUser().getPassword())
                    .build());
        }

        if (post.getLocation() != null) {
            // Map sang LocationsRequest (Lưu ý: Không có ID)
            response.setLocation(LocationsRequest.builder()
                    .name(post.getLocation().getName())
                    .latitude(post.getLocation().getLatitude())
                    .longitude(post.getLocation().getLongitude())
                    .description(post.getLocation().getDescription())
                    .build());
        }

        if (post.getTags() != null) {
            response.setTags(post.getTags().stream()
                    .map(Tags::getName)
                    .collect(Collectors.toList()));
        }

        if (post.getPhotos() != null) {
            response.setPhotos(post.getPhotos().stream()
                    .map(this::mapPhotoToDto)
                    .collect(Collectors.toList()));
        }

        return response;
    }

    private PhotosRequest mapPhotoToDto(Photos photo) {
        if (photo == null) return null;

        PhotosRequest dto = PhotosRequest.builder()
                .id(photo.getId()) // PhotosRequest có id
                .imageUrl(photo.getImageUrl())
                .width(photo.getWidth())
                .height(photo.getHeight())
                .isLocationVerified(photo.getIsLocationVerified())
                .build();

        // Sử dụng PhotoMetadata thay vì ExifDataDto như đã phân tích ở lỗi trước
        PhotoMetadata metadata = photo.getMetadata();
        if (metadata != null) {
            dto.setCameraMake(metadata.getCameraMake());
            dto.setCameraModel(metadata.getCameraModel());
            dto.setLensModel(metadata.getLensModel());
            dto.setIso(metadata.getIso());
            dto.setAperture(metadata.getAperture());
            dto.setShutterSpeed(metadata.getShutterSpeed());
            dto.setFocalLength(metadata.getFocalLength());
        }

        return dto;
    }
}