# 🔐 KENGLISH Authentication

Hệ thống Authentication cho ứng dụng **Kenglish**, xây dựng bằng **Node.js + Express + MySQL + JWT**.

Authentication hiện hỗ trợ:

- Đăng ký tài khoản
- Hash mật khẩu bằng bcrypt
- Gửi OTP qua email
- Xác thực email
- Gửi lại OTP
- Đăng nhập
- JWT Authentication
- Middleware bảo vệ API
- Đăng xuất

---

# 1. Công nghệ sử dụng

| Công nghệ | Chức năng |
|---|---|
| Node.js | Runtime backend |
| Express.js | Xây dựng REST API |
| MySQL | Lưu trữ dữ liệu |
| mysql2 | Kết nối Node.js với MySQL |
| bcrypt | Hash và kiểm tra mật khẩu |
| jsonwebtoken | Tạo và xác thực JWT |
| Nodemailer | Gửi OTP qua email |
| dotenv | Quản lý biến môi trường |
| cors | Cấu hình Cross-Origin Resource Sharing |

Cài đặt dependency:

```bash
npm install express mysql2 bcrypt jsonwebtoken nodemailer dotenv cors
```

---

# 2. Cấu trúc Backend

```text
backend/
│
├── assets/
│   └── images/
│       └── logo.png
│
├── config/
│   └── database.js
│
├── controllers/
│   └── authControllers/
│       ├── dangKyController.js
│       ├── xacThucEmailController.js
│       ├── guiLaiMaController.js
│       ├── dangNhapController.js
│       └── dangXuatController.js
│
├── middleware/
│   └── xacThucToken.js
│
├── routes/
│   └── authRoutes.js
│
├── services/
│   └── emailService.js
│
├── .env
├── .gitignore
├── package.json
└── server.js
```

### Vai trò

```text
routes/
→ Định nghĩa endpoint API.

controllers/
→ Xử lý nghiệp vụ.

middleware/
→ Xử lý request trước khi tới controller.

services/
→ Các chức năng dùng chung như gửi email.

config/
→ Cấu hình database.

assets/
→ Tài nguyên backend như logo email.
```

Ví dụ request:

```text
POST /auth/dang-nhap
        ↓
authRoutes.js
        ↓
dangNhapController.js
        ↓
MySQL
```

---

# 3. Environment Variables

Các thông tin cấu hình và secret được lưu trong:

```text
.env
```

Ví dụ:

```env
DB_HOST=localhost
DB_USER=root
DB_PASSWORD=
DB_NAME=kenglish
DB_PORT=3306

PORT=3000

EMAIL_GUI=example@gmail.com
EMAIL_APP_PASSWORD=your_app_password

JWT_SECRET=your_random_secret
JWT_EXPIRES_IN=30d
```

Load `.env`:

```js
require("dotenv").config();
```

Sử dụng:

```js
process.env.JWT_SECRET
process.env.DB_HOST
process.env.EMAIL_GUI
```

## Không commit `.env`

`.gitignore`:

```gitignore
backend/node_modules/
backend/.env
```

Các thông tin như:

- Database password
- Gmail App Password
- JWT Secret

không được đưa lên GitHub.

---

# 4. Database Connection

File:

```text
config/database.js
```

```js
const mysql = require("mysql2/promise");

const ketNoi = mysql.createPool({
    host: process.env.DB_HOST,
    user: process.env.DB_USER,
    password: process.env.DB_PASSWORD,
    database: process.env.DB_NAME,
    port: process.env.DB_PORT,
    charset: "utf8mb4"
});

module.exports = ketNoi;
```

Query:

```js
const [ketQua] = await ketNoi.execute(
    "SELECT * FROM nguoi_dung WHERE email = ?",
    [email]
);
```

Sử dụng parameter:

```sql
?
```

thay vì nối trực tiếp dữ liệu người dùng vào SQL.

Điều này giúp hạn chế **SQL Injection**.

---

# 5. Authentication Flow

