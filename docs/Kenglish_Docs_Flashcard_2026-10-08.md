# Kenglish — Nhật ký phát triển: Luyện tập & Flashcard

**Ngày:** 08/10/2026  
**Phạm vi:** Android Java/XML, Node.js/Express, MySQL. **Dừng ở Flashcard; không tính Trắc nghiệm là chức năng hoàn thành.**  
**Mục đích:** Lưu kiến thức kỹ thuật, luồng xử lý, lỗi đã gặp, hướng khắc phục và nội dung có thể tái sử dụng cho báo cáo đồ án.

## 1. Tổng quan chức năng

- Màn hình **Luyện tập** có bộ lọc: bộ từ (`bo_tu_id`), trạng thái (`all`/`learned`/`unlearned`), thứ tự (`random`/`default`) và số lượng tối đa.
- Android gọi `GET /practice/words` để lấy **danh sách từ sau khi áp dụng bộ lọc**, rồi mới kiểm tra đủ điều kiện mở game.
- **Flashcard** hỗ trợ mặt trước/mặt sau, hiệu ứng lật, điều hướng thẻ, đánh giá **Đã thuộc (1)** / **Chưa thuộc (0)**.
- Kết quả đánh giá được giữ **tạm trong phiên học**; quay lại thẻ cũ vẫn giữ lựa chọn và có thể đổi lại.
- Màn hình `KetQuaLuyenTap` được thiết kế để dùng chung cho nhiều game: thống kê số từ, phần trăm, danh sách đã/chưa thuộc và nút **LƯU TIẾN TRÌNH**.
- Chỉ khi người dùng chủ động lưu mới gọi `POST /practice/save-progress` để cập nhật DB, ghi nhận ngày hoạt động và lượt chơi.

> **Trạng thái kiểm chứng:** API lưu tiến trình đã được người dùng xác nhận chạy được sau khi sửa SQL. Một số phần tích hợp UI, luồng back stack và tăng lượt chơi cần kiểm thử đầu-cuối trên thiết bị. Không ghi Trắc nghiệm là đã hoàn thành.

## 2. Công nghệ và vai trò

| Công nghệ | Ứng dụng trong buổi làm việc |
|---|---|
| Android **Java + XML** | Fragment game, màn hình kết quả, điều khiển UI, lật thẻ |
| **FragmentManager / FragmentTransaction** | Mở Flashcard và kết quả, `hide`/`add`, `addToBackStack`, `popBackStack` |
| **Bundle + Serializable** | Chuyển danh sách `TuVung` và `KetQuaTuVung` giữa Fragment |
| **LinkedHashMap<Integer, KetQuaTuVung>** | Ghi nhớ trạng thái từng từ theo ID, ghi đè lựa chọn và giữ thứ tự chèn |
| **Retrofit + Gson** | Gọi API có JWT Bearer, ánh xạ request/response JSON |
| **SharedPreferences** | Lấy token đăng nhập từ `Kenglish` / `token` |
| **Node.js + Express** | Xác thực, kiểm tra đầu vào và xử lý lưu kết quả |
| **MySQL + transaction** | Cập nhật nhiều từ, hoạt động và lượt chơi như một đơn vị giao dịch |
| **SQL JOIN / khóa ngoại logic** | Xác minh từ thuộc bộ từ của chính người dùng |
| **UNIQUE + UPSERT** | Hạn chế ghi trùng ngày hoạt động nếu đã có ràng buộc duy nhất |
| **Git / tài liệu Markdown** | Ghi lại tiến độ và quyết định thiết kế phục vụ báo cáo |

## 3. Kiến trúc và luồng dữ liệu

```text
LuyenTap (bộ lọc)
  └─ GET /practice/words
       ├─ Không đủ số từ → Toast, không mở game
       └─ Đủ từ → Flashcard.newInstance(List<TuVung>)
                       ├─ Lật thẻ / chuyển thẻ
                       ├─ Chọn Đã thuộc hoặc Chưa thuộc
                       └─ ketQuaPhienHoc: Map<ID, KetQuaTuVung>
                              └─ KetQuaLuyenTap
                                   ├─ Hiển thị thống kê (chưa ghi DB)
                                   └─ Nút LƯU TIẾN TRÌNH
                                        └─ POST /practice/save-progress
                                             ├─ Xác thực JWT
                                             ├─ Validate + kiểm tra sở hữu
                                             ├─ UPDATE tu_vung.da_thuoc
                                             ├─ INSERT ngay_hoat_dong
                                             ├─ UPDATE nguoi_dung.so_luot_choi
                                             ├─ Tính current_streak
                                             └─ COMMIT / ROLLBACK
```

