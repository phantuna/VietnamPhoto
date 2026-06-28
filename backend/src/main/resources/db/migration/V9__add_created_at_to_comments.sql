ALTER TABLE comments ADD COLUMN created_at DATETIME NULL;
UPDATE comments SET created_at = created_date WHERE created_at IS NULL;
UPDATE comments SET created_at = NOW() WHERE created_at IS NULL;
ALTER TABLE comments MODIFY COLUMN created_at DATETIME NOT NULL;
