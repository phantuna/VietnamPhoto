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
import com.example.backend.service.post.PostService;
import com.example.backend.service.tag.impl.TagServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    @Transactional
    public PostResponse createPost(String userId, PostCreateRequest request) {

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
            photo.setPost(post);

            PhotoMetadata metadata = photo.getMetadata();
            if (metadata != null && metadata.getGpsLatitude() != null && metadata.getGpsLongitude() != null) {
                double distanceMeters = photoVerificationService.calculateDistanceMeters(metadata, location);
                boolean isVerified = (distanceMeters >= 0 && distanceMeters <= 300);
                photo.setIsLocationVerified(isVerified);
            }
        }

        // 4. Gắn danh sách ảnh vào bài viết
        post.setPhotos(new ArrayList<>(uploadedPhotos));

        // 5. Lưu bài viết và dùng Mapper chuyển sang DTO
        Posts savedPost = postsRepository.save(post);
        return postMapper.toResponse(savedPost); // SỬA Ở ĐÂY
    }

    // 🌟 THÊM HÀM NÀY: Dùng nội bộ để lấy Entity gốc thao tác với Database
    private Posts getPostEntityById(String postId) {
        return postsRepository.findByIdWithDetails(postId)
                .orElseThrow(() -> new RuntimeException("Bài viết không tồn tại"));
    }

    @Override
    public PostResponse getPostById(String postId) {
        // Gọi hàm nội bộ lấy Entity, sau đó map sang DTO trả về cho Controller
        Posts post = getPostEntityById(postId);
        return postMapper.toResponse(post); // SỬA Ở ĐÂY
    }

    @Override
    public List<PostResponse> getAllPosts() {
        List<Posts> allPosts = postsRepository.findAll();

        return allPosts.stream()
                .map(postMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public PostResponse updatePost(String postId, String userId, PostUpdateRequest request) {
        // Phải dùng getPostEntityById thay vì getPostById
        Posts post = getPostEntityById(postId); // SỬA Ở ĐÂY

        // Bảo mật: Check quyền sở hữu
        if (!post.getUser().getId().toString().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền chỉnh sửa bài viết này");
        }

        // Cập nhật các trường cơ bản
        if (request.getCaption() != null) post.setCaption(request.getCaption());
        if (request.getShootingTip() != null) post.setShootingTip(request.getShootingTip());

        // Cập nhật Tags
        if (request.getTags() != null) {
            List<Tags> newTags = request.getTags().stream()
                    .map(tagService::getOrCreateTag)
                    .toList();
            post.setTags(new ArrayList<>(newTags));
        }

        // Lưu bài viết và dùng Mapper chuyển sang DTO
        Posts updatedPost = postsRepository.save(post);
        return postMapper.toResponse(updatedPost); // SỬA Ở ĐÂY
    }

    @Override
    @Transactional
    public void deletePost(String postId, String userId) {
        // Phải dùng getPostEntityById thay vì getPostById
        Posts post = getPostEntityById(postId); // SỬA Ở ĐÂY

        // Bảo mật: Check quyền sở hữu
        if (!post.getUser().getId().toString().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền xóa bài viết này");
        }

        postsRepository.delete(post);
    }
}