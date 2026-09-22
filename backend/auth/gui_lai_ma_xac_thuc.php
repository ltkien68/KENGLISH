<?php

header("Content-Type: application/json; charset=UTF-8");

require_once "../config/database.php";
require_once "../config/gui_email.php";

// Doc du lieu JSON duoc gui den API
$duLieu = json_decode(
    file_get_contents("php://input"),
    true
);

$email = trim($duLieu["email"] ?? "");

if (empty($email)) {

    echo json_encode([
        "thanh_cong" => false,
        "thong_bao" => "Please enter your email"
    ]);

    exit;
}

// Tim nguoi dung theo email
$cauLenh = $ketNoi->prepare(
    "SELECT id, email_da_xac_thuc
     FROM nguoi_dung
     WHERE email = ?"
);

$cauLenh->bind_param("s", $email);

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

// Tao ma xac thuc moi
$maXacThuc = random_int(100000, 999999);

// Tao thoi gian het han moi: 5 phut
$hetHanMaXacThuc = date(
    "Y-m-d H:i:s",
    time() + 300
);

$cauLenh->close();

$cauLenh = $ketNoi->prepare(
    "UPDATE nguoi_dung
     SET ma_xac_thuc = ?,
         het_han_ma_xac_thuc = ?
     WHERE id = ?"
);

$cauLenh->bind_param(
    "isi",
    $maXacThuc,
    $hetHanMaXacThuc,
    $nguoiDung["id"]
);

if ($cauLenh->execute()) {

    $guiEmailThanhCong = guiMaXacThuc(
        $email,
        $maXacThuc
    );

    if ($guiEmailThanhCong) {

        echo json_encode([
            "thanh_cong" => true,
            "thong_bao" => "A new verification code has been sent to your email"
        ]);

    } else {

        echo json_encode([
            "thanh_cong" => false,
            "thong_bao" => "Failed to send verification email"
        ]);
    }

}

$cauLenh->close();
$ketNoi->close();

?>