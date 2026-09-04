package com.example.backend.service.post.impl;

import com.example.backend.dto.request.post.PostCreateRequest;
import com.example.backend.dto.request.post.PostUpdateRequest;
import com.example.backend.dto.response.post.PostResponse;
import com.example.backend.entity.*;
import com.example.backend.enums.PostStatus;
import com.example.backend.mapper.PostMapper;
import com.example.backend.repository.post.PostsRepository;
import com.example.backend.repository.location.LocationsRepository;
import com.example.backend.repository.photo.PhotosRepository;
import com.example.backend.repository.post.saved.SavedPostRepository;
import com.example.backend.repository.user.UserRepository;
import com.example.backend.service.photo.PhotoVerificationService;
import com.example.backend.service.post.PostLikeService;
import com.example.backend.service.post.PostService;
import com.example.backend.service.user.ReputationService;
import com.example.backend.service.tag.impl.TagServiceImpl;
import com.example.backend.utils.HashtagUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.time.LocalDate;
import java.util.*;
import java.math.BigDecimal;

import com.example.backend.exception.AppException;
import com.example.backend.exception.ErrorCode;
import com.example.backend.service.comment.ToxicCommentModerationService;
import com.example.backend.dto.response.comment.ToxicModerationResponse;

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
    private final SavedPostRepository savedPostRepository;
    private final ReputationService reputationService;
    private final ApplicationEventPublisher eventPublisher;
    private final com.example.backend.service.banned.BadWordFilterService badWordFilterService;
    private final ToxicCommentModerationService toxicCommentModerationService;

    private static final double MAX_ALLOWED_DISTANCE_METERS = 5000.0;

    @Override
    @Transactional
    @CacheEvict(value = {"posts", "admin_posts"}, allEntries = true)
    public PostResponse createPost(String userId, PostCreateRequest request) {

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        int userLevel = user.getLevel() != null ? user.getLevel() : 1;
        int maxPostsPerDay;
        if (userLevel == 1) maxPostsPerDay = 2;
        else if (userLevel == 2) maxPostsPerDay = 5;
        else if (userLevel == 3) maxPostsPerDay = 10;
        else maxPostsPerDay = 20;

        LocalDate today = LocalDate.now();
        long postsToday = postsRepository.countByUserIdAndCreatedDate(userId, today);
        
        if (postsToday >= maxPostsPerDay) {
            throw new AppException(ErrorCode.POST_LIMIT_EXCEEDED);
        }

        Locations location = locationsRepository.findById(request.getLocationId())
                .orElseThrow(() -> new AppException(ErrorCode.LOCATION_NOT_FOUND));

        String cleanCaption = request.getCaption();
        if (cleanCaption != null && !cleanCaption.isBlank()) {
            ToxicModerationResponse moderation = toxicCommentModerationService.checkToxic(cleanCaption);
            if ("REJECTED".equalsIgnoreCase(moderation.getAction())) {
                throw new AppException(ErrorCode.CONTAIN_BANNED_WORDS);
            } else {
                cleanCaption = badWordFilterService.censorText(cleanCaption);
            }
        }

        Posts post = new Posts();
        post.setCaption(cleanCaption);
        post.setShootingTip(request.getShootingTip());
        post.setUser(user);
        post.setLocation(location);
        post.setLikeCount(0L);
        
        if (userLevel < 3) {
            post.setStatus(PostStatus.PENDING_REVIEW);
        } else {
            post.setStatus(PostStatus.ACTIVE);
        }
        
        post.setManualLatitude(request.getManualLatitude());
        post.setManualLongitude(request.getManualLongitude());

        Set<String> allTags = new HashSet<>();
        // Extract tags from caption
        allTags.addAll(HashtagUtils.extractHashtags(request.getCaption()));
        
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            allTags.addAll(request.getTags());
        }

        List<Tags> postTags = allTags.stream()
                .map(tagService::getOrCreateTag)
                .toList();
        post.setTags(new ArrayList<>(postTags));

        List<Photos> uploadedPhotos = photosRepository.findAllById(request.getPhotoIds());
        if (uploadedPhotos.isEmpty()) {
            throw new AppException(ErrorCode.PHOTO_UPLOAD_FAILED);
        }

        boolean forceCreate = Boolean.TRUE.equals(request.getForceCreate());

        for (Photos photo : uploadedPhotos) {
            photo.setPost(post);

            PhotoMetadata metadata = photo.getMetadata();

            if (metadata != null
                    && metadata.getGpsLatitude() != null
                    && metadata.getGpsLongitude() != null) {

                double distanceMeters = photoVerificationService.calculateDistanceMeters(metadata, location);
                boolean isVerified;

                if (location.getLevel() != null && location.getLevel() == 0) {
                    isVerified = photoVerificationService.isProvinceMatch(metadata, location);
                } else {
                    boolean distanceOk = distanceMeters >= 0 && distanceMeters <= MAX_ALLOWED_DISTANCE_METERS;
                    boolean provinceOk = photoVerificationService.isProvinceMatch(metadata, location);
                    isVerified = distanceOk && provinceOk;
                }

                photo.setIsLocationVerified(isVerified);

                if (!isVerified && !forceCreate) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("distanceMeters", distanceMeters);
                    data.put("allowedDistanceMeters", MAX_ALLOWED_DISTANCE_METERS);
                    data.put("photoProvince", metadata.getProvince());
                    data.put("allowContinue", true);

                    throw new AppException(ErrorCode.PHOTO_LOCATION_MISMATCH, data);
                }

            } else {
                photo.setIsLocationVerified(false);
            }
        }

        post.setPhotos(new ArrayList<>(uploadedPhotos));

        boolean hasWarning = false;
        boolean hasLocationVerified = false;

        for (Photos photo : uploadedPhotos) {
            if ("WARNING".equals(photo.getModerationStatus())) {
                hasWarning = true;
            }
            if (Boolean.TRUE.equals(photo.getIsLocationVerified())) {
                hasLocationVerified = true;
            }
        }

        reputationService.addPoints(user, 2, "Upload ảnh thành công");

        if (hasWarning) {
            reputationService.subtractPoints(user, 10, "Đăng ảnh có nhãn WARNING");
        } else {
            reputationService.addPoints(user, 1, "Đăng ảnh an toàn (SAFE)");
        }

        com.example.backend.enums.LocationType locType = location.getLocationType();
        if (locType == null) locType = com.example.backend.enums.LocationType.SPOT;

        if (locType == com.example.backend.enums.LocationType.SPOT) {
            if (hasLocationVerified) {
                reputationService.addPoints(user, 2, "Check-in SPOT vị trí chính xác");
            }
        } else {
            if (userId.equals(location.getCreatorId())) {
            } else {
                reputationService.addPoints(user, 1, "Review địa điểm SERVICE");
            }
        }

        if (request.getShootingTip() != null && !request.getShootingTip().isBlank() &&
            request.getCaption() != null && !request.getCaption().isBlank()) {
            reputationService.addPoints(user, 1, "Có caption và shooting tip");
        }

        Posts savedPost = postsRepository.save(post);

        Locations currentLoc = location;
        while (currentLoc != null) {
            if (currentLoc.getPostCount() == null) currentLoc.setPostCount(0L);
            if (currentLoc.getCheckInCount() == null) currentLoc.setCheckInCount(0L);
            
            if (post.getStatus() == PostStatus.ACTIVE) {
                currentLoc.setPostCount(currentLoc.getPostCount() + 1);
                currentLoc.setCheckInCount(currentLoc.getCheckInCount() + 1);
            }
            
            if (currentLoc.getCoverPhoto() == null && !uploadedPhotos.isEmpty()) {
                currentLoc.setCoverPhoto(uploadedPhotos.get(0).getImageUrl());
            }
            locationsRepository.save(currentLoc);
            currentLoc = currentLoc.getParent();
        }

        eventPublisher.publishEvent(new com.example.backend.event.PostCreatedEvent(this, user, savedPost));

        return postMapper.toResponse(savedPost, false, false);
    }

    private Posts getPostEntityById(String postId) {
        return postsRepository.findByIdWithDetails(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public PostResponse getPostById(String postId, String userId) {
        Posts post = getPostEntityById(postId);
        boolean liked = userId != null && postLikeService.isLiked(userId, postId);
        boolean saved = userId != null && savedPostRepository.existsByUserIdAndPostIdAndDeleted(userId, postId, 0);
        return postMapper.toResponse(post, liked, saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> getAllPosts(String userId, int page, int size) {
        Page<Posts> postPage = postsRepository.findAllPostsWithDetails(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"))
        );

        List<String> postIds = postPage.getContent().stream().map(Posts::getId).toList();
        
        Set<String> likedPostIds = userId != null && !postIds.isEmpty() ? 
            postLikeService.getLikedPostIds(userId, postIds) : Collections.emptySet();
            
        Set<String> savedPostIds = userId != null && !postIds.isEmpty() ? 
            new HashSet<>(savedPostRepository.findSavedPostIdsByUserIdAndPostIdsIn(userId, postIds, 0)) : Collections.emptySet();

        return postPage.map(post -> {
            boolean liked = likedPostIds.contains(post.getId());
            boolean saved = savedPostIds.contains(post.getId());
            return postMapper.toResponse(post, liked, saved);
        });
    }

    private double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // Earth's radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2)
                 + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                 * Math.sin(dLon/2) * Math.sin(dLon/2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> getNearbyPosts(double lat, double lng, double radiusKm, String viewerId, int page, int size) {
        // Calculate Bounding Box
        double latRadians = Math.toRadians(lat);
        
        // 1 degree of latitude is roughly 111.045 km
        double deltaLat = radiusKm / 111.045;
        double minLat = lat - deltaLat;
        double maxLat = lat + deltaLat;
        
        // 1 degree of longitude is roughly 111.045 * cos(lat) km
        double deltaLng = radiusKm / (111.045 * Math.cos(latRadians));
        double minLng = lng - deltaLng;
        double maxLng = lng + deltaLng;

        List<Locations> roughLocations = locationsRepository.findLocationsWithinBoundingBox(
           BigDecimal.valueOf(minLat), BigDecimal.valueOf(maxLat),
        BigDecimal.valueOf(minLng), BigDecimal.valueOf(maxLng)
        );

        List<String> nearbyLocationIds = new ArrayList<>();

        for (Locations loc : roughLocations) {
            if (loc.getLatitude() != null && loc.getLongitude() != null) {
                double distance = haversineKm(lat, lng, loc.getLatitude().doubleValue(), loc.getLongitude().doubleValue());
                if (distance <= radiusKm) {
                    nearbyLocationIds.add(loc.getId());
                }
            }
        }

        if (nearbyLocationIds.isEmpty()) {
            return Page.empty(PageRequest.of(page, size));
        }

        Page<Posts> postPage = postsRepository.findActivePostsByLocationIdsWithDetails(
                nearbyLocationIds,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"))
        );

        List<String> postIds = postPage.getContent().stream().map(Posts::getId).toList();
        
        Set<String> likedPostIds = viewerId != null && !postIds.isEmpty() ? 
            postLikeService.getLikedPostIds(viewerId, postIds) : Collections.emptySet();
            
        Set<String> savedPostIds = viewerId != null && !postIds.isEmpty() ? 
            new HashSet<>(savedPostRepository.findSavedPostIdsByUserIdAndPostIdsIn(viewerId, postIds, 0)) : Collections.emptySet();

        return postPage.map(post -> {
            boolean liked = likedPostIds.contains(post.getId());
            boolean saved = savedPostIds.contains(post.getId());
            return postMapper.toResponse(post, liked, saved);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> searchPosts(String query, String viewerId, int page, int size) {
        Page<Posts> postPage = postsRepository.searchPosts(
                query.trim(),
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"))
        );

        List<String> postIds = postPage.getContent().stream().map(Posts::getId).toList();
        
        Set<String> likedPostIds = viewerId != null && !postIds.isEmpty() ? 
            postLikeService.getLikedPostIds(viewerId, postIds) : Collections.emptySet();
            
        Set<String> savedPostIds = viewerId != null && !postIds.isEmpty() ? 
            new HashSet<>(savedPostRepository.findSavedPostIdsByUserIdAndPostIdsIn(viewerId, postIds, 0)) : Collections.emptySet();

        return postPage.map(post -> {
            boolean liked = likedPostIds.contains(post.getId());
            boolean saved = savedPostIds.contains(post.getId());
            return postMapper.toResponse(post, liked, saved);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> getPostsByLocation(String locationId, String userId, int page, int size) {
        Page<Posts> postPage = postsRepository.findActivePostsByLocationIdWithDetails(
                locationId, 
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"))
        );

        List<String> postIds = postPage.getContent().stream().map(Posts::getId).toList();
        
        Set<String> likedPostIds = userId != null && !postIds.isEmpty() ? 
            postLikeService.getLikedPostIds(userId, postIds) : Collections.emptySet();
            
        Set<String> savedPostIds = userId != null && !postIds.isEmpty() ? 
            new HashSet<>(savedPostRepository.findSavedPostIdsByUserIdAndPostIdsIn(userId, postIds, 0)) : Collections.emptySet();

        return postPage.map(post -> {
            boolean liked = likedPostIds.contains(post.getId());
            boolean saved = savedPostIds.contains(post.getId());
            return postMapper.toResponse(post, liked, saved);
        });
    }

    @Override
    @Transactional
    @CacheEvict(value = {"posts", "admin_posts"}, allEntries = true)
    public PostResponse updatePost(String postId, String userId, PostUpdateRequest request) {
        Posts post = getPostEntityById(postId);

        if (!post.getUser().getId().toString().equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED_POST_ACTION);
        }

        if (request.getCaption() != null) {
            String newCaption = request.getCaption();
            if (!newCaption.isBlank()) {
                ToxicModerationResponse moderation = toxicCommentModerationService.checkToxic(newCaption);
                if ("REJECTED".equalsIgnoreCase(moderation.getAction())) {
                    throw new AppException(ErrorCode.CONTAIN_BANNED_WORDS);
                } else {
                    newCaption = badWordFilterService.censorText(newCaption);
                }
            }
            post.setCaption(newCaption);
        }
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
        boolean saved = savedPostRepository.existsByUserIdAndPostIdAndDeleted(userId, postId, 0);
        return postMapper.toResponse(updatedPost, liked, saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"posts", "admin_posts"}, allEntries = true)
    public void deletePost(String postId, String userId) {
        Posts post = getPostEntityById(postId);

        if (!post.getUser().getId().toString().equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED_POST_ACTION);
        }

        Locations location = post.getLocation();
        if (location != null && post.getStatus() == PostStatus.ACTIVE && (post.getDeleted() == null || post.getDeleted() == 0)) {
            Locations currentLoc = location;
            while (currentLoc != null) {
                long currentPostCount = currentLoc.getPostCount() != null ? currentLoc.getPostCount() : 0L;
                long currentCheckIn = currentLoc.getCheckInCount() != null ? currentLoc.getCheckInCount() : 0L;
                currentLoc.setPostCount(Math.max(0L, currentPostCount - 1));
                currentLoc.setCheckInCount(Math.max(0L, currentCheckIn - 1));
                locationsRepository.save(currentLoc);
                currentLoc = currentLoc.getParent();
            }
        }

        post.setDeleted(1);
        post.setDeletedAt(java.time.LocalDateTime.now());
        postsRepository.save(post);
    }
}