### 3.1. Hợp đồng API

**Lấy từ:** `GET /practice/words` với các query `bo_tu_id`, `trang_thai`, `thu_tu`, `so_luong`.

**Lưu kết quả:** `POST /practice/save-progress` — yêu cầu `Authorization: Bearer <JWT>`.

```json
{
  "ket_qua": [
    { "tu_vung_id": 8, "da_thuoc": 1 },
    { "tu_vung_id": 9, "da_thuoc": 0 }
  ]
}
```

Response thành công dự kiến:

```json
{
  "thanh_cong": true,
  "thong_bao": "Đã lưu tiến trình luyện tập",
  "data": {
    "so_tu_da_luu": 2,
    "current_streak": 1,
    "so_luot_choi": 5
  }
}
```

Các giá trị trong ví dụ chỉ để minh họa. Android sử dụng `ApiResponse<LuuTienTrinhResponse>`; cần bảo đảm model response có đủ trường tương ứng.

## 4. Kỹ thuật xử lý nổi bật

### 4.1. Tách trạng thái phiên học khỏi trạng thái lưu trên server

`TuVung.da_thuoc` phản ánh dữ liệu từ API tại thời điểm tải. Không dùng nó làm kết quả vừa chọn. Thay vào đó:

```java
private final Map<Integer, KetQuaTuVung> ketQuaPhienHoc =
        new LinkedHashMap<>();

ketQuaPhienHoc.put(tu.getId(), new KetQuaTuVung(
        tu.getId(), tu.getTu_goc(), tu.getNghia_tieng_viet(), trangThai
));
```

- **Key:** ID từ vựng; một từ chỉ có một kết quả cuối cùng trong phiên.
- `put()` với cùng ID sẽ **ghi đè** lựa chọn trước đó.
- `get(tu.getId())` để phục hồi lựa chọn khi chuyển về thẻ cũ.
- Không cập nhật DB mỗi lần nhấn nút; chỉ lưu khi xác nhận kết quả.

### 4.2. Đồng bộ trạng thái với giao diện

Mỗi lần hiển thị thẻ, gọi hàm phục hồi trạng thái từ `ketQuaPhienHoc` và chỉnh `selected`/`alpha` của hai nút. Đồng thời đặt:

```java
layoutDanhGia.setVisibility(
        dangHienMatSau ? View.VISIBLE : View.GONE
);
```

**Bắt buộc đặt bên ngoài** nhánh `if (dangHienMatSau)` để nút đánh giá được ẩn khi quay về mặt trước.

### 4.3. Đánh giá lại trước khi lưu

Sau khi nhấn Đã thuộc/Chưa thuộc, giữ nguyên thẻ để người dùng có thể đổi lựa chọn. Chỉ cho hoàn thành khi đã đánh giá đủ số từ; khi thiếu, tìm thẻ chưa đánh giá. Điều này tránh nhầm **chưa đánh giá** với **chưa thuộc**.

### 4.4. Kiểm tra điều kiện bắt đầu game sau bộ lọc

Quy tắc nghiệp vụ:

| Game | Số từ tối thiểu |
|---|---:|
| Flashcard | 1 |
| Các game khác (khi triển khai) | 4 |

Kiểm tra `duLieu.getTuVung().size()` sau phản hồi `GET /practice/words`, không kiểm tra tổng số từ của bộ từ. Ví dụ bộ từ có 20 từ nhưng bộ lọc chỉ trả 3 từ thì game cần 4 từ không được bắt đầu.

