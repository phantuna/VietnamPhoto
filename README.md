# 🇻🇳 Vietnam Photo Scout — Backend

> Server API cung cấp dữ liệu và logic nghiệp vụ cho nền tảng mạng xã hội dành cho cộng đồng nhiếp ảnh Việt Nam.

---

## 📋 Mục lục

- [Giới thiệu](#giới-thiệu)
- [Công nghệ sử dụng](#công-nghệ-sử-dụng)
- [Cấu trúc thư mục](#cấu-trúc-thư-mục)
- [Thống kê module](#thống-kê-module)
- [Cài đặt & Chạy local](#cài-đặt--chạy-local)
- [Biến môi trường](#biến-môi-trường)
- [Tính năng chính](#tính-năng-chính)
- [Thông tin đồ án](#thông-tin-đồ-án)

---

## 🌟 Giới thiệu

**Vietnam Photo Scout (VNScout) Backend** là hệ thống máy chủ RESTful API cung cấp các dịch vụ cốt lõi:
- 📍 Quản lý dữ liệu **địa điểm chụp ảnh** (Spot & Service) và tích hợp bản đồ.
- 🖼️ Xử lý, lưu trữ ảnh và tự động **trích xuất EXIF metadata**.
- 💬 Hỗ trợ **nhắn tin thời gian thực** và **thông báo** qua WebSocket/STOMP.
- 🛡️ Phân quyền bảo mật, quản trị hệ thống (Kiểm duyệt, Báo cáo vi phạm).
- ✉️ Tích hợp gửi email xác thực và các tiện ích AI (Gemini).

---

## 🛠️ Công nghệ sử dụng

| Thành phần | Công nghệ | Phiên bản |
|---|---|---|
| Framework | Spring Boot | 3.x / 4.x |
| Ngôn ngữ | Java | 17 |
| Database | MySQL | 8.0+ |
| ORM | Spring Data JPA + QueryDSL | 5.1.0 |
| Database Migration | Flyway | — |
| Security | Spring Security + Nimbus JOSE JWT | 9.38 |
| Realtime | WebSocket + STOMP | — |
| Rate Limiting | Bucket4j | 8.10.1 |
| Image Processing | Cloudinary + Metadata Extractor | 1.37.0 / 2.19.0 |

---

## 📁 Cấu trúc thư mục

```
VietnamPhoto/
├── backend/
│   ├── src/main/java/com/example/backend/
│   │   ├── config/             # Cấu hình (Security, WebSocket, Cloudinary...)
│   │   ├── controller/         # RESTful API Controllers
│   │   ├── dto/                # Data Transfer Objects
│   │   ├── entity/             # JPA Entities
│   │   ├── enums/              # Các hằng số (Enum)
│   │   ├── event/              # Event-driven listeners (Gửi email, tính điểm...)
│   │   ├── exception/          # Global Exception Handler
│   │   ├── init/               # Khởi tạo dữ liệu mẫu (Seeder)
│   │   ├── mapper/             # Chuyển đổi giữa Entity và DTO
│   │   ├── repository/         # Spring Data JPA & QueryDSL Repositories
│   │   ├── service/            # Lớp chứa Business Logic
│   │   └── utils/              # Các hàm tiện ích (Helper)
│   ├── src/main/resources/
│   │   ├── db/migration/       # Các script SQL Flyway (V1, V2...)
│   │   └── application.yml     # Cấu hình ứng dụng chính
│   └── pom.xml                 # Cấu hình Maven dependencies
├── .env                        # File biến môi trường bảo mật
├── docker-compose.yml          # Cấu hình Docker (cho DB, Redis...)
└── VNSCOUT_TONG_HOP_NGHIEP_VU.md # Tài liệu tổng hợp nghiệp vụ
```

---

## 📊 Thống kê module chính

### ⚙️ Core Modules (Các phân hệ)

| Module | Chức năng |
|---|---|
| `Authentication` | Xử lý Login, Signup, Refresh Token, phân quyền JWT. |
| `User` | Quản lý thông tin hồ sơ, thống kê cá nhân, tính điểm uy tín (Reputation). |
| `Post & Comment` | Đăng bài, tải ảnh, like, comment, bookmark bài viết. |
| `Location` | Quản lý Spot, Service, tra cứu dữ liệu không gian. |
| `Chat` | Quản lý hội thoại, tin nhắn realtime (WebSocket). |
| `Notification` | Đẩy thông báo hệ thống realtime đến người dùng. |
| `Admin` | Quản lý user, duyệt bài đăng, xử lý báo cáo vi phạm. |

---

## 🚀 Cài đặt & Chạy local

### Yêu cầu

- **Java 17** (JDK 17)
- **Maven** 3.8+
- **MySQL** 8.0+

### Các bước cài đặt

```bash
# 1. Clone repo
git clone https://github.com/phantuna/VietnamPhoto.git
cd VietnamPhoto

# 2. Cấu hình cơ sở dữ liệu
# Tạo một database mới tên là 'vn_photo' trong MySQL của bạn.

# 3. Tạo file biến môi trường
# Tạo file .env ở thư mục gốc của dự án, điền các thông tin cần thiết.

# 4. Chạy ứng dụng
cd backend
./mvnw spring-boot:run
```

API Server sẽ chạy tại: **http://localhost:8080**

---

## 🔐 Biến môi trường

Tạo file `.env` ở thư mục gốc `VietnamPhoto/` (lưu ý không commit file này lên Git):

```env
# Cloudinary (Quản lý hình ảnh)
CLOUD_NAME=your_cloudinary_name
CLOUD_API_KEY=your_cloudinary_key
CLOUD_API_SECRET=your_cloudinary_secret

# VietMap & AI
VIETMAP_API_KEY=your_vietmap_api_key
GEMINI_API_KEY=your_gemini_api_key

# Email Server (Gửi thông báo, OTP)
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_app_password
```

---

## ✨ Tính năng chính

- 🔐 **Bảo mật & Phân quyền** — Xác thực stateless với JWT, mã hóa mật khẩu, phân quyền Role (User, Admin).
- ☁️ **Xử lý Ảnh Nâng cao** — Tự động upload ảnh lên Cloudinary, tự động trích xuất thông tin ảnh (EXIF: ngày chụp, thiết bị, ISO...).
- 💬 **Real-time Giao tiếp** — Hỗ trợ nhắn tin 1-1, chat nhóm và thông báo tức thì (WebSocket/STOMP).
- 🛡️ **Kiểm duyệt & Đánh giá** — Hệ thống tính điểm uy tín người dùng, quản lý báo cáo và chặn từ khóa nhạy cảm.
- 🚀 **Tối ưu Hiệu suất** — Chống tấn công Spam/DDoS nhờ Rate Limiting (Bucket4j), truy vấn động mạnh mẽ qua QueryDSL.
- 📦 **Tự động Database** — Quản lý phiên bản CSDL và rollback tự động với Flyway.

---

## 🎓 Thông tin đồ án

- **Tên đề tài:** Vietnam Photo Scout — Nền tảng mạng xã hội hỗ trợ cộng đồng nhiếp ảnh Việt Nam
- **Loại:** Đồ án tốt nghiệp (ĐATN)
- **Repo Frontend:** [FE_VNScout](https://github.com/phantuna/FE_VNScout)
