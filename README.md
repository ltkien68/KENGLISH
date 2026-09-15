# 📘 Kenglish

**Kenglish** là ứng dụng Android hỗ trợ học và ôn luyện từ vựng tiếng Anh theo hướng trực quan, cá nhân hóa và gamification.

Ứng dụng hướng đến việc giúp người học xây dựng vốn từ vựng theo từng mục tiêu như **TOEIC, IELTS, THPT, trình độ tiếng Anh, giao tiếp và từ vựng chuyên ngành**, đồng thời cung cấp các hình thức luyện tập, theo dõi tiến độ và trợ lý AI hỗ trợ học tập.

> 🚧 **Trạng thái dự án:** Đang phát triển — hiện tập trung hoàn thiện giao diện và luồng điều hướng.

---

## 🎯 Mục tiêu

Kenglish được xây dựng với mục tiêu tạo ra một ứng dụng học từ vựng tiếng Anh đơn giản, dễ sử dụng nhưng vẫn có đủ công cụ để người dùng duy trì việc học lâu dài.

Các mục tiêu chính:

- Học từ vựng theo từng bộ từ và lộ trình.
- Phân loại từ vựng theo mục tiêu học tập.
- Luyện tập và ôn lại từ đã học.
- Theo dõi tiến độ học tập.
- Tạo động lực bằng streak, xu và bảng xếp hạng.
- Hỗ trợ phát âm bằng Text-to-Speech.
- Tích hợp trợ lý AI hỗ trợ việc học tiếng Anh.
- Hỗ trợ tra cứu từ vựng Anh - Việt.

---

## 📱 Các màn hình chính

Kenglish sử dụng một `MainActivity` và `BottomNavigationView` để điều hướng giữa **6 Fragment chính**.

| Màn hình | Chức năng |
|---|---|
| 🏠 **Trang chủ** | Tổng quan quá trình học và các lộ trình từ vựng |
| 📚 **Bộ từ** | Quản lý và học các bộ từ vựng |
| 🎯 **Luyện tập** | Thực hiện các bài luyện tập và ôn từ |
| 🏆 **Xếp hạng** | Hiển thị thành tích và bảng xếp hạng |
| 🛒 **Cửa hàng** | Sử dụng xu và các phần thưởng trong ứng dụng |
| 👤 **Cá nhân** | Thông tin người dùng, thành tích và cài đặt |

---

## 🏠 Trang chủ

Trang chủ là màn hình đang được phát triển chi tiết nhất ở phiên bản hiện tại.

### Thông tin học tập

Trang chủ hiển thị các thông tin tổng quan như:

- Tổng số từ.
- Số từ đã thuộc.
- Phần trăm tiến độ.
- Số từ đến hạn ôn.
- Chuỗi ngày học — **streak**.
- Số xu hiện có.

### Lộ trình học

Người dùng có thể xem các bộ từ được chia thành nhiều nhóm:

- THPT.
- IELTS.
- TOEIC.
- Người nổi tiếng khuyên dùng.
- Theo level.
- Người đi làm & Chuyên ngành.
- THCS & Tiểu học.

Mỗi nhóm chứa các bộ từ tương ứng và có thể cuộn ngang để xem thêm.

Ví dụ:

- TOEIC Cơ bản.
- TOEIC 450+.
- TOEIC 650+.
- TOEIC 850+.

Mỗi bộ từ có thể hiển thị:

- Tên bộ từ.
- Số lượng từ.
- Tiến độ học.
- Thanh tiến độ.

---

## 🔎 Bộ lọc lộ trình

Trang chủ đã có logic lọc lộ trình trực tiếp trên giao diện.

Người dùng có thể chọn:

`Tất cả` · `THPT` · `IELTS` · `TOEIC` · `Người nổi tiếng` · `Theo level` · `Người đi làm & Chuyên ngành` · `THCS & Tiểu học`

Khi chọn một bộ lọc:

- Chỉ nhóm tương ứng được hiển thị.
- Các nhóm khác được ẩn.
- Nút đang được chọn đổi màu để dễ nhận biết.
- Chọn **Tất cả** sẽ hiển thị lại toàn bộ lộ trình.

Logic hiện tại được xử lý bằng `View.VISIBLE` và `View.GONE`.

---

## 🧩 Cấu trúc giao diện

Kenglish sử dụng **XML Views**, không sử dụng Jetpack Compose.

Các phần lớn của Trang chủ được tách thành những layout component riêng và ghép lại bằng `<include>`.

Ví dụ:

```text
trang_chu.xml
│
├── trangchu_thongtinnguoidung.xml
├── trangchu_banner.xml
├── trangchu_thongke.xml
├── trangchu_lotrinh.xml
├── trangchu_tieudedanhsach.xml
└── trangchu_dslotrinh.xml
        │
        ├── trangchu_nhom_thpt.xml
        ├── trangchu_nhom_ielts.xml
        ├── trangchu_nhom_toeic.xml
        ├── trangchu_nhom_nguoi_noi_tieng.xml
        ├── trangchu_nhom_level.xml
        ├── trangchu_nhom_chuyen_nganh.xml
        └── trangchu_nhom_thcs.xml
```

