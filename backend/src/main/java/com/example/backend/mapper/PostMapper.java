package com.example.backend.mapper;

import com.example.backend.dto.request.LocationsRequest;
import com.example.backend.dto.request.PhotosRequest;
import com.example.backend.dto.request.UserRequest;
import com.example.backend.dto.response.PostResponse;
import com.example.backend.entity.Photos;
import com.example.backend.entity.Posts;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PostMapper {

    public PostResponse toResponse(Posts post, boolean liked) {
        if (post == null) return null;

        return PostResponse.builder()
                .id(post.getId())
                .caption(post.getCaption())
                .shootingTip(post.getShootingTip())
                .likeCount(post.getLikeCount() != null ? post.getLikeCount() : 0L)
                .liked(liked)
                .createdDate(post.getCreatedDate())
                .author(mapAuthor(post))
                .location(mapLocation(post))
                .tags(post.getTags() != null
                        ? post.getTags().stream().map(tag -> tag.getName()).toList()
                        : List.of())
                .photos(post.getPhotos() != null
                        ? post.getPhotos().stream().map(this::mapPhoto).toList()
                        : List.of())
                .build();
    }

    private UserRequest mapAuthor(Posts post) {
        if (post.getUser() == null) return null;

        UserRequest author = new UserRequest();
        author.setUsername(post.getUser().getUsername());
//        author.setAvatarUrl(post.getUser().getAvatarUrl());
        return author;
    }

    private LocationsRequest mapLocation(Posts post) {
        if (post.getLocation() == null) return null;

        LocationsRequest location = new LocationsRequest();
        location.setId(post.getLocation().getId());
        location.setName(post.getLocation().getName());
        return location;
    }

    private PhotosRequest mapPhoto(Photos photo) {
        PhotosRequest dto = new PhotosRequest();
        dto.setId(photo.getId());
        dto.setImageUrl(photo.getImageUrl());
        dto.setWidth(photo.getWidth());
        dto.setHeight(photo.getHeight());

        dto.setIsLocationVerified(photo.getIsLocationVerified());

        if (photo.getMetadata() != null) {
            dto.setCameraMake(photo.getMetadata().getCameraMake());
            dto.setCameraModel(photo.getMetadata().getCameraModel());
            dto.setLensModel(photo.getMetadata().getLensModel());
            dto.setIso(photo.getMetadata().getIso());
            dto.setAperture(photo.getMetadata().getAperture());
            dto.setShutterSpeed(photo.getMetadata().getShutterSpeed());
            dto.setFocalLength(photo.getMetadata().getFocalLength());
            dto.setLatitude(photo.getMetadata().getGpsLatitude());
            dto.setLongitude(photo.getMetadata().getGpsLongitude());
        }

        return dto;
    }
}