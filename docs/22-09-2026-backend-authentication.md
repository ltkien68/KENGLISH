# Kenglish Development Notes - 22/09/2026

## 1. Mục tiêu của buổi học

Trong buổi này, Kenglish bắt đầu xây dựng Backend Authentication thay vì chỉ sử dụng dữ liệu giả trên Android.

Các chức năng đã hoàn thành:

- Kết nối PHP với MySQL.
- Đăng ký tài khoản.
- Mã hóa mật khẩu.
- Gửi mã OTP qua Gmail.
- Xác thực email bằng OTP.
- OTP có thời gian hết hạn.
- Gửi lại OTP.
- Đăng nhập.
- Tạo authentication token.
- Lưu phiên đăng nhập.
- Kiểm tra token.
- Đăng xuất bằng cách hủy phiên.
- Tách thông tin Gmail/App Password khỏi source code.
- Đưa backend vào cùng repository Kenglish.
- Dùng Windows Junction để XAMPP chạy trực tiếp backend trong repository.

---

# 2. Kiến trúc Backend

Kenglish hiện sử dụng kiến trúc:

```text
Android App
    ↓
HTTP Request / JSON
    ↓
PHP REST API
    ↓
MySQL
```

Android không kết nối trực tiếp tới MySQL.

Ví dụ sau này Android đăng nhập:

```text
Android
   ↓
POST /auth/dang_nhap.php
   ↓
PHP kiểm tra tài khoản
   ↓
MySQL
   ↓
PHP trả JSON
   ↓
Android
```

Lý do:

- Không để thông tin MySQL trong ứng dụng Android.
- Backend chịu trách nhiệm xác thực và xử lý dữ liệu.
- Android chỉ giao tiếp với backend thông qua API.
- Sau này có thể thay Android bằng Web/iOS mà vẫn sử dụng cùng backend.

---

# 3. Database

Database:

```text
kenglish
```

## Bảng `nguoi_dung`

Dùng để lưu tài khoản người dùng.

Các dữ liệu quan trọng:

```text
id
ten_hien_thi
email
mat_khau
email_da_xac_thuc
ma_xac_thuc
het_han_ma_xac_thuc
ngay_tao
```

### Không lưu mật khẩu gốc

Mật khẩu được hash bằng:

```php
$matKhauHash = password_hash(
    $matKhau,
    PASSWORD_DEFAULT
);
```

Ví dụ:

```text
User nhập:
123456

Database:
$2y$10$...
```

Không thể lấy lại mật khẩu gốc từ hash.

Khi đăng nhập sử dụng:

```php
password_verify(
    $matKhau,
    $nguoiDung["mat_khau"]
);
```

Không hash mật khẩu mới rồi so sánh hai chuỗi hash.

---

# 4. Nhận JSON trong PHP

Android/Postman gửi:

```json
{
    "email": "example@gmail.com",
    "mat_khau": "123456"
}
```

PHP đọc raw request:

```php
file_get_contents("php://input")
```

Sau đó chuyển JSON thành PHP Array:

```php
$duLieu = json_decode(
    file_get_contents("php://input"),
    true
);
```

Có thể lấy dữ liệu:

```php
$email = $duLieu["email"];
```

Ghi nhớ:

```text
json_decode()
JSON → PHP

json_encode()
PHP → JSON
```

---

# 5. Prepared Statement

Không viết trực tiếp:

```php
$sql = "SELECT * FROM nguoi_dung WHERE email = '$email'";
```

Sử dụng:

```php
$cauLenh = $ketNoi->prepare(
    "SELECT *
     FROM nguoi_dung
     WHERE email = ?"
);

$cauLenh->bind_param(
    "s",
    $email
);
```

Mục đích chính:

- Tránh SQL Injection.
- Tách SQL khỏi dữ liệu người dùng.

Các kiểu thường dùng trong `bind_param`:

```text
s = string
i = integer
d = double
b = blob
```

Ví dụ:

```php
$cauLenh->bind_param(
    "iss",
    $idNguoiDung,
    $tokenHash,
    $hetHan
);
```

