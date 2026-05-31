CREATE TABLE saved_posts (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    post_id VARCHAR(36) NOT NULL,
    created_date DATETIME,
    modified_date DATETIME,
    created_by VARCHAR(255),
    modified_by VARCHAR(255),
    deleted INT DEFAULT 0,
    CONSTRAINT fk_saved_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_saved_post FOREIGN KEY (post_id) REFERENCES posts(id),
    CONSTRAINT unique_user_post_saved UNIQUE (user_id, post_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
