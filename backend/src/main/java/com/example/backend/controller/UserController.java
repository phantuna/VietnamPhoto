package com.example.backend.controller;

import com.example.backend.dto.request.UserRequest;
import com.example.backend.dto.response.UserResponse;
import com.example.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/created")
    public UserResponse createdUser(@RequestBody UserRequest dto){

        return userService.createUser(dto);
    }

    @GetMapping("/getall")
    public List<UserResponse> getAllUser(){
        return userService.getAll();
    }
}
