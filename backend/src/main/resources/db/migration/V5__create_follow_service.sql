CREATE TABLE user_follows
(
    id            VARCHAR(36) NOT NULL,
    created_date  date NULL,
    modified_date date NULL,
    modified_by   VARCHAR(255) NULL,
    created_by    VARCHAR(255) NULL,
    deleted       INT         NOT NULL,
    deleted_at    datetime NULL,
    follower_id   VARCHAR(36) NOT NULL,
    following_id  VARCHAR(36) NOT NULL,
    CONSTRAINT pk_user_follows PRIMARY KEY (id)
);

ALTER TABLE user_follows
    ADD CONSTRAINT uc_6d853ba591318892d304aa007 UNIQUE (follower_id, following_id);

ALTER TABLE user_follows
    ADD CONSTRAINT FK_USER_FOLLOWS_ON_FOLLOWER FOREIGN KEY (follower_id) REFERENCES users (id);

ALTER TABLE user_follows
    ADD CONSTRAINT FK_USER_FOLLOWS_ON_FOLLOWING FOREIGN KEY (following_id) REFERENCES users (id);

ALTER TABLE photo_metadata
    MODIFY photo_id VARCHAR (255);