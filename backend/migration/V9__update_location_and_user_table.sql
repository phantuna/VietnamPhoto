ALTER TABLE locations
    ADD creator_id VARCHAR(255) NULL;

ALTER TABLE locations
    ADD location_type VARCHAR(255) NULL;

ALTER TABLE users
    MODIFY level INT NOT NULL;

ALTER TABLE photo_metadata
    MODIFY photo_id VARCHAR (255);

ALTER TABLE users
    MODIFY reputation_score INT NOT NULL;