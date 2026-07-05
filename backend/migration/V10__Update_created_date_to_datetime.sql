SET FOREIGN_KEY_CHECKS = 0;

-- 1. Bảng posts (Bài viết)
ALTER TABLE posts MODIFY COLUMN created_date datetime NULL;
ALTER TABLE posts MODIFY COLUMN modified_date datetime NULL;

-- 2. Bảng comments (Bình luận)
ALTER TABLE comments MODIFY COLUMN created_date datetime NULL;
ALTER TABLE comments MODIFY COLUMN modified_date datetime NULL;
ALTER TABLE comments MODIFY COLUMN created_at datetime NOT NULL;

-- 3. Bảng users (Người dùng)
ALTER TABLE users MODIFY COLUMN created_date datetime NULL;
ALTER TABLE users MODIFY COLUMN modified_date datetime NULL;

-- 4. Bảng locations (Địa điểm)
ALTER TABLE locations MODIFY COLUMN created_date datetime NULL;
ALTER TABLE locations MODIFY COLUMN modified_date datetime NULL;

-- 5. Bảng tags (Nhãn)
ALTER TABLE tags MODIFY COLUMN created_date datetime NULL;
ALTER TABLE tags MODIFY COLUMN modified_date datetime NULL;

-- 6. Bảng saved_posts (Bài viết đã lưu)
ALTER TABLE saved_posts MODIFY COLUMN created_date datetime NULL;
ALTER TABLE saved_posts MODIFY COLUMN modified_date datetime NULL;

-- 7. Bảng reports (Báo cáo vi phạm)
ALTER TABLE reports MODIFY COLUMN created_date datetime NULL;
ALTER TABLE reports MODIFY COLUMN modified_date datetime NULL;

-- 8. Bảng photos (Ảnh)
ALTER TABLE photos MODIFY COLUMN created_date datetime NULL;
ALTER TABLE photos MODIFY COLUMN modified_date datetime NULL;

-- 9. Bảng likes (Lượt thích)
ALTER TABLE likes MODIFY COLUMN created_date datetime NULL;
ALTER TABLE likes MODIFY COLUMN modified_date datetime NULL;

-- 10. Bảng notifications (Thông báo)
ALTER TABLE notifications MODIFY COLUMN created_date datetime NULL;
ALTER TABLE notifications MODIFY COLUMN modified_date datetime NULL;

-- 11. Bảng chat_messages (Tin nhắn chat)
ALTER TABLE chat_messages MODIFY COLUMN created_date datetime NULL;
ALTER TABLE chat_messages MODIFY COLUMN modified_date datetime NULL;

-- 12. Bảng conversations (Cuộc hội thoại)
ALTER TABLE conversations MODIFY COLUMN created_date datetime NULL;
ALTER TABLE conversations MODIFY COLUMN modified_date datetime NULL;

-- 13. Bảng user_follows (Theo dõi người dùng)
ALTER TABLE user_follows MODIFY COLUMN created_date datetime NULL;
ALTER TABLE user_follows MODIFY COLUMN modified_date datetime NULL;

-- 14. Bảng post_ratings (Đánh giá bài viết)
ALTER TABLE post_ratings MODIFY COLUMN created_date datetime NULL;
ALTER TABLE post_ratings MODIFY COLUMN modified_date datetime NULL;

-- 15. Bảng refresh_tokens
ALTER TABLE refresh_tokens MODIFY id VARCHAR(255);
ALTER TABLE refresh_tokens MODIFY user_id VARCHAR(36);

SET FOREIGN_KEY_CHECKS = 1;