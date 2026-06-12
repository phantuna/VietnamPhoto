-- 1. Xử lý lỗi NULL cho bảng users
-- Cập nhật dữ liệu cũ trước (giả sử giá trị mặc định là 0)
UPDATE users SET level = 0 WHERE level IS NULL;
UPDATE users SET reputation_score = 0 WHERE reputation_score IS NULL;

-- Sau đó mới thêm ràng buộc NOT NULL
ALTER TABLE users MODIFY level INT NOT NULL;
ALTER TABLE users MODIFY reputation_score INT NOT NULL;


-- 2. Xử lý lỗi khóa ngoại cho bảng photo_metadata
-- Bước a: Xóa khóa ngoại cũ
ALTER TABLE photo_metadata DROP FOREIGN KEY FKqtybwctvqpk60lu7eob712twm;

-- Bước b: Thay đổi kiểu dữ liệu của cột
ALTER TABLE photo_metadata MODIFY photo_id VARCHAR(255);

-- Bước c: Tạo lại khóa ngoại
-- LƯU Ý: Bạn cần thay thế 'photos' và 'id' bằng tên bảng và tên cột gốc mà photo_id trỏ tới
ALTER TABLE photo_metadata ADD CONSTRAINT FKqtybwctvqpk60lu7eob712twm FOREIGN KEY (photo_id) REFERENCES photos(id);