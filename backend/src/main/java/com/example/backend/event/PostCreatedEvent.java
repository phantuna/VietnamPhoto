package com.example.backend.event;

import com.example.backend.entity.Posts;
import com.example.backend.entity.Users;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PostCreatedEvent extends ApplicationEvent {

    private final Users author;
    private final Posts post;

    public PostCreatedEvent(Object source, Users author, Posts post) {
        super(source);
        this.author = author;
        this.post = post;
    }
}
