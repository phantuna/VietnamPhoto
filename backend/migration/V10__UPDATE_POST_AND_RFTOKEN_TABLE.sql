ALTER TABLE posts
    ADD comment_count BIGINT NULL;

ALTER TABLE refresh_tokens
    MODIFY user_id VARCHAR (36) NOT NULL;