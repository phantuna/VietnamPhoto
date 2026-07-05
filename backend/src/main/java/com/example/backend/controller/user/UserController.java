package com.example.backend.controller.user;

import com.example.backend.dto.request.user.ChangePasswordRequest;
import com.example.backend.dto.request.user.UserRequest;
import com.example.backend.dto.response.ApiResponse;
import com.example.backend.dto.response.user.UserResponse;
import com.example.backend.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public UserResponse createUser(@RequestBody UserRequest dto){
        return userService.createUser(dto);
    }
    @GetMapping("/getall")
    public Page<UserResponse> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return userService.getAllUsers(page, size);
    }

    @GetMapping("/me")
    public UserResponse getMe() {
        return userService.getMe();
    }


    @GetMapping("/{userId}")
    public UserResponse getUserById(@PathVariable String userId) {
        return userService.getById(userId);
    }

    @PutMapping("/{userId}")
    public UserResponse updateUser(
            @PathVariable String userId,
            @RequestBody UserRequest request
    ) {
        return userService.updateUser(userId, request);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
    }

    @PutMapping("/change-password")
    public ApiResponse<String> changePassword(@RequestBody ChangePasswordRequest request) {
        userService.changePassword(request);
        ApiResponse<String> response = new ApiResponse<>();
        response.setResult("Đổi mật khẩu thành công");
        return response;
    }
}