---

# 6. Luồng đăng ký

API:

```text
POST /auth/dang_ky.php
```

Luồng xử lý:

```text
Nhận JSON
   ↓
Kiểm tra dữ liệu
   ↓
Kiểm tra email hợp lệ
   ↓
Kiểm tra password
   ↓
Kiểm tra email đã tồn tại chưa
   ↓
Hash password
   ↓
Sinh OTP
   ↓
Tạo thời gian hết hạn OTP
   ↓
INSERT user vào MySQL
   ↓
Gửi OTP qua Gmail
```

OTP được tạo bằng:

```php
$maXacThuc = random_int(
    100000,
    999999
);
```

OTP có hiệu lực 5 phút:

```php
$hetHanMaXacThuc = date(
    "Y-m-d H:i:s",
    time() + 300
);
```

---

# 7. Gửi OTP bằng Gmail

Kenglish sử dụng:

```text
PHPMailer
+
Gmail SMTP
```

Cài PHPMailer bằng Composer.

Package được khai báo trong:

```text
composer.json
composer.lock
```

Thư mục:

```text
vendor/
```

được Composer tạo và không cần đưa lên Git.

SMTP:

```text
smtp.gmail.com
Port 587
STARTTLS
```

Kenglish không sử dụng mật khẩu Gmail thông thường.

Thay vào đó sử dụng:

```text
Google App Password
```

App Password cho phép backend xác thực với Gmail SMTP để gửi email.

---

# 8. Xác thực email

API:

```text
POST /auth/xac_thuc_email.php
```

Client gửi:

```json
{
    "email": "example@gmail.com",
    "ma_xac_thuc": "123456"
}
```

Backend thực hiện:

```text
Tìm tài khoản
   ↓
Kiểm tra đã xác thực chưa
   ↓
So sánh OTP
   ↓
Kiểm tra OTP hết hạn chưa
   ↓
email_da_xac_thuc = 1
   ↓
Xóa OTP
   ↓
Xóa thời gian hết hạn
```

Sau khi thành công:

```text
email_da_xac_thuc = 1
ma_xac_thuc = NULL
het_han_ma_xac_thuc = NULL
```

---

# 9. Gửi lại OTP

API:

```text
POST /auth/gui_lai_ma_xac_thuc.php
```

Luồng:

```text
Nhận email
   ↓
Tìm account
   ↓
Kiểm tra account chưa verify
   ↓
Sinh OTP mới
   ↓
Tạo expiry mới
   ↓
UPDATE database
   ↓
Gửi email mới
```

OTP cũ sẽ không còn được sử dụng.

---

# 10. Đăng nhập

API:

```text
POST /auth/dang_nhap.php
```

Client gửi:

```json
{
    "email": "example@gmail.com",
    "mat_khau": "123456"
}
```

Luồng:

```text
Tìm email
   ↓
password_verify()
   ↓
Kiểm tra email đã verify
   ↓
Tạo authentication token
   ↓
Hash token
   ↓
Lưu session vào MySQL
   ↓
Trả raw token cho client
```

---

# 11. Authentication Token

Token được tạo bằng:

```php
$token = bin2hex(
    random_bytes(32)
);
```

Sau đó hash:

```php
$tokenHash = hash(
    "sha256",
    $token
);
```

Điểm quan trọng:

```text
Android
    giữ RAW TOKEN

Database
    giữ TOKEN HASH
```

Ví dụ:

```text
Android:
a7c8f1...

              ↓ SHA-256

Database:
934b8e...
```

Không cần lưu raw token trong database.

Nếu database bị lộ, attacker không lấy ngay được các token đang hoạt động.

---

# 12. Bảng phiên đăng nhập

Bảng:

```text
phien_dang_nhap
```

Chứa:

```text
id
nguoi_dung_id
token_hash
het_han
ngay_tao
```

Mỗi lần đăng nhập thành công có thể tạo một phiên.

Token hiện có hiệu lực:

```text
30 ngày
```

Một người có thể có nhiều phiên nếu đăng nhập trên nhiều thiết bị.

