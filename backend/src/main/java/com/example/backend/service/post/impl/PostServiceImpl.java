package com.example.backend.service.post.impl;

import com.example.backend.dto.request.post.PostCreateRequest;
import com.example.backend.dto.request.post.PostUpdateRequest;
import com.example.backend.dto.response.PostResponse;
import com.example.backend.entity.*;
import com.example.backend.mapper.PostMapper;
import com.example.backend.repository.post.PostsRepository;
import com.example.backend.repository.location.LocationsRepository;
import com.example.backend.repository.photo.PhotosRepository;
import com.example.backend.repository.user.UserRepository;
import com.example.backend.service.photo.PhotoVerificationService;
import com.example.backend.service.post.PostLikeService;
import com.example.backend.service.post.PostService;
import com.example.backend.service.tag.impl.TagServiceImpl;
import com.example.backend.utils.HashtagUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import com.example.backend.exception.AppException;
import com.example.backend.exception.ErrorCode;
import java.util.HashMap;
import java.util.Map;
@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final UserRepository usersRepository;
    private final LocationsRepository locationsRepository;
    private final PostsRepository postsRepository;
    private final PhotosRepository photosRepository;
    private final TagServiceImpl tagService;
    private final PostMapper postMapper;
    private final PhotoVerificationService photoVerificationService;
    private final PostLikeService postLikeService;

    private static final double MAX_ALLOWED_DISTANCE_METERS = 5000.0;

    @Override
    @Transactional
    public PostResponse createPost(String userId, PostCreateRequest request) {

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Locations location = locationsRepository.findById(request.getLocationId())
                .orElseThrow(() -> new RuntimeException("Location not found"));

        Posts post = new Posts();
        post.setCaption(request.getCaption());
        post.setShootingTip(request.getShootingTip());
        post.setUser(user);
        post.setLocation(location);
        post.setLikeCount(0L);

        Set<String> extractedTags = HashtagUtils.extractHashtags(request.getCaption());
        List<Tags> postTags = extractedTags.stream()
                .map(tagService::getOrCreateTag)
                .toList();
        post.setTags(new ArrayList<>(postTags));

        List<Photos> uploadedPhotos = photosRepository.findAllById(request.getPhotoIds());
        if (uploadedPhotos.isEmpty()) {
            throw new RuntimeException("Không tìm thấy ảnh hợp lệ");
        }

        boolean forceCreate = Boolean.TRUE.equals(request.getForceCreate());

        for (Photos photo : uploadedPhotos) {
            photo.setPost(post);

            PhotoMetadata metadata = photo.getMetadata();

            if (metadata != null
                    && metadata.getGpsLatitude() != null
                    && metadata.getGpsLongitude() != null) {

                double distanceMeters = photoVerificationService.calculateDistanceMeters(metadata, location);

                boolean isVerified = distanceMeters >= 0
                        && distanceMeters <= MAX_ALLOWED_DISTANCE_METERS;

                photo.setIsLocationVerified(isVerified);

                if (!isVerified && !forceCreate) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("distanceMeters", distanceMeters);
                    data.put("allowedDistanceMeters", MAX_ALLOWED_DISTANCE_METERS);
                    data.put("allowContinue", true);

                    throw new AppException(ErrorCode.PHOTO_LOCATION_MISMATCH, data);
                }

            } else {
                photo.setIsLocationVerified(false);
            }
        }

        post.setPhotos(new ArrayList<>(uploadedPhotos));

        Posts savedPost = postsRepository.save(post);
        return postMapper.toResponse(savedPost, false);
    }

    // 🌟 THÊM HÀM NÀY: Dùng nội bộ để lấy Entity gốc thao tác với Database
    private Posts getPostEntityById(String postId) {
        return postsRepository.findByIdWithDetails(postId)
                .orElseThrow(() -> new RuntimeException("Bài viết không tồn tại"));
    }

    @Override
    @Transactional(readOnly = true)
    public PostResponse getPostById(String postId, String userId) {
        Posts post = getPostEntityById(postId);
        boolean liked = userId != null && postLikeService.isLiked(userId, postId);
        return postMapper.toResponse(post, liked);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostResponse> getAllPosts(String userId) {
        List<Posts> allPosts = postsRepository.findAll();

        return allPosts.stream()
                .map(post -> {
                    boolean liked = userId != null && postLikeService.isLiked(userId, post.getId());
                    return postMapper.toResponse(post, liked);
                })
                .toList();
    }

    @Override
    @Transactional
    public PostResponse updatePost(String postId, String userId, PostUpdateRequest request) {
        Posts post = getPostEntityById(postId);

        if (!post.getUser().getId().toString().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền chỉnh sửa bài viết này");
        }

        if (request.getCaption() != null) post.setCaption(request.getCaption());
        if (request.getShootingTip() != null) post.setShootingTip(request.getShootingTip());

        if (request.getTags() != null) {
            List<Tags> newTags = request.getTags().stream()
                    .map(tagService::getOrCreateTag)
                    .toList();
            post.setTags(new ArrayList<>(newTags));
        }

        Posts updatedPost = postsRepository.save(post);

        // user owner đang update, liked có thể true/false tùy user đó từng like hay chưa
        boolean liked = postLikeService.isLiked(userId, postId);
        return postMapper.toResponse(updatedPost, liked);
    }

    @Override
    @Transactional
    public void deletePost(String postId, String userId) {
        Posts post = getPostEntityById(postId);

        if (!post.getUser().getId().toString().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền xóa bài viết này");
        }

        postsRepository.delete(post);
    }
}