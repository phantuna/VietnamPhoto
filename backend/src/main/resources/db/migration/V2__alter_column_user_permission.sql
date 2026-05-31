ALTER TABLE users
    ADD unread_notification_count BIGINT NULL;

ALTER TABLE users
    MODIFY unread_notification_count BIGINT NOT NULL;

ALTER TABLE permission
DROP
COLUMN permission_key;

ALTER TABLE permission
DROP
COLUMN permission_type;

ALTER TABLE permission
    ADD permission_key VARCHAR(255) NULL;

ALTER TABLE permission
    ADD permission_type VARCHAR(255) NULL;

ALTER TABLE photo_metadata
    MODIFY photo_id VARCHAR (255);