package com.example.backend.event;

import com.example.backend.entity.Users;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class NewFollowerEvent extends ApplicationEvent {

    private final Users follower;
    private final Users following;

    public NewFollowerEvent(Object source, Users follower, Users following) {
        super(source);
        this.follower = follower;
        this.following = following;
    }
}
