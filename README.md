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
