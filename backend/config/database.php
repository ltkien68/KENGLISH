<?php

$host = "localhost";
$tenDatabase = "kenglish";
$tenDangNhap = "root";
$matKhau = "";

$ketNoi = new mysqli(
    $host,
    $tenDangNhap,
    $matKhau,
    $tenDatabase
);

if ($ketNoi->connect_error) {
    die("Ket noi that bai: " . $ketNoi->connect_error);
}

$ketNoi->set_charset("utf8mb4");

?>