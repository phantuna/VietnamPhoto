package com.example.backend.mapper;

import com.example.backend.dto.response.PostResponse;
import com.example.backend.dto.response.photo.ExifDataDto;
import com.example.backend.entity.Photos;
import com.example.backend.entity.Posts;
import com.example.backend.entity.Tags;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class PostMapper {

//    public PostResponse toResponse(Posts post) {
//        if (post == null) return null;
//
//        PostResponse response = new PostResponse();
//        response.setId(post.getId());
//        response.setCaption(post.getCaption());
//        response.setShootingTip(post.getShootingTip());
//        response.setLikeCount(post.getLikeCount());
//        response.setCreatedDate(post.getCreatedDate());
//
//        if (post.getUser() != null) {
//            response.setAuthor(PostResponse.UserRequest.builder()
//                    .id(post.getUser().getId())
//                    .username(post.getUser().getUsername())
//                    .avatarUrl(post.getUser().getAvatarUrl())
//                    .build());
//        }
//
//        if (post.getLocation() != null) {
//            response.setLocation(PostResponse.LocationDto.builder()
//                    .id(post.getLocation().getId())
//                    .name(post.getLocation().getName())
//                    .province(post.getLocation().getProvince())
//                    .build());
//        }
//
//        if (post.getTags() != null) {
//            response.setTags(post.getTags().stream()
//                    .map(Tags::getName)
//                    .collect(Collectors.toList()));
//        }
//
//        if (post.getPhotos() != null) {
//            response.setPhotos(post.getPhotos().stream()
//                    .map(this::mapPhotoToDto)
//                    .collect(Collectors.toList()));
//        }
//
//        return response;
//    }
//
//    private PostResponse.PhotoDto mapPhotoToDto(Photos photo) {
//        if (photo == null) return null;
//
//        PostResponse.PhotoDto dto = PostResponse.PhotoDto.builder()
//                .id(photo.getId())
//                .imageUrl(photo.getImageUrl())
//                .width(photo.getWidth())
//                .height(photo.getHeight())
//                .isLocationVerified(photo.getIsLocationVerified())
//                .build();
//
//        ExifDataDto metadata = photo.getMetadata();
//        if (metadata != null) {
//            dto.setCameraMake(metadata.getCameraMake());
//            dto.setCameraModel(metadata.getCameraModel());
//            dto.setLensModel(metadata.getLensModel());
//            dto.setIso(metadata.getIso());
//            dto.setAperture(metadata.getAperture());
//            dto.setShutterSpeed(metadata.getShutterSpeed());
//            dto.setFocalLength(metadata.getFocalLength());
//            dto.setCaptureTime(metadata.getCaptureTime()); // Map thời gian chụp
//        }
//
//        return dto;
//    }
}
