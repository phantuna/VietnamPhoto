CREATE TABLE users
(
    id            BINARY(16)   NOT NULL,
    created_date  date         NULL,
    modified_date date         NULL,
    modified_by   VARCHAR(255) NULL,
    created_by    VARCHAR(255) NULL,
    deleted       INT          NOT NULL,
    deleted_at    datetime     NULL,
    avatar_url    VARCHAR(255) NULL,
    username      VARCHAR(255) NULL,
    email         VARCHAR(255) NULL,
    password      VARCHAR(255) NULL,
    birthday      date         NULL,
    `description` VARCHAR(255) NULL,
    CONSTRAINT pk_user PRIMARY KEY (id)
);

CREATE TABLE `role`
(
    id            VARCHAR(50)  NOT NULL,
    name          VARCHAR(255) NULL,
    `description` VARCHAR(255) NULL,
    CONSTRAINT pk_role PRIMARY KEY (id)
);

CREATE TABLE permission
(
    id              VARCHAR(50)  NOT NULL,
    permission_key  VARCHAR(255) NULL,
    permission_type VARCHAR(255) NULL,
    CONSTRAINT pk_permission PRIMARY KEY (id)
);

CREATE TABLE locations
(
    id            BINARY(16)   NOT NULL,
    created_date  date         NULL,
    modified_date date         NULL,
    modified_by   VARCHAR(255) NULL,
    created_by    VARCHAR(255) NULL,
    deleted       INT          NOT NULL,
    deleted_at    datetime     NULL,
    name          VARCHAR(255) NULL,
    province      VARCHAR(255) NULL,
    district      VARCHAR(255) NULL,
    latitude      DECIMAL      NULL,
    longitude     DECIMAL      NULL,
    `description` VARCHAR(255) NULL,
    CONSTRAINT pk_locations PRIMARY KEY (id)
);

CREATE TABLE photos
(
    id            BINARY(16)   NOT NULL,
    created_date  date         NULL,
    modified_date date         NULL,
    modified_by   VARCHAR(255) NULL,
    created_by    VARCHAR(255) NULL,
    deleted       INT          NOT NULL,
    deleted_at    datetime     NULL,
    image_url     TEXT         NOT NULL,
    caption       VARCHAR(255) NULL,
    width         INT          NULL,
    height        INT          NULL,
    file_size     BIGINT       NULL,
    user_id       BINARY(16)   NULL,
    location_id   BINARY(16)   NULL,
    location_verified BIT(1)   NULL,
    CONSTRAINT pk_photos PRIMARY KEY (id)
);

CREATE TABLE photo_metadata
(
    photo_id      BINARY(16)     NOT NULL,
    camera_make   VARCHAR(100)   NULL,
    camera_model  VARCHAR(100)   NULL,
    lens_model    VARCHAR(100)   NULL,
    iso           INT            NULL,
    aperture      DECIMAL(4, 2)  NULL,
    shutter_speed VARCHAR(20)    NULL,
    focal_length  DECIMAL(5, 2)  NULL,
    gps_latitude  DECIMAL(10, 7) NULL,
    gps_longitude DECIMAL(10, 7) NULL,
    CONSTRAINT pk_photo_metadata PRIMARY KEY (photo_id)
);

CREATE TABLE role_permissions
(
    permissions_id VARCHAR(50) NOT NULL,
    role_id        VARCHAR(50) NOT NULL
);

CREATE TABLE user_role
(
    role_id VARCHAR(50) NOT NULL,
    user_id BINARY(16)  NOT NULL
);

ALTER TABLE photos
    ADD CONSTRAINT FK_PHOTOS_ON_LOCATION FOREIGN KEY (location_id) REFERENCES locations (id);

ALTER TABLE photos
    ADD CONSTRAINT FK_PHOTOS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE photo_metadata
    ADD CONSTRAINT FK_PHOTO_METADATA_ON_PHOTO FOREIGN KEY (photo_id) REFERENCES photos (id);

ALTER TABLE role_permissions
    ADD CONSTRAINT fk_rolper_on_permission FOREIGN KEY (permissions_id) REFERENCES permission (id);

ALTER TABLE role_permissions
    ADD CONSTRAINT fk_rolper_on_role FOREIGN KEY (role_id) REFERENCES `role` (id);

ALTER TABLE user_role
    ADD CONSTRAINT fk_user_role_on_role FOREIGN KEY (role_id) REFERENCES `role` (id);

ALTER TABLE user_role
    ADD CONSTRAINT fk_user_role_on_user FOREIGN KEY (user_id) REFERENCES users (id);