package com.example.backend.controller.user;

import com.example.backend.dto.request.user.UserRequest;
import com.example.backend.dto.response.user.UserResponse;
import com.example.backend.service.user.UserService;
import lombok.RequiredArgsConstructor;
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
    public List<UserResponse> getAllUsers() {
        return userService.getAll();
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
}