**Lưu ý đối chiếu file:** Bản `LuyenTap.java` hiện có trong phiên làm việc đã chứa `laySoTuToiThieu()`; đồng thời điều kiện chọn game trong bản file này cho phép vị trí 0 **và 1** đi tiếp. Cần kiểm tra lại điều hướng Trắc nghiệm trước khi coi file là bản Flashcard-only ổn định. Việc kiểm tra số lượng không đồng nghĩa game Trắc nghiệm đã được triển khai.

### 4.5. Transaction để bảo toàn dữ liệu

Backend thực hiện các thay đổi trên **cùng một connection**:

1. `beginTransaction()`.
2. Xác minh danh sách từ thuộc người dùng qua `tu_vung` JOIN `bo_tu`.
3. Cập nhật `tu_vung.da_thuoc`.
4. Ghi nhận ngày vào `ngay_hoat_dong`.
5. Tăng `nguoi_dung.so_luot_choi`.
6. Tính streak và lấy lượt chơi.
7. `commit()`; lỗi thì `rollback()`.

Transaction giúp tránh trường hợp cập nhật trạng thái từ thành công nhưng lỗi ở bước ghi hoạt động/lượt chơi làm dữ liệu bị cập nhật một phần.

### 4.6. Xác thực và kiểm soát quyền sở hữu

Không tin `nguoi_dung_id` từ client; lấy ID từ JWT đã được middleware xác thực (`req.nguoiDung.id`). JOIN qua bảng bộ từ để xác minh chủ sở hữu, thay vì giả định bảng `tu_vung` có cột `nguoi_dung_id`.

### 4.7. Streak và lượt chơi

- `ngay_hoat_dong`: ghi ngày hiện tại; nên có `UNIQUE(nguoi_dung_id, ngay)` để mỗi ngày chỉ có một dòng.
- `current_streak`: tính bằng cách duyệt lùi từ ngày hiện tại trên tập các ngày hoạt động.
- `nguoi_dung.so_luot_choi`: tăng một đơn vị mỗi lần endpoint lưu tiến trình thành công, dùng cho bảng xếp hạng lượt chơi sau này.
- **Giới hạn:** Nếu cùng phiên gửi lại request thành công nhiều lần, lượt chơi vẫn tăng nhiều lần. Cần mã phiên duy nhất và bảng lịch sử phiên để chống cộng trùng trước khi mở xếp hạng chính thức.

## 5. Bug và cách xử lý

| Vấn đề | Nguyên nhân | Cách xử lý / bài học |
|---|---|---|
| API lưu kết quả trả HTTP 500: `ER_NO_SUCH_TABLE` | SQL dùng tên bảng giả định `vocabulary`, nhưng DB thực tế dùng `tu_vung` | Đối chiếu schema thực tế, sửa tên bảng đúng |
| HTTP 500: `ER_BAD_FIELD_ERROR` với `nguoi_dung_id` | `tu_vung` không có cột `nguoi_dung_id` | JOIN `tu_vung.bo_tu_id = bo_tu.id`, lọc `bo_tu.nguoi_dung_id` |
| Lật lại thẻ nhưng nút đánh giá vẫn hiện | `layoutDanhGia.setVisibility()` chỉ nằm trong nhánh mặt sau | Chuyển cập nhật visibility ra ngoài `if/else` |
| Quay lại thẻ cũ không thấy lựa chọn đã lưu | UI không đọc lại `ketQuaPhienHoc` khi đổi thẻ | Thêm `hienThiTrangThaiDanhGia()`, phục hồi selected/alpha theo ID |
| Chọn lại một từ không phản ánh lựa chọn cuối | Dễ nhầm dữ liệu gốc `TuVung.da_thuoc` với trạng thái phiên | Dùng `Map.put(ID, ketQua)` để ghi đè kết quả tạm |
| Thống kê cũ không khớp kết quả vừa đánh giá | Hàm `hienThiKetQua()` cũ đếm `tu.isDa_thuoc()` từ dữ liệu API | Ngừng dùng AlertDialog cũ, tổng hợp từ `ketQuaPhienHoc` |
| Có thể mở game với số từ không đủ | Chưa kiểm tra số từ sau lọc | Kiểm tra danh sách API trả về; Flashcard ≥1, game khác ≥4 |
| Nguy cơ cộng trùng lượt chơi | Endpoint cộng 1 mỗi lần lưu thành công, không có định danh phiên | Dự kiến thêm `ma_phien_choi` + UNIQUE/idempotency |