```text
ĐĂNG KÝ
   ↓
Validate dữ liệu
   ↓
Kiểm tra email tồn tại
   ↓
Hash mật khẩu bằng bcrypt
   ↓
Tạo OTP 6 số
   ↓
Lưu tài khoản vào MySQL
   ↓
Gửi OTP qua Nodemailer
   ↓
XÁC THỰC EMAIL
   ↓
Kiểm tra OTP + thời hạn
   ↓
email_da_xac_thuc = 1
   ↓
ĐĂNG NHẬP
   ↓
Kiểm tra email
   ↓
bcrypt.compare()
   ↓
Kiểm tra email đã xác thực
   ↓
jwt.sign()
   ↓
JWT
   ↓
Android lưu JWT
   ↓
Authorization: Bearer <JWT>
   ↓
Middleware
   ↓
jwt.verify()
   ↓
Protected API
```

---

# 6. Đăng ký

### Endpoint

```http
POST /auth/dang-ky
```

### Request

```json
{
    "ten_hien_thi": "Kien",
    "email": "example@gmail.com",
    "mat_khau": "123456"
}
```

Backend thực hiện:

```text
1. Nhận JSON
2. Chuẩn hóa dữ liệu
3. Kiểm tra trường rỗng
4. Validate email
5. Kiểm tra độ dài mật khẩu
6. Kiểm tra email tồn tại
7. Hash mật khẩu
8. Tạo OTP
9. Tạo thời gian hết hạn OTP
10. INSERT người dùng
11. Gửi OTP
```

---

# 7. Password Hashing

Kenglish không lưu mật khẩu gốc.

```text
123456
   ↓
bcrypt.hash()
   ↓
$2b$10$...
   ↓
MySQL
```

Hash password:

```js
const matKhauHash = await bcrypt.hash(
    matKhau,
    10
);
```

Database chỉ lưu hash.

### Kiểm tra password

Khi đăng nhập:

```js
const matKhauDung = await bcrypt.compare(
    matKhau,
    nguoiDung.mat_khau
);
```

Flow:

```text
Password người dùng nhập
          ↓
    bcrypt.compare()
          ↑
   Hash trong MySQL
          ↓
      true / false
```

---

# 8. OTP Verification

OTP được tạo bằng module `crypto`:

```js
const crypto = require("crypto");

const maXacThuc = crypto.randomInt(
    100000,
    1000000
);
```

Ví dụ:

```text
381592
```

OTP có hiệu lực trong 5 phút:

```js
const hetHanMaXacThuc =
    new Date(Date.now() + 5 * 60 * 1000);
```

Database lưu:

```text
ma_xac_thuc
het_han_ma_xac_thuc
email_da_xac_thuc
```

---

# 9. Gửi OTP qua Email

Kenglish sử dụng:

```text
Node.js
   ↓
Nodemailer
   ↓
Gmail SMTP
   ↓
Email người dùng
```

Cấu hình:

```js
const transporter = nodemailer.createTransport({
    host: "smtp.gmail.com",
    port: 587,
    secure: false,

    auth: {
        user: process.env.EMAIL_GUI,
        pass: process.env.EMAIL_APP_PASSWORD
    }
});
```

Gửi email:

```js
await transporter.sendMail({
    from: `"Kenglish" <${process.env.EMAIL_GUI}>`,
    to: email,
    subject: "Kenglish - Email Verification",
    html: `...`
});
```

Gmail App Password được lưu trong `.env`.

---

# 10. Xác thực Email

### Endpoint

```http
POST /auth/xac-thuc-email
```

### Request

```json
{
    "email": "example@gmail.com",
    "ma_xac_thuc": "381592"
}
```

Backend:

```text
Tìm tài khoản
      ↓
Tài khoản tồn tại?
      ↓
Email đã xác thực?
      ↓
OTP đúng?
      ↓
OTP còn hạn?
      ↓
     YES
      ↓
email_da_xac_thuc = 1
      ↓
ma_xac_thuc = NULL
het_han_ma_xac_thuc = NULL
```

---

# 11. Gửi lại OTP

### Endpoint

```http
POST /auth/gui-lai-ma-xac-thuc
```

### Request

```json
{
    "email": "example@gmail.com"
}
```

Backend:

