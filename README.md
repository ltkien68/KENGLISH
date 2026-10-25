# 📘 Kenglish

**Kenglish** là ứng dụng Android hỗ trợ học và ôn luyện từ vựng tiếng Anh theo hướng trực quan, cá nhân hóa và gamification.

---

## 📱 Các màn hình chính

Kenglish sử dụng một `MainActivity` và `BottomNavigationView` để điều hướng giữa **6 Fragment chính**.

| Màn hình | Chức năng |
|---|---|
| 🏠 **Trang chủ** | Tổng quan quá trình học và các lộ trình từ vựng |
| 📚 **Bộ từ** | Quản lý bộ từ vựng của bạn |
| 🎯 **Luyện tập** | Thực hiện các bài luyện tập và ôn tập với bộ từ của bạn |
| 🏆 **Xếp hạng** | Hiển thị thành tích và bảng xếp hạng |
| 🛒 **Cửa hàng** | Sử dụng xu và các phần thưởng trong ứng dụng |
| 👤 **Cá nhân** | Thông tin người dùng, thành tích và cài đặt |

---

## 🎯 Tính năng chính

- Học từ vựng theo từng bộ từ và lộ trình.
- Phân loại từ vựng theo mục tiêu học tập.
- Luyện tập và ôn lại từ đã học.
- Theo dõi tiến độ học tập.
- Tạo động lực bằng streak, xu và bảng xếp hạng.
- Hỗ trợ phát âm bằng Text-to-Speech.
- Hỗ trợ điền phát âm và nghĩa của từ tự động khi thêm từ mới.
- Tích hợp trợ lý AI hỗ trợ việc học tiếng Anh.

---

##  Cài đặt


---

## 📌 Trạng thái phát triển

### Đã thực hiện

- [x] Khởi tạo project Android bằng Java.
- [x] Xây dựng `MainActivity`.
- [x] Bottom Navigation 6 chức năng.
- [x] Chuyển đổi giữa các Fragment.
- [x] Xây dựng giao diện Trang chủ.
- [x] Tách giao diện Trang chủ thành các component XML.
- [x] Xây dựng card thống kê.
- [x] Xây dựng giao diện lộ trình học.
- [x] Xây dựng các nhóm lộ trình.
- [x] Bộ lọc lộ trình.
- [x] Thay đổi trạng thái/màu của nút lọc.
- [x] Thiết lập màu tab Bottom Navigation.

### Đang phát triển

- [ ] Hoàn thiện UI Bộ từ.
- [ ] Hoàn thiện UI Luyện tập.
- [ ] Hoàn thiện UI Xếp hạng.
- [ ] Hoàn thiện UI Cửa hàng.
- [ ] Hoàn thiện UI Cá nhân.

### Kế hoạch tiếp theo

- [ ] Xây dựng database.
- [ ] Quản lý từ và bộ từ.
- [ ] Lưu tiến độ học.
- [ ] Hệ thống ôn tập.
- [ ] Trắc nghiệm và nối từ.
- [ ] Tích hợp Text-to-Speech.
- [ ] Tìm kiếm từ vựng.
- [ ] Hệ thống streak và xu.
- [ ] Bảng xếp hạng.
- [ ] Tích hợp từ điển Anh - Việt.
- [ ] Xây dựng backend cho AI.
- [ ] Tích hợp AI Assistant.

---

## 📂 Cấu trúc project

```text
Kenglish/
│
├── app/
│   └── src/
│       └── main/
│           │
│           ├── java/com/example/kenglish/
│           │   ├── MainActivity.java
│           │   ├── TrangChu.java
│           │   ├── BoTu.java
│           │   ├── LuyenTap.java
│           │   ├── XepHang.java
│           │   ├── CuaHang.java
│           │   └── CaNhan.java
│           │
│           └── res/
│               ├── drawable/
│               ├── layout/
│               ├── menu/
│               ├── color/
│               └── values/
│
├── gradle/
├── build.gradle
├── settings.gradle
└── README.md
```

---

##  Chạy project

### Yêu cầu

- Android Studio.
- Android SDK.
- JDK tương thích với phiên bản Android Gradle Plugin của project.
- Máy ảo Android hoặc thiết bị Android thật.

### Các bước

