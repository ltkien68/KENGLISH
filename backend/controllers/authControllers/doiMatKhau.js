const bcrypt = require("bcrypt");

const ketNoi = require("../../config/database");

async function doiMatKhau(req, res) {
    try {
        const {
            mat_khau_hien_tai = "",
            mat_khau_moi = ""
        } = req.body;

        // id được lấy từ JWT middleware
        const idNguoiDung = req.nguoiDung.id;

        if (!mat_khau_hien_tai || !mat_khau_moi) {
            return res.json({
                thanh_cong: false,
                thong_bao:
                    "Vui lòng nhập đầy đủ thông tin mật khẩu"
            });
        }

        // Kiểm tra độ dài mật khẩu mới
        if (mat_khau_moi.length < 6) {
            return res.json({
                thanh_cong: false,
                thong_bao:
                    "Mật khẩu mới phải chứa ít nhất 6 ký tự"
            });
        }

        // Lấy mật khẩu hiện tại
        const [ketQua] = await ketNoi.execute(
            `SELECT
                id,
                mat_khau,
                da_xoa
             FROM nguoi_dung
             WHERE id = ?`,
            [idNguoiDung]
        );

        if (ketQua.length === 0) {
            return res.json({
                thanh_cong: false,
                thong_bao: "Không tìm thấy tài khoản"
            });
        }

        const nguoiDung = ketQua[0];

        if (nguoiDung.da_xoa === 1) {
            return res.json({
                thanh_cong: false,
                thong_bao: "Tài khoản không còn hoạt động"
            });
        }

        // Kiểm tra mật khẩu hiện tại
        const matKhauHienTaiDung =
            await bcrypt.compare(
                mat_khau_hien_tai,
                nguoiDung.mat_khau
            );

        if (!matKhauHienTaiDung) {
            return res.json({
                thanh_cong: false,
                thong_bao:
                    "Mật khẩu hiện tại không đúng"
            });
        }

        // Không cho đổi thành chính mật khẩu cũ
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

        // Cập nhật
        await ketNoi.execute(
            `UPDATE nguoi_dung
             SET mat_khau = ?
             WHERE id = ?`,
            [
                matKhauHash,
                nguoiDung.id
            ]
        );

        return res.json({
            thanh_cong: true,
            thong_bao:
                "Đổi mật khẩu thành công"
        });

    } catch (error) {
        console.error("Lỗi đổi mật khẩu:", error);

        return res.status(500).json({
            thanh_cong: false,
            thong_bao:
                "Đổi mật khẩu thất bại"
        });
    }
}

module.exports = doiMatKhau;