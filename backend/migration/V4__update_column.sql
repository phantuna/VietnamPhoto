ALTER TABLE notifications
    ADD created_at_time datetime NULL;

ALTER TABLE photo_metadata
    MODIFY photo_id VARCHAR (255);

ALTER TABLE users
    MODIFY unread_notification_count BIGINT NOT NULL;