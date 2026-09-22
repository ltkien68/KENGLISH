<?php

use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

require_once __DIR__ . "/../vendor/autoload.php";
require_once __DIR__ . "/email_secret.php";

function guiMaXacThuc($email, $maXacThuc) {

    $mail = new PHPMailer(true);

    try {

        // Su dung SMTP
        $mail->isSMTP();

        // Cau hinh Gmail SMTP
        $mail->Host = "smtp.gmail.com";
        $mail->SMTPAuth = true;

        $mail->Username = EMAIL_GUI;
        $mail->Password = EMAIL_APP_PASSWORD;

        // Ma hoa ket noi
        $mail->SMTPSecure = PHPMailer::ENCRYPTION_STARTTLS;
        $mail->Port = 587;

        // Ho tro noi dung UTF-8
        $mail->CharSet = "UTF-8";

        // Nguoi gui
        $mail->setFrom(
            "ltkien.aug6@gmail.com",
            "Kenglish"
        );

        // Nguoi nhan
        $mail->addAddress($email);

        // Gui email dang HTML
        $mail->isHTML(true);

        $mail->Subject = "Kenglish - Email Verification";

        $mail->Body = "
            <h2>Xác minh tài khoản Kenglish của bạn</h2>

            <p>Mã xác minh của bạn là:</p>

            <h1>$maXacThuc</h1>

            <p>Mã sẽ hết hạn sau 5 phút.</p>
        ";

        $mail->AltBody =
            "Your Kenglish verification code is: $maXacThuc. " .
            "This code will expire in 5 minutes.";

        $mail->send();

        return true;

    } catch (Exception $e) {

        return false;
    }
}

?>