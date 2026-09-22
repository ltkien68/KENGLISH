<?php

header("Content-Type: application/json; charset=UTF-8");

require_once "../config/database.php";

// Lay Authorization header
$headers = getallheaders();

$authorization = $headers["Authorization"] ?? "";

if (!preg_match(
    '/Bearer\s+(\S+)/',
    $authorization,
    $matches
)) {

    echo json_encode([
        "thanh_cong" => false,
        "thong_bao" => "Invalid authentication token"
    ]);

    exit;
}

$token = $matches[1];

$tokenHash = hash(
    "sha256",
    $token
);

// Xoa phien dang nhap
$cauLenh = $ketNoi->prepare(
    "DELETE FROM phien_dang_nhap
     WHERE token_hash = ?"
);

$cauLenh->bind_param(
    "s",
    $tokenHash
);

$cauLenh->execute();

echo json_encode([
    "thanh_cong" => true,
    "thong_bao" => "Logout successful"
]);

$cauLenh->close();
$ketNoi->close();

?>