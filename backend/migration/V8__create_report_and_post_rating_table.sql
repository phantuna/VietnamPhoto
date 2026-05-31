CREATE TABLE post_ratings
(
    id            VARCHAR(36) NOT NULL,
    created_date  date NULL,
    modified_date date NULL,
    modified_by   VARCHAR(255) NULL,
    created_by    VARCHAR(255) NULL,
    deleted       INT         NOT NULL,
    deleted_at    datetime NULL,
    post_id       VARCHAR(36) NOT NULL,
    user_id       VARCHAR(36) NOT NULL,
    rating_value  INT         NOT NULL,
    CONSTRAINT pk_post_ratings PRIMARY KEY (id)
);

CREATE TABLE reports
(
    id            VARCHAR(36)  NOT NULL,
    created_date  date NULL,
    modified_date date NULL,
    modified_by   VARCHAR(255) NULL,
    created_by    VARCHAR(255) NULL,
    deleted       INT          NOT NULL,
    deleted_at    datetime NULL,
    post_id       VARCHAR(36)  NOT NULL,
    reporter_id   VARCHAR(36) NULL,
    reason        VARCHAR(500) NOT NULL,
    status        VARCHAR(255) NOT NULL,
    CONSTRAINT pk_reports PRIMARY KEY (id)
);

ALTER TABLE posts
    ADD average_rating FLOAT NULL;

ALTER TABLE posts
    ADD status VARCHAR(255) NULL;

ALTER TABLE posts
    ADD total_ratings INT NULL;

ALTER TABLE users
    ADD level INT NULL;

ALTER TABLE users
    ADD reputation_score INT NULL;

ALTER TABLE users
    MODIFY level INT NOT NULL;

ALTER TABLE users
    MODIFY reputation_score INT NOT NULL;

ALTER TABLE post_ratings
    ADD CONSTRAINT uc_dc95be1d9cf4212bbdf842dd2 UNIQUE (post_id, user_id);

ALTER TABLE post_ratings
    ADD CONSTRAINT FK_POST_RATINGS_ON_POST FOREIGN KEY (post_id) REFERENCES posts (id);

ALTER TABLE post_ratings
    ADD CONSTRAINT FK_POST_RATINGS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE reports
    ADD CONSTRAINT FK_REPORTS_ON_POST FOREIGN KEY (post_id) REFERENCES posts (id);

ALTER TABLE reports
    ADD CONSTRAINT FK_REPORTS_ON_REPORTER FOREIGN KEY (reporter_id) REFERENCES users (id);

ALTER TABLE photo_metadata
    MODIFY photo_id VARCHAR (255);

ALTER TABLE saved_posts
    MODIFY post_id VARCHAR (36) NULL;

ALTER TABLE saved_posts
    MODIFY user_id VARCHAR (36) NULL;