CREATE TABLE saved_posts
(
    id            VARCHAR(36) NOT NULL PRIMARY KEY,
    user_id       VARCHAR(36) NOT NULL,
    post_id       VARCHAR(36) NOT NULL,
    created_date  DATE,
    modified_date DATE,
    created_by    VARCHAR(255),
    modified_by   VARCHAR(255),
    deleted       INT         NOT NULL DEFAULT 0,
    deleted_at    DATETIME(6),
    CONSTRAINT fk_saved_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_saved_post FOREIGN KEY (post_id) REFERENCES posts (id),
    CONSTRAINT unique_user_post_saved UNIQUE (user_id, post_id)
);
