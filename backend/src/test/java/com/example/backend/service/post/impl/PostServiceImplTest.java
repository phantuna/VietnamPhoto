package com.example.backend.service.post.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.example.backend.dto.request.post.PostCreateRequest;
import com.example.backend.dto.request.post.PostUpdateRequest;
import com.example.backend.dto.response.post.PostResponse;
import com.example.backend.entity.Locations;
import com.example.backend.entity.PhotoMetadata;
import com.example.backend.entity.Photos;
import com.example.backend.entity.Posts;
import com.example.backend.entity.Users;
import com.example.backend.enums.LocationType;
import com.example.backend.enums.PostStatus;
import com.example.backend.exception.AppException;
import com.example.backend.exception.ErrorCode;
import com.example.backend.mapper.PostMapper;
import com.example.backend.repository.location.LocationsRepository;
import com.example.backend.repository.photo.PhotosRepository;
import com.example.backend.repository.post.PostsRepository;
import com.example.backend.repository.post.saved.SavedPostRepository;
import com.example.backend.repository.user.UserRepository;
import com.example.backend.service.photo.PhotoVerificationService;
import com.example.backend.service.post.PostLikeService;
import com.example.backend.service.tag.impl.TagServiceImpl;
import com.example.backend.service.user.ReputationService;
import com.example.backend.service.banned.BadWordFilterService;

@ExtendWith(MockitoExtension.class)
public class PostServiceImplTest {

    @Mock
    private UserRepository usersRepository;

    @Mock
    private LocationsRepository locationsRepository;

    @Mock
    private PostsRepository postsRepository;

    @Mock
    private PhotosRepository photosRepository;

    @Mock
    private TagServiceImpl tagService;

    @Mock
    private PostMapper postMapper;

    @Mock
    private PhotoVerificationService photoVerificationService;

    @Mock
    private PostLikeService postLikeService;

    @Mock
    private SavedPostRepository savedPostRepository;

    @Mock
    private ReputationService reputationService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private BadWordFilterService badWordFilterService;

    @InjectMocks
    private PostServiceImpl postService;

    private Users user;
    private Locations location;
    private Photos photo;
    private PostCreateRequest createRequest;
    private Posts post;

    @BeforeEach
    void setUp() {
        user = new Users();
        user.setId("user-111");
        user.setUsername("poster");
        user.setLevel(1);

        location = new Locations();
        location.setId("loc-222");
        location.setName("Hồ Tây");
        location.setLocationType(LocationType.SPOT);
        location.setPostCount(0L);
        location.setCheckInCount(0L);

        photo = new Photos();
        photo.setId("photo-333");
        photo.setMetadata(new PhotoMetadata());

        createRequest = new PostCreateRequest();
        createRequest.setLocationId("loc-222");
        createRequest.setCaption("Nice sunset!");
        createRequest.setPhotoIds(List.of("photo-333"));

        post = new Posts();
        post.setId("post-444");
        post.setUser(user);
        post.setLocation(location);
        post.setCaption("Nice sunset!");
    }

    @Test
    void createPost_ValidRequest_Success() {
        when(usersRepository.findById("user-111")).thenReturn(Optional.of(user));
        when(postsRepository.countByUserIdAndCreatedDate(anyString(), any())).thenReturn(0L);
        when(locationsRepository.findById("loc-222")).thenReturn(Optional.of(location));
        when(badWordFilterService.censorText("Nice sunset!")).thenReturn("Nice sunset!");
        when(photosRepository.findAllById(List.of("photo-333"))).thenReturn(List.of(photo));
        
        when(postsRepository.save(any(Posts.class))).thenReturn(post);
        when(postMapper.toResponse(any(Posts.class), anyBoolean(), anyBoolean())).thenReturn(new PostResponse());

        PostResponse response = postService.createPost("user-111", createRequest);

        assertThat(response).isNotNull();
        verify(reputationService).addPoints(user, 2, "Upload ảnh thành công");
        verify(postsRepository).save(any(Posts.class));
    }

    @Test
    void createPost_PostLimitExceeded_ThrowsAppException() {
        user.setLevel(1);
        when(usersRepository.findById("user-111")).thenReturn(Optional.of(user));
        when(postsRepository.countByUserIdAndCreatedDate(anyString(), any())).thenReturn(2L);

        assertThatThrownBy(() -> postService.createPost("user-111", createRequest))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.POST_LIMIT_EXCEEDED.name());
    }

    @Test
    void createPost_LocationNotFound_ThrowsAppException() {
        when(usersRepository.findById("user-111")).thenReturn(Optional.of(user));
        when(postsRepository.countByUserIdAndCreatedDate(anyString(), any())).thenReturn(0L);
        when(locationsRepository.findById("loc-222")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.createPost("user-111", createRequest))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.LOCATION_NOT_FOUND.name());
    }

    @Test
    void createPost_NoPhotosUploaded_ThrowsAppException() {
        when(usersRepository.findById("user-111")).thenReturn(Optional.of(user));
        when(postsRepository.countByUserIdAndCreatedDate(anyString(), any())).thenReturn(0L);
        when(locationsRepository.findById("loc-222")).thenReturn(Optional.of(location));
        when(badWordFilterService.censorText("Nice sunset!")).thenReturn("Nice sunset!");
        when(photosRepository.findAllById(List.of("photo-333"))).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> postService.createPost("user-111", createRequest))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.PHOTO_UPLOAD_FAILED.name());
    }

    @Test
    void createPost_LocationMismatchNoForce_ThrowsAppException() {
        PhotoMetadata metadata = new PhotoMetadata();
        metadata.setGpsLatitude(java.math.BigDecimal.valueOf(21.0));
        metadata.setGpsLongitude(java.math.BigDecimal.valueOf(105.0));
        photo.setMetadata(metadata);

        createRequest.setForceCreate(false);

        when(usersRepository.findById("user-111")).thenReturn(Optional.of(user));
        when(postsRepository.countByUserIdAndCreatedDate(anyString(), any())).thenReturn(0L);
        when(locationsRepository.findById("loc-222")).thenReturn(Optional.of(location));
        when(badWordFilterService.censorText("Nice sunset!")).thenReturn("Nice sunset!");
        when(photosRepository.findAllById(List.of("photo-333"))).thenReturn(List.of(photo));
        
        // Mock khoảng cách quá xa (> 5km)
        when(photoVerificationService.calculateDistanceMeters(metadata, location)).thenReturn(6000.0);

        assertThatThrownBy(() -> postService.createPost("user-111", createRequest))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.PHOTO_LOCATION_MISMATCH.name());
    }

    @Test
    void updatePost_NotPostOwner_ThrowsAppException() {
        // Post của user-111, nhưng người sửa là user-999
        when(postsRepository.findByIdWithDetails("post-444")).thenReturn(Optional.of(post));

        PostUpdateRequest updateRequest = new PostUpdateRequest();
        updateRequest.setCaption("Updated caption");

        assertThatThrownBy(() -> postService.updatePost("post-444", "user-999", updateRequest))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.UNAUTHORIZED_POST_ACTION.name());
    }
}
