<?php

header("Content-Type: application/json; charset=UTF-8");

require_once "../config/database.php";
require_once "../config/gui_email.php";

$duLieu = json_decode(file_get_contents("php://input"), true);

$tenHienThi = trim($duLieu["ten_hien_thi"] ?? "");
$email = trim($duLieu["email"] ?? "");
$matKhau = $duLieu["mat_khau"] ?? "";

if (empty($tenHienThi) || empty($email) || empty($matKhau)) {
    echo json_encode([
        "thanh_cong" => false, 
        "thong_bao" => "Please enter all the required information"
    ]);

    exit;
}

if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    echo json_encode([
        "thanh_cong" => false,
        "thong_bao" => "Email is not valid"
    ]);

    exit;
}

if (strlen($matKhau) < 6) {
    echo json_encode([
        "thanh_cong" => false,
        "thong_bao" => "Password must be at least 6 characters"
    ]);

    exit;
}

$cauLenh = $ketNoi->prepare("SELECT id FROM nguoi_dung WHERE email = ?");

$cauLenh->bind_param("s", $email);

$cauLenh->execute();

$ketQua = $cauLenh->get_result();

if ($ketQua->num_rows > 0) {
    echo json_encode([
        "thanh_cong" => false,
        "thong_bao" => "Email already exists"
    ]);

    $cauLenh->close();
    exit;
}

$cauLenh->close();

// Hash mat khau
$matKhauHash = password_hash($matKhau, PASSWORD_DEFAULT);

// Tao ma xac thuc gom 6 chu so
$maXacThuc = random_int(100000, 999999);

// Ma xac thuc co hieu luc trong 5 phut
$hetHanMaXacThuc = date(
    "Y-m-d H:i:s",
    time() + 300
);

// Chuan bi cau lenh them nguoi dung
$cauLenh = $ketNoi->prepare(
    "INSERT INTO nguoi_dung
    (
        ten_hien_thi,
        email,
        mat_khau,
        ma_xac_thuc,
        het_han_ma_xac_thuc
    )
    VALUES (?, ?, ?, ?, ?)"
);

$cauLenh->bind_param(
    "sssis",
    $tenHienThi,
    $email,
    $matKhauHash,
    $maXacThuc,
    $hetHanMaXacThuc
);

// Them nguoi dung vao database
if ($cauLenh->execute()) {
    // Gui ma xac thuc den email
    $guiEmailThanhCong = guiMaXacThuc(
        $email,
        $maXacThuc
    );

    if ($guiEmailThanhCong) {
        echo json_encode([
            "thanh_cong" => true,
            "thong_bao" => "Registration successful. Please check your email."
        ]);

    } else {
        echo json_encode([
            "thanh_cong" => false,
            "thong_bao" => "Account created, but verification email could not be sent"
        ]);
    }

} else {
    echo json_encode([
        "thanh_cong" => false,
        "thong_bao" => "Registration failed"
    ]);
}

$cauLenh->close();
$ketNoi->close();

?>