## 6. Kiểm thử cần thực hiện

- [ ] Flashcard với 1 từ; game khác với 3 và 4 từ sau bộ lọc.
- [ ] Chọn Đã thuộc → lật lại → qua thẻ khác → quay lại: lựa chọn vẫn được giữ.
- [ ] Đổi từ Đã thuộc sang Chưa thuộc và ngược lại: chỉ giữ kết quả cuối.
- [ ] Không thể hoàn thành khi vẫn có từ chưa đánh giá.
- [ ] Trước khi nhấn Lưu: dữ liệu MySQL chưa đổi.
- [ ] Sau khi lưu: `tu_vung.da_thuoc` cập nhật đúng các ID được gửi.
- [ ] `ngay_hoat_dong` không tạo dòng trùng ngày (kiểm tra UNIQUE thực tế).
- [ ] `so_luot_choi` tăng đúng 1 với một lần lưu thành công.
- [ ] Mất mạng / lỗi server: có thông báo, không mất kết quả tạm; thử lại được.
- [ ] Kiểm tra nút Back và thanh điều hướng dưới khi chuyển Flashcard ↔ Kết quả ↔ Luyện tập.
- [ ] Kiểm tra trường hợp request được server commit nhưng client mất response (nguy cơ gửi lại).

## 7. Hạng mục chưa hoàn thành / để buổi sau

1. **Trắc nghiệm:** chưa triển khai và chưa nghiệm thu. Thiết kế dự kiến 4 đáp án, đáp án sai lấy từ nghĩa của các từ khác; chỉ bắt đầu khi có tối thiểu 4 từ hợp lệ.
2. **Các game còn lại:** chưa triển khai; dùng lại `KetQuaLuyenTap` và API lưu kết quả khi hoàn thành.
3. **Chống gian lận lượt chơi:** bổ sung định danh phiên, bảng lịch sử và kiểm tra lưu trùng.
4. **Kiểm thử tích hợp trên máy thật:** đặc biệt vòng đời Fragment, back stack, lỗi mạng và refresh dữ liệu sau khi quay lại.
5. **Tối ưu request:** cân nhắc DTO chỉ gửi `tu_vung_id` và `da_thuoc`, không gửi thêm tên/nghĩa nếu không cần.

## 8. Nội dung có thể đưa vào báo cáo đồ án

**Chức năng:** Xây dựng mô-đun luyện tập từ vựng bằng Flashcard, cho phép người dùng học, đánh giá và lưu tiến trình cá nhân.

**Kỹ thuật nổi bật:**

- Thiết kế **trạng thái phiên học độc lập với dữ liệu bền vững**, tránh cập nhật cơ sở dữ liệu khi người dùng chưa xác nhận.
- Sử dụng **Map theo khóa ID** để quản lý kết quả từng từ và cho phép chỉnh sửa lựa chọn.
- Tổ chức **màn hình kết quả dùng chung**, giảm trùng lặp giữa các trò chơi.
- Áp dụng **JWT, kiểm tra quyền sở hữu và transaction** để bảo vệ tính nhất quán của dữ liệu.
- Thiết kế **đếm ngày hoạt động (streak) và tổng lượt chơi** làm cơ sở cho thống kê và xếp hạng.
- Kiểm tra **điều kiện nghiệp vụ sau lọc dữ liệu** trước khi bắt đầu trò chơi.

**Bài học kỹ thuật:** Không suy đoán cấu trúc bảng từ tên API; phải kiểm tra schema thật. Phân biệt trạng thái giao diện, trạng thái tạm của phiên và trạng thái đã lưu trong DB. Thiết kế endpoint ghi nhận lượt chơi cần tính đến việc gửi lại request và chống gian lận.

---

**Kết thúc buổi:** Dừng phát triển tại Flashcard. Tài liệu phản ánh các phần đã viết và thảo luận; các mục chưa kiểm thử được đánh dấu riêng, không coi là đã nghiệm thu.
