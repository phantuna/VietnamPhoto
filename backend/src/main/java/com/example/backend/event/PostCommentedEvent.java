package com.example.backend.event;

import com.example.backend.entity.Posts;
import com.example.backend.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostCommentedEvent {
    private Users actor;
    private Posts post;
    private String commentContent;
}
