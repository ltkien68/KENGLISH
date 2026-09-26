<?php

header("Content-Type: application/json; charset=UTF-8");

require_once "../config/database.php";

// Doc du lieu JSON duoc gui den API
$duLieu = json_decode(file_get_contents("php://input"), true);

// Lay thong tin dang nhap
$email = trim($duLieu["email"] ?? "");
$matKhau = $duLieu["mat_khau"] ?? "";

if (empty($email) || empty($matKhau)) {
    echo json_encode([
        "thanh_cong" => false,
        "thong_bao" => "Vui lòng nhập email và mật khẩu"
    ]);

    exit;
}

$cauLenh = $ketNoi->prepare(
    "SELECT id, ten_hien_thi, email, mat_khau, email_da_xac_thuc
     FROM nguoi_dung
     WHERE email = ?"
);

$cauLenh->bind_param("s", $email);

$cauLenh->execute();

$ketQua = $cauLenh->get_result();

if ($ketQua->num_rows == 0) {
    echo json_encode([
        "thanh_cong" => false,
        "thong_bao" => "Email hoặc tài khoản chưa đúng"
    ]);

    $cauLenh->close();
    $ketNoi->close();

    exit;
}

    $nguoiDung = $ketQua->fetch_assoc();

if (!password_verify($matKhau, $nguoiDung["mat_khau"])) {
    echo json_encode([
        "thanh_cong" => false,
        "thong_bao" => "Email hoặc tài khoản chưa đúng"
    ]);

    $cauLenh->close();
    $ketNoi->close();

    exit;
}

// Kiem tra email da duoc xac thuc hay chua
if ($nguoiDung["email_da_xac_thuc"] == 0) {

    echo json_encode([
        "thanh_cong" => false,
        "thong_bao" => "Vui lòng xác thực email của bạn trước khi đăng nhập"
    ]);

    $cauLenh->close();
    $ketNoi->close();

    exit;
}

// Dang nhap thanh cong
// Tao token ngau nhien
$token = bin2hex(random_bytes(32));

// Hash token truoc khi luu vao database
$tokenHash = hash("sha256", $token);

// Token co hieu luc trong 30 ngay
$hetHan = date(
    "Y-m-d H:i:s",
    time() + (30 * 24 * 60 * 60)
);

$idNguoiDung = $nguoiDung["id"];

// Dong cau lenh SELECT cu
$cauLenh->close();

// Luu phien dang nhap
$cauLenh = $ketNoi->prepare(
    "INSERT INTO phien_dang_nhap
    (nguoi_dung_id, token_hash, het_han)
    VALUES (?, ?, ?)"
);

$cauLenh->bind_param(
    "iss",
    $idNguoiDung,
    $tokenHash,
    $hetHan
);

if ($cauLenh->execute()) {

    echo json_encode([
        "thanh_cong" => true,
        "thong_bao" => "Login successful",
        "token" => $token,
        "nguoi_dung" => [
            "id" => $nguoiDung["id"],
            "ten_hien_thi" => $nguoiDung["ten_hien_thi"],
            "email" => $nguoiDung["email"]
        ]
    ]);

} else {

    echo json_encode([
        "thanh_cong" => false,
        "thong_bao" => "Đăng nhập thất bại"
    ]);
}

$cauLenh->close();
$ketNoi->close();

?>