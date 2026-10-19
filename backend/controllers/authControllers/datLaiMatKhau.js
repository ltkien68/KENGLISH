const bcrypt = require("bcrypt");

const ketNoi = require("../../config/database");

async function datLaiMatKhau(req, res) {
    try {
        const {
            email = "",
            ma_xac_thuc = "",
            mat_khau_moi = ""
        } = req.body;

        const emailNguoiDung = email.trim();

        // Kiểm tra dữ liệu
        if (!emailNguoiDung || !ma_xac_thuc || !mat_khau_moi) {
            return res.json({
                thanh_cong: false,
                thong_bao:
                    "Vui lòng nhập đầy đủ email, mã OTP và mật khẩu mới"
            });
        }

        // Kiểm tra độ dài mật khẩu
        if (mat_khau_moi.length < 6) {
            return res.json({
                thanh_cong: false,
                thong_bao:
                    "Mật khẩu phải chứa ít nhất 6 ký tự"
            });
        }

        // Tìm tài khoản
        const [ketQua] = await ketNoi.execute(
            `SELECT
                id,
                email,
                mat_khau,
                email_da_xac_thuc,
                da_xoa,
                ma_xac_thuc,
                het_han_ma_xac_thuc
             FROM nguoi_dung
             WHERE email = ?`,
            [emailNguoiDung]
        );

        if (ketQua.length === 0) {
            return res.json({
                thanh_cong: false,
                thong_bao: "Email hoặc mã OTP không đúng"
            });
        }

        const nguoiDung = ketQua[0];

        // Tài khoản đã bị xóa
        if (nguoiDung.da_xoa === 1) {
            return res.json({
                thanh_cong: false,
                thong_bao: "Email hoặc mã OTP không đúng"
            });
        }

        // Email chưa xác thực
        if (nguoiDung.email_da_xac_thuc === 0) {
            return res.json({
                thanh_cong: false,
                thong_bao:
                    "Email của tài khoản chưa được xác thực"
            });
        }

        // Kiểm tra OTP
        if (
            !nguoiDung.ma_xac_thuc ||
            String(ma_xac_thuc) !==
            String(nguoiDung.ma_xac_thuc)
        ) {
            return res.json({
                thanh_cong: false,
                thong_bao: "Mã OTP không đúng"
            });
        }

        // Kiểm tra OTP hết hạn
        if (!nguoiDung.het_han_ma_xac_thuc) {
            return res.json({
                thanh_cong: false,
                thong_bao: "Mã OTP không còn hiệu lực"
            });
        }

        const thoiGianHetHan =
            new Date(nguoiDung.het_han_ma_xac_thuc);

        if (new Date() > thoiGianHetHan) {
            return res.json({
                thanh_cong: false,
                thong_bao: "Mã OTP đã hết hạn"
            });
        }

        // Không cho sử dụng lại mật khẩu cũ
        const trungMatKhauCu =
            await bcrypt.compare(
                mat_khau_moi,
                nguoiDung.mat_khau
            );

        if (trungMatKhauCu) {
            return res.json({
                thanh_cong: false,
                thong_bao:
                    "Mật khẩu mới không được trùng mật khẩu hiện tại"
            });
        }

        // Hash mật khẩu mới
        const matKhauHash =
            await bcrypt.hash(mat_khau_moi, 10);

        // Cập nhật mật khẩu và xóa OTP
        await ketNoi.execute(
            `UPDATE nguoi_dung
             SET mat_khau = ?,
                 ma_xac_thuc = NULL,
                 het_han_ma_xac_thuc = NULL
             WHERE id = ?`,
            [
                matKhauHash,
                nguoiDung.id
            ]
        );

        return res.json({
            thanh_cong: true,
            thong_bao:
                "Đặt lại mật khẩu thành công"
        });

    } catch (error) {
        console.error("Lỗi đặt lại mật khẩu:", error);

        return res.status(500).json({
            thanh_cong: false,
            thong_bao:
                "Đặt lại mật khẩu thất bại"
        });
    }
}

module.exports = datLaiMatKhau;