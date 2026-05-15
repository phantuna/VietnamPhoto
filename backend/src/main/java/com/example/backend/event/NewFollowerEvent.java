package com.example.backend.event;

import com.example.backend.entity.Users;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class NewFollowerEvent extends ApplicationEvent {

    /** Người vừa bấm Follow */
    private final Users follower;
    /** Người được Follow */
    private final Users following;

    public NewFollowerEvent(Object source, Users follower, Users following) {
        super(source);
        this.follower = follower;
        this.following = following;
    }
}
