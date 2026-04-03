package com.example.backend.service.post.impl;

import com.example.backend.dto.request.PostCreateRequest;
import com.example.backend.dto.response.photo.ExifDataDto;
import com.example.backend.dto.response.photo.ModerationResult;
import com.example.backend.dto.response.photo.ProcessedImageResult;
import com.example.backend.entity.*;
import com.example.backend.repository.PostsRepository;
import com.example.backend.repository.location.LocationsRepository;
import com.example.backend.repository.user.UserRepository;
import com.example.backend.service.photo.ExifExtractorService;
import com.example.backend.service.photo.ImageModerationService;
import com.example.backend.service.photo.ImageProcessingService;
import com.example.backend.service.photo.PhotoVerificationService;
import com.example.backend.service.tag.impl.TagServiceImpl;
import com.example.backend.utils.cloudinary.CloudinaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl {

    private final UserRepository usersRepository;
    private final LocationsRepository locationsRepository;
    private final PostsRepository postsRepository;
    private final TagServiceImpl tagService;

    // Các service tiện ích giữ nguyên từ code cũ
    private final ImageModerationService imageModerationService;
    private final ExifExtractorService exifExtractorService;
    private final ImageProcessingService imageProcessingService;
    private final CloudinaryService cloudinaryService;
    private final PhotoVerificationService photoVerificationService;

    @Qualifier("photoUploadExecutor")
    private final Executor photoUploadExecutor;

    @Transactional
    public Posts createPostWithPhotos(UUID userId, PostCreateRequest request, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) throw new RuntimeException("Bài viết phải có ít nhất 1 ảnh");
        if (files.size() > 10) throw new RuntimeException("Tối đa 10 ảnh mỗi bài viết");

        // 1. Lấy thông tin User và Location
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Locations location = locationsRepository.findById(request.getLocationId())
                .orElseThrow(() -> new RuntimeException("Location not found"));

        // 2. Khởi tạo Post
        Posts post = new Posts();
        post.setCaption(request.getCaption());
        post.setShootingTip(request.getShootingTip());
        post.setUser(user);
        post.setLocation(location);
        post.setLikeCount(0L);

        // 3. Xử lý Tags (Hệ thống Khám phá mới)
        if (request.getTags() != null) {
            List<Tags> postTags = request.getTags().stream()
                    .map(tagService::getOrCreateTag)
                    .toList();
            post.setTags(new ArrayList<>(postTags));
        }

        // 4. Lưu Post trước để có ID gán cho Photos
        Posts savedPost = postsRepository.save(post);

        // 5. Xử lý đa luồng upload và trích xuất ảnh
        List<CompletableFuture<Photos>> futures = files.stream()
                .map(file -> CompletableFuture.supplyAsync(() ->
                        processAndUploadSinglePhoto(file, savedPost, location), photoUploadExecutor)
                ).toList();

        try {
            List<Photos> processedPhotos = futures.stream()
                    .map(CompletableFuture::join)
                    .toList();

            // Gắn danh sách ảnh vào Post
            savedPost.setPhotos(new ArrayList<>(processedPhotos));
            return postsRepository.save(savedPost); // Lưu lần cuối cùng với list ảnh

        } catch (Exception e) {
            log.error("Lỗi khi xử lý danh sách ảnh cho Post ID: {}", savedPost.getId(), e);
            throw new RuntimeException("Lỗi khi tạo bài viết và upload ảnh", e);
        }
    }

    // Tách riêng hàm xử lý 1 ảnh (logic từ SinglePhotoUploadServiceImpl cũ chuyển sang)
    private Photos processAndUploadSinglePhoto(MultipartFile file, Posts post, Locations location) {
        // Kiểm duyệt
        ModerationResult moderation = imageModerationService.moderate(file);
        if (moderation.isBlocked()) {
            throw new RuntimeException("Ảnh chứa nội dung vi phạm tiêu chuẩn");
        }

        // Lấy EXIF
        ExifDataDto exifData = exifExtractorService.extract(file);
        PhotoMetadata metadata = buildMetadata(exifData);

        // Verify GPS (so sánh GPS của EXIF với GPS của Location)
        boolean verified = false;
        if (metadata.getGpsLatitude() != null && metadata.getGpsLongitude() != null) {
            double distanceMeters = photoVerificationService.calculateDistanceMeters(metadata, location);
            verified = (distanceMeters >= 0 && distanceMeters <= 300); // Bán kính 300m
        }

        // Xử lý và Upload (nén ảnh, convert)
        String publicId = "user_" + post.getUser().getId() + "/post_" + post.getId() + "/photo_" + UUID.randomUUID();
        ProcessedImageResult processed = imageProcessingService.process(file, 1600, 0.82f);
        String imageUrl = cloudinaryService.uploadImage(processed.getBytes(), publicId);

        // Khởi tạo Entity Photos
        Photos photo = new Photos();
        photo.setPost(post);
        photo.setImageUrl(imageUrl);
        photo.setWidth(processed.getWidth());
        photo.setHeight(processed.getHeight());
        photo.setFileSize(processed.getFileSize());
        photo.setIsLocationVerified(verified);

        // Map Metadata 1-1 với Photo
        metadata.setPhoto(photo);
        photo.setMetadata(metadata);

        return photo;
        // Lưu ý: Không cần gọi photoRepository.save() ở đây vì `post.setPhotos(...)`
        // ở hàm cha kết hợp với `CascadeType.ALL` trong entity Posts sẽ tự động lưu Photos.
    }

    private PhotoMetadata buildMetadata(ExifDataDto exifData) {
        PhotoMetadata metadata = new PhotoMetadata();
        metadata.setCameraMake(exifData.getCameraMake());
        metadata.setCameraModel(exifData.getCameraModel());
        metadata.setLensModel(exifData.getLensModel());
        metadata.setIso(exifData.getIso());
        metadata.setAperture(exifData.getAperture());
        metadata.setShutterSpeed(exifData.getShutterSpeed());
        metadata.setFocalLength(exifData.getFocalLength());
        metadata.setGpsLatitude(exifData.getGpsLatitude());
        metadata.setGpsLongitude(exifData.getGpsLongitude());
        return metadata;
    }
}