Ví dụ:

```text
User 15
├── Laptop
├── Phone
└── Tablet
```

→ có thể có 3 session.

Session hết hạn có thể xóa:

```sql
DELETE FROM phien_dang_nhap
WHERE het_han < NOW();
```

---

# 13. Bearer Token

Các API cần đăng nhập sẽ nhận:

```http
Authorization: Bearer TOKEN
```

Ví dụ:

```text
Authorization: Bearer abc123...
```

Backend:

```text
Nhận token
   ↓
SHA-256
   ↓
Tìm token_hash trong database
   ↓
Kiểm tra chưa hết hạn
   ↓
Xác định nguoi_dung_id
```

File dùng chung:

```text
auth/kiem_tra_token.php
```

sẽ giúp các API sau này xác định:

> Request này thuộc về người dùng nào?

Ví dụ sau này:

```text
POST /bo-tu/tao_bo_tu.php
Authorization: Bearer ...
```

Backend có thể biết bộ từ phải thuộc user nào mà không cần client tự gửi `nguoi_dung_id`.

---

# 14. Logout

API:

```text
POST /auth/dang_xuat.php
```

Logout không chỉ đơn giản là:

```text
Android xóa isLoggedIn
```

Backend phải thu hồi session.

Luồng:

```text
Authorization: Bearer TOKEN
          ↓
Lấy raw token
          ↓
SHA-256
          ↓
DELETE token_hash khỏi phien_dang_nhap
```

Sau khi DELETE:

```text
Token cũ
   ↓
không còn trong database
   ↓
không thể xác thực request
```

Đây chính là revoke session.

Đã kiểm tra bằng Postman:

```text
Login
→ session xuất hiện

Logout
→ session biến mất
```

---

# 15. Bảo vệ Gmail App Password

Ban đầu Gmail/App Password nằm trực tiếp trong:

```text
gui_email.php
```

Điều này nguy hiểm nếu push lên GitHub.

Đã tách thành:

```text
config/
├── gui_email.php
└── email_secret.php
```

`gui_email.php` chứa logic:

```php
$mail->Username = EMAIL_GUI;
$mail->Password = EMAIL_APP_PASSWORD;
```

`email_secret.php` chứa dữ liệu thật:

```php
define(
    "EMAIL_GUI",
    "..."
);

define(
    "EMAIL_APP_PASSWORD",
    "..."
);
```

`email_secret.php` KHÔNG được commit.

`.gitignore`:

```gitignore
/backend/config/email_secret.php
/backend/vendor/
/backend/composer.phar

.env
.env.*
```

Nguyên tắc:

```text
Logic/code          → GitHub
Secret/password     → không GitHub
```

---

# 16. Composer và Git

Đưa lên Git:

```text
composer.json       YES
composer.lock       YES
```

Không đưa:

```text
vendor/             NO
composer.phar       NO
```

Sau khi clone project, dependencies có thể được cài lại bằng Composer.

Do đó không cần lưu toàn bộ thư viện PHPMailer trong repository.

---

# 17. Backend và repository Kenglish

Ban đầu backend nằm ở:

```text
C:\xampp\htdocs\kenglish_api
```

Trong khi Android nằm ở repository Kenglish.

Nếu giữ hai nơi sẽ dễ xảy ra:

```text
Sửa backend trong XAMPP
↓
quên copy
↓
Git chứa phiên bản cũ
```

Vì vậy backend đã được chuyển vào:

```text
Kenglish/
└── backend/
```

---

# 18. Windows Junction

Apache/XAMPP mặc định phục vụ file trong:

```text
C:\xampp\htdocs
```

Nhưng source thật hiện nằm trong repository.

Giải pháp sử dụng Windows Junction:

```cmd
mklink /J "C:\xampp\htdocs\kenglish_api" "D:\master\PERSONAL\Study\LTDD\Kenglish\backend"
```

Kết quả:

```text
C:\xampp\htdocs\kenglish_api
             │
             │ Junction
             ↓
Kenglish\backend
```

Chỉ có **một source backend thật**.

