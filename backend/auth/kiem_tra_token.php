<?php

function layNguoiDungTuToken($ketNoi) {

    // Lay Authorization header
    $headers = getallheaders();

    $authorization = $headers["Authorization"] ?? "";

    if (empty($authorization)) {
        return null;
    }

    // Authorization: Bearer TOKEN
    if (!preg_match(
        '/Bearer\s+(\S+)/',
        $authorization,
        $matches
    )) {
        return null;
    }

    $token = $matches[1];

    // Hash token de so sanh voi database
    $tokenHash = hash("sha256", $token);

    $cauLenh = $ketNoi->prepare(
        "SELECT nguoi_dung_id
         FROM phien_dang_nhap
         WHERE token_hash = ?
         AND het_han > NOW()"
    );

    $cauLenh->bind_param(
        "s",
        $tokenHash
    );

    $cauLenh->execute();

    $ketQua = $cauLenh->get_result();

    if ($ketQua->num_rows == 0) {

        $cauLenh->close();

        return null;
    }

    $phienDangNhap = $ketQua->fetch_assoc();

    $cauLenh->close();

    return $phienDangNhap["nguoi_dung_id"];
}

?>