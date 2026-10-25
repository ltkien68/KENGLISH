const ketNoi = require("../../config/database");

async function xacThucEmail(req, res) {
  try {
    const {
      email = "",
      ma_xac_thuc = "",
      loai = "xac_thuc_tai_khoan",
    } = req.body;

    const emailNguoiDung = email.trim();

    if (!emailNguoiDung || !ma_xac_thuc) {
      return res.json({
        thanh_cong: false,
        thong_bao: "Vui lòng nhập mã OTP đã được gửi tới email của bạn",
      });
    }

    const [ketQua] = await ketNoi.execute(
      `SELECT
                id,
                email_da_xac_thuc,
                ma_xac_thuc,
                het_han_ma_xac_thuc
             FROM nguoi_dung
             WHERE email = ?`,
      [emailNguoiDung],
    );

    if (ketQua.length === 0) {
      return res.json({
        thanh_cong: false,
        thong_bao: "Tài khoản không tìm thấy",
      });
    }

    const nguoiDung = ketQua[0];

    // Luồng đăng ký tài khoản
    if (loai === "xac_thuc_tai_khoan") {
      if (nguoiDung.email_da_xac_thuc === 1) {
        return res.json({
          thanh_cong: false,
          thong_bao: "Email đã được xác thực",
        });
      }
    }

    // Kiểm tra OTP
    if (String(ma_xac_thuc) !== String(nguoiDung.ma_xac_thuc)) {
      return res.json({
        thanh_cong: false,
        thong_bao: "Mã OTP không đúng",
      });
    }

    // Kiểm tra OTP hết hạn
    const thoiGianHetHan = new Date(nguoiDung.het_han_ma_xac_thuc);

    if (new Date() > thoiGianHetHan) {
      return res.json({
        thanh_cong: false,
        thong_bao: "Mã OTP đã hết hạn",
      });
    }

    // =========================
    // XÁC THỰC TÀI KHOẢN
    // =========================
    if (loai === "xac_thuc_tai_khoan") {
      await ketNoi.execute(
        `UPDATE nguoi_dung
                 SET email_da_xac_thuc = 1,
                     ma_xac_thuc = NULL,
                     het_han_ma_xac_thuc = NULL,
                     da_xoa = 0,
                     xoa_luc = NULL
                 WHERE id = ?`,
        [nguoiDung.id],
      );

      return res.json({
        thanh_cong: true,
        thong_bao: "Email xác thực thành công",
      });
    }

    // =========================
    // ĐẶT LẠI MẬT KHẨU
    // =========================
    if (loai === "dat_lai_mat_khau") {
      await ketNoi.execute(
        `UPDATE nguoi_dung
                 WHERE id = ?`,
        [nguoiDung.id],
      );

      return res.json({
        thanh_cong: true,
        thong_bao: "Mã OTP hợp lệ",
      });
    }

    return res.json({
      thanh_cong: false,
      thong_bao: "Loại xác thực không hợp lệ",
    });
  } catch (error) {
    console.error("Lỗi xác thực email:", error);

    return res.status(500).json({
      thanh_cong: false,
      thong_bao: "Xác thực thất bại",
    });
  }
}

module.exports = xacThucEmail;
