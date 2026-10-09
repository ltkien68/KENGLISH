
const db = require("../../config/database");

const layLichSuLuyenTap = async (req, res) => {
  try {
    const nguoiDungId = req.nguoiDung.id;

    const trang = Number(req.query.page || 1);
    const gioiHan = Number(req.query.limit || 10);

    if (
      !Number.isInteger(trang) ||
      trang < 1 ||
      !Number.isInteger(gioiHan) ||
      gioiHan < 1 ||
      gioiHan > 50
    ) {
      return res.status(400).json({
        thanh_cong: false,
        thong_bao: "Tham số phân trang không hợp lệ"
      });
    }

    const offset = (trang - 1) * gioiHan;

    const [demRows] = await db.query(
      `
      SELECT COUNT(*) AS tong_so
      FROM lich_su_luyen_tap
      WHERE nguoi_dung_id = ?
      `,
      [nguoiDungId]
    );

    const tongSo = demRows[0].tong_so;

    const [danhSach] = await db.query(
      `
      SELECT
        id,
        che_do,
        tong_so_cau,
        so_dung,
        so_sai,
        ROUND(
          so_dung * 100.0 /
          NULLIF(tong_so_cau, 0),
          0
        ) AS phan_tram,
        DATE_FORMAT(
          ngay_luyen_tap,
          '%Y-%m-%d %H:%i:%s'
        ) AS ngay_luyen_tap
      FROM lich_su_luyen_tap
      WHERE nguoi_dung_id = ?
      ORDER BY ngay_luyen_tap DESC, id DESC
      LIMIT ? OFFSET ?
      `,
      [nguoiDungId, gioiHan, offset]
    );

    return res.status(200).json({
      thanh_cong: true,
      thong_bao: "Lấy lịch sử luyện tập thành công",
      data: {
        danh_sach: danhSach,
        trang_hien_tai: trang,
        tong_so: tongSo,
        tong_trang: Math.ceil(tongSo / gioiHan)
      }
    });

  } catch (error) {
    console.error("Lỗi lấy lịch sử:", error);

    return res.status(500).json({
      thanh_cong: false,
      thong_bao: "Không thể lấy lịch sử luyện tập"
    });
  }
};

module.exports = layLichSuLuyenTap;