```text
Tìm tài khoản
      ↓
Kiểm tra trạng thái xác thực
      ↓
Tạo OTP mới
      ↓
Tạo thời hạn mới
      ↓
UPDATE MySQL
      ↓
Gửi OTP mới
```

Ví dụ:

```text
OTP cũ: 123456
       ↓
Gửi lại OTP
       ↓
OTP mới: 847291
```

Sau khi tạo OTP mới:

```text
123456 → Không còn hợp lệ
847291 → Hợp lệ
```

---

# 12. Đăng nhập

### Endpoint

```http
POST /auth/dang-nhap
```

### Request

```json
{
    "email": "example@gmail.com",
    "mat_khau": "123456"
}
```

Backend:

```text
Tìm tài khoản
      ↓
bcrypt.compare()
      ↓
Password đúng?
      ↓
Email đã xác thực?
      ↓
jwt.sign()
      ↓
Trả JWT cho client
```

---

# 13. JSON Web Token

JWT là viết tắt của:

```text
JSON Web Token
```

Token có dạng:

```text
xxxxx.yyyyy.zzzzz
```

Gồm:

```text
HEADER.PAYLOAD.SIGNATURE
```

## Header

Ví dụ:

```json
{
    "alg": "HS256",
    "typ": "JWT"
}
```

## Payload

Kenglish hiện sử dụng:

```json
{
    "id": 5,
    "email": "example@gmail.com"
}
```

JWT còn chứa các trường thời gian như:

```text
iat
→ Issued At

exp
→ Expiration Time
```

## Signature

Backend sử dụng `JWT_SECRET` để ký token.

```text
Header
   +
Payload
   +
JWT_SECRET
   ↓
Signature
```

Signature giúp backend phát hiện token đã bị chỉnh sửa.

---

# 14. JWT_SECRET

`.env` chứa:

```env
JWT_SECRET=your_secret
```

`JWT_SECRET` không phải token của người dùng.

Nó là secret của server dùng cho:

```js
jwt.sign()
```

và:

```js
jwt.verify()
```

Flow:

```text
JWT_SECRET
    ↓
jwt.sign()
    ↓
JWT
    ↓
Client
    ↓
JWT
    ↓
jwt.verify()
    ↑
JWT_SECRET
```

`JWT_SECRET` không bao giờ được gửi cho Android.

---

# 15. JWT không phải Encryption

JWT payload có thể được decode.

Do đó không lưu trong payload:

```text
❌ Password
❌ OTP
❌ JWT_SECRET
❌ Email App Password
❌ Các secret khác
```

Có thể lưu các claim cần thiết như:

```text
✓ User ID
✓ Email
✓ Role
```

JWT được **ký (signed)** để bảo vệ tính toàn vẹn.

JWT không mặc định **mã hóa (encrypted)** payload.

---

# 16. Tạo JWT khi đăng nhập

Sau khi xác thực tài khoản thành công:

```js
const token = jwt.sign(
    {
        id: nguoiDung.id,
        email: nguoiDung.email
    },
    process.env.JWT_SECRET,
    {
        expiresIn:
            process.env.JWT_EXPIRES_IN || "30d"
    }
);
```

Flow:

```text
User ID + Email
       +
JWT_SECRET
       +
Expiration
       ↓
   jwt.sign()
       ↓
      JWT
```

JWT được backend tạo tại thời điểm đăng nhập.

JWT không được lấy từ database.

---

# 17. Login Response

Ví dụ:

```json
{
    "thanh_cong": true,
    "thong_bao": "Đăng nhập thành công",

    "token": "eyJhbGciOi...",

    "nguoi_dung": {
        "id": 5,
        "ten_hien_thi": "Kien",
        "email": "example@gmail.com"
    }
}
```

Client lưu:

```text
token
```

để sử dụng cho những request yêu cầu đăng nhập.

---

# 18. Bearer Authentication

Client gửi JWT trong HTTP Header:

```http
Authorization: Bearer <JWT>
```

Ví dụ:

```http
Authorization: Bearer eyJhbGciOiJIUzI1Ni...
```

Flow:

