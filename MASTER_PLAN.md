# 🚀 MASTER PLAN: Vietnam Photo Scout

## 1. Mục tiêu Hệ thống
Xây dựng một nền tảng chia sẻ ảnh phong cảnh với các tính năng:
*   **Kiểm duyệt ảnh bằng AI:** Tự động phân loại và ngăn chặn nội dung xấu.
*   **Hệ thống Reputation & Level:** Đánh giá mức độ đóng góp, tạo động lực hoạt động, mang ý nghĩa khám phá và nhiếp ảnh.
*   **Hệ thống Đánh giá cộng đồng:** Vote sao cảm tính, báo cáo vi phạm, quản lý bài viết bởi Admin.
*   **Check-in vị trí (Map Check):** Khuyến khích tính chân thực bằng việc xác thực khoảng cách GPS và Location Tag.

---

## 2. Cấu trúc Database (Entities cần cập nhật/thêm mới)

**2.1. Bảng `User` (Hệ thống cấp bậc)**
*   `reputationScore` (Integer, default = 0): Tổng điểm uy tín.
*   `level` (Integer, default = 1): Cấp độ hiện tại của người dùng.

**2.2. Bảng `Post` (Trạng thái & Đánh giá)**
*   `status` (Enum: `ACTIVE`, `PENDING_REVIEW`, `HIDDEN`).
*   `averageRating` (Float, default = 0.0): Điểm sao trung bình.
*   `totalRatings` (Integer, default = 0): Tổng lượt đánh giá sao.

**2.3. Bảng `PostRating` (Đánh giá Sao - Mới)**
*   `id`, `post_id`, `user_id`, `rating_value` (1 - 5).

**2.4. Bảng `Report` (Báo cáo vi phạm - Mới)**
*   `id`, `post_id`, `reporter_id` (null nếu hệ thống tự quét), `reason`, `status` (`PENDING`, `RESOLVED`, `DISMISSED`).

---

## 3. Hệ thống Quy tắc Điểm thưởng & Phạt (Reputation System)

**3.1. AI Kiểm duyệt ảnh (Gemini Moderation)**
*   ✅ **SAFE:** Cho phép đăng bài (`ACTIVE`). **+5 điểm**.
*   ⚠️ **WARNING:** Vẫn cho phép đăng bài (`ACTIVE`). **-15 điểm**.
*   🛑 **UNSAFE:** Chặn đăng bài ngay lập tức.

**3.2. Điểm Hoạt động / Tương tác**
*   Upload ảnh thành công: **+5 điểm**
*   Đăng ảnh Check-in đúng GPS (Khoảng cách <= 2km) / Location mới: **+10 điểm**
*   Có điền *Shooting tip* và *Caption* chất lượng: **+2 điểm**
*   Ảnh được user khác Save/Bookmark: **+3 điểm**
*   Ảnh bị Admin xác nhận vi phạm từ Report Spam: **-20 điểm**

---

## 4. Hệ thống Level Người dùng

| Level | Title (Danh hiệu) | Điểm yêu cầu | Ý nghĩa |
| :--- | :--- | :--- | :--- |
| 1 | **Explorer** | < 20 | Người mới bắt đầu khám phá |
| 2 | **Traveler** | 20+ | Đăng ảnh thường xuyên |
| 3 | **Spot Hunter** | 60+ | Hay khám phá location mới, check-in chuẩn |
| 4 | **Story Teller** | 120+ | Caption và shooting tip cực kỳ tâm huyết |
| 5 | **Light Chaser** | 200+ | Ảnh đẹp, nội dung chất lượng, hoạt động tích cực |
| 6 | **Master Scout** | 300+ | Người dùng nổi bật, leader của cộng đồng |

---

## 5. Luồng xử lý chi tiết (Upload Flow)
1.  **Map Check (GPS):** <= 2km thì cộng điểm. > 10km cảnh báo UI.
2.  **AI Kiểm duyệt (Gemini):** UNSAFE chặn, WARNING trừ điểm, SAFE cộng điểm.
3.  **Cộng/Trừ điểm hoạt động** -> Cập nhật `reputationScore`.
4.  **Tính lại Level** -> Cập nhật User.
5.  **Lưu bài viết & Trả kết quả.**

---

## 6. Đánh giá Cộng đồng & Admin (Vote Sao & Report)
*   **Vote Sao:** 1-5 sao. Tính trung bình lưu vào Post.
*   **Report:** User có thể cắm cờ bài viết vi phạm.
*   **Auto-flag System:** Vote > 10 mà trung bình < 2.0 -> Tự động Report gửi Admin.

---
## 7. Các bước thực hiện (Roadmap)
- [ ] **Step 1:** Cập nhật Entity User & Post, tạo Entity PostRating & Report.
- [ ] **Step 2:** Cập nhật API Đăng bài (Tích hợp điểm, Level, Check GPS, AI Moderation).
- [ ] **Step 3:** Triển khai API Vote Sao & API Report bài viết.
- [ ] **Step 4:** Phát triển Admin Panel xử lý Report.
- [ ] **Step 5:** Cập nhật UI Frontend (Level badge, Vote Sao, Warning Popup).
