const crypto = require("crypto");

const ketNoi = require("../../config/database");
const { guiMaDatLaiMatKhau } = require("../../services/emailService");

async function quenMatKhau(req, res) {
    try {
        const {email = ""} = req.body;

        const emailNguoiDung = email.trim();
        if (!emailNguoiDung) {
            return res.json({
                thanh_cong: false,
                thong_bao: "Vui lòng nhập email"
            });
        }

        // Tìm tài khoản
        const [ketQua] = await ketNoi.execute(
            `SELECT
                id,
                email,
                email_da_xac_thuc,
                da_xoa
            FROM nguoi_dung
            WHERE email = ?`,
            [emailNguoiDung]
        );

        if (ketQua.length === 0 || ketQua[0].da_xoa === 1) {
            return res.json({
                thanh_cong: true,
                thong_bao: "Mã OTP sẽ được gửi tới email của bạn"
            });
        }

        const nguoiDung = ketQua[0];

        if (nguoiDung.email_da_xac_thuc === 0) {
            return res.json({
                thanh_cong: false,
                thong_bao: "Tài khoản chưa được xác thực"
            });
        }
        // Tạo OTP 6 chữ số
        const maXacThuc = crypto.randomInt(100000, 1000000);
        const hetHanMaXacThuc = new Date(Date.now() + 5 * 60 * 1000);

        await ketNoi.execute(
            `UPDATE nguoi_dung
             SET ma_xac_thuc = ?,
                 het_han_ma_xac_thuc = ?
             WHERE id = ?`,
            [
                maXacThuc,
                hetHanMaXacThuc,
                nguoiDung.id
            ]
        );

        const guiEmailThanhCong = await guiMaDatLaiMatKhau(emailNguoiDung, maXacThuc);

        if (!guiEmailThanhCong) {
            await ketNoi.execute(
                `UPDATE nguoi_dung
                 SET ma_xac_thuc = NULL,
                     het_han_ma_xac_thuc = NULL
                 WHERE id = ?`,
                [nguoiDung.id]
            );

            return res.json({
                thanh_cong: false,
                thong_bao:
                    "Không thể gửi mã OTP tới email của bạn"
            });
        }

        return res.json({
            thanh_cong: true,
            thong_bao:
                "Mã OTP đã được gửi tới email của bạn"
        });
    } catch (error) {
        console.error("Lỗi quên mật khẩu:", error);

        return res.status(500).json({
            thanh_cong: false,
            thong_bao: "Không thể thực hiện yêu cầu quên mật khẩu"
        });
    }
}

module.exports = quenMatKhau;