1. Clone repository.
2. Mở project bằng Android Studio.
3. Chờ Gradle Sync hoàn tất.
4. Chọn Emulator hoặc thiết bị Android.
5. Nhấn **Run ▶** để chạy ứng dụng.

---

## Hướng dẫn cài đặt Kenglish trên Android

### Yêu cầu hệ thống

- **Hệ điều hành:** Android 7.0 trở lên.
- **Kết nối Internet:** Cần thiết để sử dụng các chức năng trực tuyến.
- **Bộ cài đặt:** File APK trong GitHub Releases.

### 1. Tải file APK

1. Truy cập trang [Kenglish Releases](../../releases/latest).
2. Tìm phần **Assets** trong bản phát hành mới nhất.
3. Nhấn vào file kenglish.apk để tải xuống điện thoại.

### 2. Cho phép cài đặt ứng dụng

Nếu điện thoại yêu cầu cấp quyền cài đặt ứng dụng từ nguồn bên ngoài Google Play:

1. Mở **Cài đặt (Settings)** trên điện thoại.
2. Tìm mục **Cài đặt ứng dụng không rõ nguồn gốc (Install unknown apps)**.
3. Chọn ứng dụng dùng để mở file APK, chẳng hạn Chrome hoặc Quản lý tệp.
4. Bật **Cho phép từ nguồn này (Allow from this source)**.

*Tên và vị trí các mục cài đặt có thể khác nhau tùy theo hãng điện thoại và phiên bản Android.*

### 3. Cài đặt Kenglish

1. Mở ứng dụng **Quản lý tệp (File Manager)**.
2. Truy cập thư mục **Downloads / Tải xuống**.
3. Nhấn vào file APK vừa tải.
4. Chọn **Cài đặt (Install)**.
5. Sau khi hoàn tất, nhấn **Mở (Open)** để khởi chạy Kenglish.

### 4. Đăng nhập và sử dụng

1. Mở ứng dụng Kenglish.
2. Đăng nhập bằng tài khoản đã đăng ký hoặc tạo tài khoản mới.
3. Đảm bảo điện thoại có kết nối Internet để sử dụng các chức năng cần máy chủ.

### ⚠️ Lưu ý

- Chỉ tải APK từ trang Releases chính thức của repository Kenglish.
- Nếu Android cảnh báo về file APK, hãy xác minh nguồn tải trước khi tiếp tục.
- Nếu không cài đặt được, hãy kiểm tra phiên bản Android, tính toàn vẹn của file APK và chữ ký ứng dụng.
- Nếu phiên bản Kenglish cũ được ký bằng khóa khác, bạn có thể cần gỡ phiên bản cũ trước khi cài bản mới. Việc này có thể làm mất dữ liệu lưu cục bộ.
- Các chức năng trực tuyến phụ thuộc vào tình trạng hoạt động của backend.

---

**Kenglish — Learn English smarter, one word at a time.**


## Công nghệ sử dụng

| Công nghệ | Mục đích |
|---|---|
| **Java** | Ngôn ngữ lập trình chính |
| **XML** | Xây dựng giao diện ứng dụng |
| **Android Studio** | Môi trường phát triển |
| **Android SDK** | Phát triển ứng dụng Android |
| **Fragment** | Xây dựng và quản lý các màn hình |
| **Material Components** | Xây dựng các thành phần giao diện |
| **BottomNavigationView** | Điều hướng giữa 6 màn hình chính |
| **Git & GitHub** | Quản lý phiên bản và mã nguồn |

### Công nghệ dự kiến sử dụng

| Công nghệ | Mục đích |
|---|---|
| **Room Database** | Lưu trữ từ vựng và dữ liệu học tập trên thiết bị |
| **SharedPreferences** | Lưu các thiết lập và trạng thái đơn giản |
| **Text-to-Speech** | Phát âm từ vựng tiếng Anh |
| **Dictionary API** | Tự động lấy nghĩa và phiên âm của từ |
| **AI API** | Xây dựng trợ lý AI hỗ trợ học tiếng Anh |

---


## Liên hệ

**Lê Trung Kiên**

Project: **Kenglish — English Vocabulary Learning App**

---

⭐ *Kenglish đang trong quá trình phát triển. Các chức năng và giao diện có thể tiếp tục được thay đổi và hoàn thiện.*