```text
Android
   ↓
Authorization Header
   ↓
Bearer JWT
   ↓
Express
   ↓
Authentication Middleware
```

Backend xác định người dùng dựa trên JWT đã được xác thực.

Không nên tin `nguoi_dung_id` do client tự gửi để xác định danh tính.

---

# 19. Authentication Middleware

File:

```text
middleware/xacThucToken.js
```

Middleware đứng giữa request và controller:

```text
Request
   ↓
xacThucToken
   ↓
JWT hợp lệ?
 ┌──────┴──────┐
 NO            YES
 ↓              ↓
401         req.nguoiDung
                ↓
             next()
                ↓
            Controller
```

Ví dụ:

```js
const jwt = require("jsonwebtoken");

function xacThucToken(req, res, next) {
    try {
        const authorization =
            req.headers.authorization;

        if (!authorization) {
            return res.status(401).json({
                thanh_cong: false,
                thong_bao: "Bạn chưa đăng nhập"
            });
        }

        const [loaiToken, token] =
            authorization.split(" ");

        if (loaiToken !== "Bearer" || !token) {
            return res.status(401).json({
                thanh_cong: false,
                thong_bao: "Token không hợp lệ"
            });
        }

        const duLieuToken = jwt.verify(
            token,
            process.env.JWT_SECRET
        );

        req.nguoiDung = duLieuToken;

        next();

    } catch (error) {
        return res.status(401).json({
            thanh_cong: false,
            thong_bao:
                "Token không hợp lệ hoặc đã hết hạn"
        });
    }
}

module.exports = xacThucToken;
```

---

# 20. `req.nguoiDung`

Sau:

```js
const duLieuToken = jwt.verify(
    token,
    process.env.JWT_SECRET
);
```

payload có thể là:

```js
{
    id: 5,
    email: "example@gmail.com",
    iat: 1790570000,
    exp: 1793162000
}
```

Middleware gắn nó vào request:

```js
req.nguoiDung = duLieuToken;
```

Controller phía sau có thể:

```js
const idNguoiDung = req.nguoiDung.id;
```

Nhờ đó controller biết người đang thực hiện request là ai.

---

# 21. Protected Route

Ví dụ API chỉ dành cho người đã đăng nhập:

```js
router.get(
    "/thong-tin-ca-nhan",
    xacThucToken,
    thongTinCaNhan
);
```

Express chạy:

```text
Request
   ↓
xacThucToken()
   ↓
JWT sai
   ↓
401 Unauthorized
```

hoặc:

```text
Request
   ↓
xacThucToken()
   ↓
JWT đúng
   ↓
next()
   ↓
thongTinCaNhan()
```

---

# 22. Đăng xuất

### Endpoint

```http
POST /auth/dang-xuat
```

Với JWT stateless hiện tại, backend không lưu từng access token trong database.

Do đó logout chủ yếu là:

```text
User bấm đăng xuất
        ↓
Android gọi API logout
        ↓
Backend xác thực JWT
        ↓
Response thành công
        ↓
Android xóa JWT đã lưu
        ↓
Quay về màn hình đăng nhập
```

Controller:

```js
function dangXuat(req, res) {
    return res.json({
        thanh_cong: true,
        thong_bao: "Đăng xuất thành công"
    });
}

module.exports = dangXuat;
```

Route được bảo vệ:

```js
router.post(
    "/dang-xuat",
    xacThucToken,
    dangXuat
);
```

---

# 23. Hạn chế của JWT Stateless hiện tại

Nếu JWT có thời hạn:

```env
JWT_EXPIRES_IN=30d
```

thì JWT đã phát hành có thể vẫn hợp lệ cho đến khi `exp` dù người dùng đã logout.

Android xóa JWT không làm JWT đó bị revoke trên server.

Ví dụ:

```text
28/09
Login
↓
JWT được tạo
↓
Hết hạn 28/10

29/09
Logout
↓
Android xóa JWT

JWT đã phát hành
↓
Vẫn hợp lệ tới 28/10 nếu có bản sao
```

Đây là giới hạn cần lưu ý của thiết kế JWT stateless hiện tại.

---

# 24. Nâng cấp Authentication sau này