Do đó:

```text
Sửa Kenglish/backend
        ↓
Git thấy thay đổi

đồng thời

XAMPP cũng chạy thay đổi đó
```

Không cần copy file qua lại.

---

# 19. Kiểm thử bằng Postman

Backend đã được test bằng Postman.

Ví dụ login:

```text
POST
http://localhost/kenglish_api/auth/dang_nhap.php
```

Body:

```json
{
    "email": "example@gmail.com",
    "mat_khau": "123456"
}
```

Lưu ý:

API nhận JSON phải sử dụng đúng HTTP method.

Ví dụ login dùng:

```text
POST
```

không phải:

```text
GET
```

JSON cũng phải đúng cú pháp.

Sai:

```json
{
    "email": "example@gmail.com,
    "mat_khau": "123456"
}
```

Đúng:

```json
{
    "email": "example@gmail.com",
    "mat_khau": "123456"
}
```

---

# 20. Auth flow hiện tại của Kenglish

Toàn bộ hệ thống hiện tại:

```text
              ┌──────────────┐
              │   Register   │
              └──────┬───────┘
                     ↓
              Hash Password
                     ↓
                 Save User
                     ↓
                Generate OTP
                     ↓
               Gmail SMTP
                     ↓
              Verify Email
                     ↓
                  Login
                     ↓
             password_verify
                     ↓
              Generate Token
                     ↓
               Hash Token
                     ↓
              Save Session
                     ↓
          ┌────────────────────┐
          │ Authenticated User │
          └─────────┬──────────┘
                    ↓
              Protected APIs
                    ↓
                  Logout
                    ↓
              Delete Session
```

---

# 21. Những thứ chưa làm

Authentication hiện tại đủ dùng cho phiên bản đầu của đồ án.

Các phần có thể cải thiện sau:

- Rate limit đăng nhập.
- Giới hạn số lần nhập OTP.
- Cooldown gửi lại OTP.
- Hash OTP trong database.
- Forgot Password.
- Reset Password.
- Xóa session hết hạn tự động.
- HTTP status code chuẩn hơn.
- HTTPS khi deploy.
- Quản lý nhiều thiết bị.
- Chuyển secret sang `.env` nếu backend lớn hơn.

Không cần ưu tiên các phần này ngay.

---

# 22. Bước tiếp theo

Buổi tiếp theo:

## Android ↔ PHP API bằng Retrofit

Mục tiêu:

```text
Android Registration UI
        ↓
Retrofit
        ↓
dang_ky.php
        ↓
OTP Gmail
        ↓
Android OTP Screen
        ↓
xac_thuc_email.php
        ↓
Login
        ↓
dang_nhap.php
        ↓
Receive Token
        ↓
Save Token locally
```

Sau khi authentication được nối với Android, bắt đầu xây dựng dữ liệu thật cho:

```text
Folder
   ↓
Bo tu
   ↓
Tu vung
```

---

# 23. Kiến thức cần nhớ sau buổi này

Nếu sau này quên hết, chỉ cần nhớ các ý sau:

1. **Android không kết nối trực tiếp MySQL.**
2. Android gửi HTTP/JSON tới PHP API.
3. PHP dùng prepared statement để truy vấn MySQL.
4. Password phải `password_hash()`, kiểm tra bằng `password_verify()`.
5. OTP dùng để xác minh quyền sở hữu email.
6. OTP phải có thời gian hết hạn.
7. Login thành công tạo token.
8. Client giữ raw token, database giữ token hash.
9. Request cần đăng nhập gửi `Authorization: Bearer TOKEN`.
10. Logout phải revoke/delete session phía server.
11. Secret như Gmail App Password không bao giờ commit lên Git.
12. `composer.json` + `composer.lock` commit; `vendor/` không cần commit.
13. Junction giúp XAMPP chạy trực tiếp backend nằm trong repository.

---

## Commit của buổi học

```text
feat: add authentication backend with email verification
```

**Trạng thái cuối buổi:** Authentication Backend v1 hoạt động end-to-end và đã được kiểm thử bằng Postman.