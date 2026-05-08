CREATE TABLE comments
(
    id            VARCHAR(36) NOT NULL,
    created_date  date NULL,
    modified_date date NULL,
    modified_by   VARCHAR(255) NULL,
    created_by    VARCHAR(255) NULL,
    deleted       INT         NOT NULL,
    deleted_at    datetime NULL,
    content       TEXT        NOT NULL,
    post_id       VARCHAR(36) NOT NULL,
    user_id       VARCHAR(36) NOT NULL,
    parent_id     VARCHAR(36) NULL,
    CONSTRAINT pk_comments PRIMARY KEY (id)
);

CREATE TABLE notifications
(
    id            VARCHAR(36)  NOT NULL,
    created_date  date NULL,
    modified_date date NULL,
    modified_by   VARCHAR(255) NULL,
    created_by    VARCHAR(255) NULL,
    deleted       INT          NOT NULL,
    deleted_at    datetime NULL,
    receiver_id   VARCHAR(36)  NOT NULL,
    actor_id      VARCHAR(36)  NOT NULL,
    post_id       VARCHAR(36) NULL,
    type          VARCHAR(255) NOT NULL,
    content       TEXT         NOT NULL,
    is_read       BIT(1)       NOT NULL,
    CONSTRAINT pk_notifications PRIMARY KEY (id)
);

ALTER TABLE comments
    ADD CONSTRAINT FK_COMMENTS_ON_PARENT FOREIGN KEY (parent_id) REFERENCES comments (id);

ALTER TABLE comments
    ADD CONSTRAINT FK_COMMENTS_ON_POST FOREIGN KEY (post_id) REFERENCES posts (id);

ALTER TABLE comments
    ADD CONSTRAINT FK_COMMENTS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE notifications
    ADD CONSTRAINT FK_NOTIFICATIONS_ON_ACTOR FOREIGN KEY (actor_id) REFERENCES users (id);

ALTER TABLE notifications
    ADD CONSTRAINT FK_NOTIFICATIONS_ON_POST FOREIGN KEY (post_id) REFERENCES posts (id);

ALTER TABLE notifications
    ADD CONSTRAINT FK_NOTIFICATIONS_ON_RECEIVER FOREIGN KEY (receiver_id) REFERENCES users (id);

ALTER TABLE photo_metadata
    MODIFY photo_id VARCHAR (255);

ALTER TABLE users
    MODIFY unread_notification_count BIGINT NOT NULL;