Kiến trúc có thể nâng cấp thành:

```text
Access Token
JWT
~15 phút

+

Refresh Token
~30 ngày
```

Flow:

```text
Login
  ↓
Access Token
  +
Refresh Token
  ↓
Access Token dùng gọi API
  ↓
Access Token hết hạn
  ↓
Refresh Token
  ↓
Xin Access Token mới
```

Khi logout:

```text
Revoke Refresh Token phía server
          +
Xóa Access Token phía client
          +
Xóa Refresh Token phía client
```

Điều này giúp quản lý session tốt hơn JWT access token sống 30 ngày.

---

# 25. API Authentication hiện tại

| Method | Endpoint | Chức năng | Authentication |
|---|---|---|---|
| POST | `/auth/dang-ky` | Đăng ký | Không |
| POST | `/auth/xac-thuc-email` | Xác thực OTP | Không |
| POST | `/auth/gui-lai-ma-xac-thuc` | Gửi lại OTP | Không |
| POST | `/auth/dang-nhap` | Đăng nhập | Không |
| POST | `/auth/dang-xuat` | Đăng xuất | Bearer JWT |

---

# 26. HTTP Status

Một số status quan trọng:

```text
200 OK
→ Request thành công

400 Bad Request
→ Dữ liệu request không hợp lệ

401 Unauthorized
→ Chưa đăng nhập / JWT không hợp lệ / JWT hết hạn

403 Forbidden
→ Đã xác thực nhưng không có quyền thực hiện

404 Not Found
→ Không tồn tại endpoint/resource

500 Internal Server Error
→ Lỗi phía backend
```

Ví dụ middleware JWT:

```js
return res.status(401).json({
    thanh_cong: false,
    thong_bao: "Token không hợp lệ hoặc đã hết hạn"
});
```

---

# 27. Security Principles

Authentication Kenglish hiện áp dụng các nguyên tắc:

```text
✓ Không lưu plaintext password
✓ Hash password bằng bcrypt
✓ SQL parameterized query
✓ Secret lưu trong .env
✓ Không commit .env lên Git
✓ OTP có thời hạn
✓ OTP được xóa sau xác thực
✓ JWT có expiration
✓ JWT được verify ở backend
✓ Protected API sử dụng middleware
✓ Không tin user ID do client tự khai báo
```

Không được:

```text
✗ Lưu plaintext password
✗ Đưa JWT_SECRET lên GitHub
✗ Đưa Gmail App Password lên GitHub
✗ Đưa password vào JWT
✗ Đưa OTP vào JWT
✗ Tin user ID từ client mà không xác thực
```

---

# 28. Kiến thức chính đã học

Qua module Authentication của Kenglish:

### Backend

```text
Node.js
Express.js
REST API
Controller
Route
Middleware
Service
Environment Variables
```

### Database

```text
MySQL
Connection Pool
Parameterized Query
SELECT
INSERT
UPDATE
```

### Security

```text
bcrypt
Password Hashing
OTP
JWT
JWT Signature
JWT Expiration
Bearer Authentication
Protected Route
```

### Email

```text
Nodemailer
SMTP
Gmail App Password
HTML Email
```

### Authentication Flow

```text
Register
   ↓
Verify Email
   ↓
Login
   ↓
JWT
   ↓
Bearer Token
   ↓
Middleware
   ↓
Protected API
   ↓
Logout
```

---

# 29. TODO

Các chức năng có thể hoàn thiện sau:

```text
[ ] Cooldown gửi lại OTP 60 giây
[ ] Tự xóa tài khoản chưa xác thực sau 24 giờ
[ ] Access Token ngắn hạn
[ ] Refresh Token
[ ] Revoke Refresh Token khi logout
[ ] Quên mật khẩu
[ ] Đặt lại mật khẩu
[ ] Đổi mật khẩu
[ ] Rate limiting
[ ] Deploy backend
[ ] Domain email riêng
[ ] SPF / DKIM / DMARC
```

---

## Kenglish

**English Vocabulary Learning Application**

Backend Authentication:

```text
Node.js + Express + MySQL + JWT
```
