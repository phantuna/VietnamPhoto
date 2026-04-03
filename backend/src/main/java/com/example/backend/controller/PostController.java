//package com.example.backend.controller;
//
//import com.example.backend.dto.request.PostCreateRequest;
//import com.example.backend.dto.response.PostResponse;
//import com.example.backend.entity.Posts;
//import com.example.backend.mapper.PostMapper;
//import com.example.backend.service.post.impl.PostServiceImpl;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestPart;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.List;
//import java.util.UUID;
//// ... import các thư viện khác
//
//@RestController
//@RequestMapping("/api/v1/posts")
//@RequiredArgsConstructor
//public class PostController {
//
//    private final PostServiceImpl postService;
//    // Cần có 1 Mapper để chuyển từ Posts Entity -> PostResponse
//    private final PostMapper postMapper;
//
//    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<PostResponse> createPost(
//            // Giả sử bạn đang dùng JWT và lấy userId từ token
//            @AuthenticationPrincipal UUID userId,
//
//            // Nhận dữ liệu text (JSON string format)
//            @RequestPart("postData") @Valid PostCreateRequest request,
//
//            // Nhận mảng file ảnh
//            @RequestPart("photos") List<MultipartFile> photos
//    ) {
//        // 1. Gọi service để lưu vào DB và Cloudinary
//        Posts savedPost = postService.createPostWithPhotos(userId, request, photos);
//
//        // 2. Map Entity sang DTO rồi trả về cho Front-end
//        PostResponse response = postMapper.toResponse(savedPost);
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }
//}