Việc tách layout giúp giao diện dễ quản lý và chỉnh sửa hơn khi số lượng thành phần trên Trang chủ tăng lên.

---

## 🧭 Điều hướng

Ứng dụng sử dụng:

- `MainActivity`
- `FrameLayout`
- `BottomNavigationView`
- `Fragment`

Cấu trúc điều hướng:

```text
MainActivity
│
├── TrangChu
├── BoTu
├── LuyenTap
├── XepHang
├── CuaHang
└── CaNhan
```

`MainActivity` chịu trách nhiệm chuyển đổi Fragment khi người dùng chọn các mục trên Bottom Navigation.

Mỗi tab có thể sử dụng màu nhận diện riêng khi được chọn, trong khi các tab chưa được chọn sử dụng màu trung tính.

---

## 🎨 Thiết kế giao diện

Kenglish sử dụng phong cách giao diện tối với màu nền chính:

```text
#182230
```

Một số đặc điểm thiết kế:

- Dark UI.
- Card bo góc.
- Material Components.
- Màu sắc thể hiện trạng thái.
- Giao diện compact.
- Thanh tiến độ.
- Các thành phần gamification.
- Bottom Navigation gồm 6 chức năng chính.
- Các danh sách lộ trình có thể cuộn ngang.

Ứng dụng hướng đến giao diện hiện đại, trực quan và tạo cảm giác giống một ứng dụng học tập có hệ thống tiến trình thay vì chỉ là một ứng dụng ghi nhớ từ vựng đơn giản.

---

## ⚙️ Công nghệ sử dụng

| Công nghệ | Mục đích |
|---|---|
| **Java** | Ngôn ngữ lập trình chính |
| **XML** | Xây dựng giao diện |
| **Android Studio** | IDE phát triển |
| **Android SDK** | Nền tảng Android |
| **Fragment** | Xây dựng các màn hình chính |
| **Material Components** | Button, Card và các thành phần UI |
| **BottomNavigationView** | Điều hướng giữa các màn hình |
| **Git / GitHub** | Quản lý source code |

---

## 🛠️ Các chức năng dự kiến

Các chức năng dưới đây nằm trong kế hoạch phát triển tiếp theo.

### 📚 Quản lý từ vựng

- Thêm từ mới.
- Nghĩa tiếng Việt.
- Phiên âm IPA.
- Tìm kiếm từ.
- Ghi chú.
- Phân loại theo bộ từ.
- Danh sách từ đã học.
- Danh sách từ cần ôn lại.

### 🎮 Luyện tập

Dự kiến hỗ trợ nhiều hình thức luyện tập:

- Trắc nghiệm.
- Chọn nghĩa đúng.
- Nối từ với nghĩa.
- Ôn lại từ đã học.
- Luyện theo từng bộ từ.

### 🔊 Phát âm

Sử dụng **Text-to-Speech (TTS)** để hỗ trợ nghe cách phát âm của từ tiếng Anh.

### 🔥 Gamification

Hệ thống tạo động lực học tập dự kiến gồm:

- Streak.
- Xu.
- Tiến độ.
- Thành tích.
- Bảng xếp hạng.
- Cửa hàng/phần thưởng.

### 🤖 AI Assistant

Kenglish dự kiến tích hợp **AI Assistant / AI Tutor** để hỗ trợ người học:

- Giải thích từ vựng.
- Đưa ra ví dụ.
- Hỗ trợ ngữ pháp.
- Giải thích đáp án.
- Hỗ trợ luyện tiếng Anh theo ngữ cảnh.

AI sẽ được gọi thông qua **backend/API**, không lưu trực tiếp API Key trong ứng dụng Android.

### 💾 Lưu trữ dữ liệu

Dự kiến sử dụng cơ sở dữ liệu local để lưu:

- Từ vựng.
- Bộ từ.
- Tiến độ học.
- Từ đã thuộc.
- Từ cần ôn.
- Ghi chú.
- Thông tin streak.

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

## 🚀 Chạy project

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

## 📖 Mục đích dự án

Kenglish được phát triển như một ứng dụng học tiếng Anh trên Android, đồng thời áp dụng các kiến thức về:

- Java Android.
- XML Layout.
- Activity và Fragment.
- Intent.
- ListView.
- RadioButton.
- SharedPreferences.
- Local Database.
- API.
- AI Assistant.

Dự án sẽ tiếp tục được hoàn thiện theo từng giai đoạn, bắt đầu từ **UI → dữ liệu → chức năng học tập → gamification → AI**.

---

## 👨‍💻 Tác giả

**Lê Trung Kiên**

Project: **Kenglish — English Vocabulary Learning App**

---

⭐ *Kenglish đang trong quá trình phát triển. Các chức năng và giao diện có thể tiếp tục được thay đổi và hoàn thiện.*
