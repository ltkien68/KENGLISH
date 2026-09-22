<?php

header("Content-Type: application/json; charset=UTF-8");

require_once "../config/database.php";

// Doc du lieu JSON duoc gui den API
$duLieu = json_decode(file_get_contents("php://input"), true);

$email = trim($duLieu["email"] ?? "");
$maXacThuc = $duLieu["ma_xac_thuc"] ?? "";

if (empty($email) || empty($maXacThuc)) {

    echo json_encode([
        "thanh_cong" => false,
        "thong_bao" => "Please enter email and verification code"
    ]);

    exit;
}

// Tim nguoi dung theo email
$cauLenh = $ketNoi->prepare(
    "SELECT id, email_da_xac_thuc, ma_xac_thuc, het_han_ma_xac_thuc
     FROM nguoi_dung
     WHERE email = ?"
);

$cauLenh->bind_param(
    "s",
    $email
);

$cauLenh->execute();

$ketQua = $cauLenh->get_result();

if ($ketQua->num_rows == 0) {

    echo json_encode([
        "thanh_cong" => false,
        "thong_bao" => "Account not found"
    ]);

    $cauLenh->close();
    $ketNoi->close();

    exit;
}

// Neu da xac thuc roi
$nguoiDung = $ketQua->fetch_assoc();

if ($nguoiDung["email_da_xac_thuc"] == 1) {

    echo json_encode([
        "thanh_cong" => false,
        "thong_bao" => "Email has already been verified"
    ]);

    $cauLenh->close();
    $ketNoi->close();

    exit;
}

// Kiem tra ma xac thuc
if ($maXacThuc != $nguoiDung["ma_xac_thuc"]) {

    echo json_encode([
        "thanh_cong" => false,
        "thong_bao" => "Verification code is incorrect"
    ]);

    $cauLenh->close();
    $ketNoi->close();

    exit;
}

//Kiem tra OTP het han
$thoiGianHetHan = strtotime(
    $nguoiDung["het_han_ma_xac_thuc"]
);

if (time() > $thoiGianHetHan) {

    echo json_encode([
        "thanh_cong" => false,
        "thong_bao" => "Verification code has expired"
    ]);

    $cauLenh->close();
    $ketNoi->close();

    exit;
}

$cauLenh->close();

// Cap nhat trang thai xac thuc email
$cauLenh = $ketNoi->prepare(
    "UPDATE nguoi_dung
     SET email_da_xac_thuc = 1,
         ma_xac_thuc = NULL,
         het_han_ma_xac_thuc = NULL
     WHERE id = ?"
);

$cauLenh->bind_param(
    "i",
    $nguoiDung["id"]
);

if ($cauLenh->execute()) {

    echo json_encode([
        "thanh_cong" => true,
        "thong_bao" => "Email verified successfully"
    ]);

} else {

    echo json_encode([
        "thanh_cong" => false,
        "thong_bao" => "Email verification failed"
    ]);
}

$cauLenh->close();
$ketNoi->close();

?>