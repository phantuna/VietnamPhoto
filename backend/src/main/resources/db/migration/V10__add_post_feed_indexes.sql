CREATE INDEX idx_posts_feed ON posts(deleted, status, created_date DESC);
CREATE INDEX idx_locations_deleted ON locations(id, deleted);
