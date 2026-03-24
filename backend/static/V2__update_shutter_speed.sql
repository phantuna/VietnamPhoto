ALTER TABLE permission
    DROP COLUMN permission_key;

ALTER TABLE permission
    DROP COLUMN permission_type;

ALTER TABLE permission
    ADD permission_key VARCHAR(255) NULL;

ALTER TABLE permission
    ADD permission_type VARCHAR(255) NULL;


