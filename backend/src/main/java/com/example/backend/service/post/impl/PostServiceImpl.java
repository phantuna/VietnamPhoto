package com.example.backend.service.post.impl;

import com.example.backend.dto.request.PostCreateRequest;
import com.example.backend.entity.*;
import com.example.backend.repository.post.PostsRepository;
import com.example.backend.repository.location.LocationsRepository;
import com.example.backend.repository.photo.PhotosRepository;
import com.example.backend.repository.user.UserRepository;
import com.example.backend.service.photo.PhotoVerificationService;
import com.example.backend.service.post.PostService;
import com.example.backend.service.tag.impl.TagServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final UserRepository usersRepository;
    private final LocationsRepository locationsRepository;
    private final PostsRepository postsRepository;
    private final PhotosRepository photosRepository; // Inject thêm PhotosRepository
    private final TagServiceImpl tagService;
    private final PhotoVerificationService photoVerificationService;

    @Transactional
    public Posts createPost(String userId, PostCreateRequest request) {

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Locations location = locationsRepository.findById(request.getLocationId())
                .orElseThrow(() -> new RuntimeException("Location not found"));

        Posts post = new Posts();
        post.setCaption(request.getCaption());
        post.setShootingTip(request.getShootingTip());
        post.setUser(user);
        post.setLocation(location);
        post.setLikeCount(0L);

        // 1. Xử lý Tags
        if (request.getTags() != null) {
            List<Tags> postTags = request.getTags().stream()
                    .map(tagService::getOrCreateTag)
                    .toList();
            post.setTags(new ArrayList<>(postTags));
        }

        // 2. Lấy danh sách ảnh từ DB dựa vào mảng ID Frontend gửi lên
        List<Photos> uploadedPhotos = photosRepository.findAllById(request.getPhotoIds());
        if (uploadedPhotos.isEmpty()) {
            throw new RuntimeException("Không tìm thấy ảnh hợp lệ");
        }

        // 3. Xử lý từng ảnh: Check khoảng cách (Verify Location) & Cập nhật post_id
        for (Photos photo : uploadedPhotos) {
            photo.setPost(post); // Trỏ bức ảnh này về bài post vừa tạo

            PhotoMetadata metadata = photo.getMetadata();
            // Tính toán khoảng cách nếu ảnh có lưu tọa độ GPS
            if (metadata != null && metadata.getGpsLatitude() != null && metadata.getGpsLongitude() != null) {
                double distanceMeters = photoVerificationService.calculateDistanceMeters(metadata, location);
                boolean isVerified = (distanceMeters >= 0 && distanceMeters <= 300); // Check trong bán kính 300m
                photo.setIsLocationVerified(isVerified);
            }
        }

        // 4. Gắn danh sách ảnh vào bài viết
        post.setPhotos(new ArrayList<>(uploadedPhotos));

        // 5. Lưu bài viết (Cascade sẽ tự động cập nhật bảng Photos)
        return postsRepository.save(post);
    }
}