const db = require("../../config/database");

const layHoatDongNamHienTai = async (req, res) => {
  try {
    const nguoiDungId = req.nguoiDung.id;

    const [rows] = await db.query(
      `
                    SELECT
                        DATE_FORMAT(
                            ngay,
                            '%Y-%m-%d'
                        ) AS ngay

                    FROM ngay_hoat_dong

                    WHERE nguoi_dung_id = ?

                    AND YEAR(ngay) =
                        YEAR(CURDATE())

                    ORDER BY ngay ASC
                    `,
      [nguoiDungId],
    );

    const danhSachNgay = rows.map((item) => item.ngay);

    /*
     * ===============================
     * TÍNH CURRENT STREAK
     * ===============================
     */

    const tapNgay = new Set(danhSachNgay);

    let streak = 0;

    const ngayKiemTra = new Date();

    /*
     * Nếu hôm nay chưa hoạt động,
     * kiểm tra từ hôm qua.
     *
     * Như vậy streak hôm qua không
     * biến thành 0 ngay đầu ngày.
     */
    const homNay = ngayKiemTra.toISOString().slice(0, 10);

    if (!tapNgay.has(homNay)) {
      return res.status(200).json({
        thanh_cong: true,

        data: {
          nam: new Date().getFullYear(),
          current_streak: 0,
          ngay_hoat_dong: danhSachNgay,
        },
      });
    }

    while (true) {
      const ngay = ngayKiemTra.toISOString().slice(0, 10);

      if (!tapNgay.has(ngay)) {
        break;
      }

      streak++;

      ngayKiemTra.setDate(ngayKiemTra.getDate() - 1);
    }

    return res.status(200).json({
      thanh_cong: true,

      data: {
        nam: new Date().getFullYear(),

        current_streak: streak,

        ngay_hoat_dong: danhSachNgay,
      },
    });
  } catch (error) {
    console.error("Lỗi lấy hoạt động:", error);

    return res.status(500).json({
      thanh_cong: false,
      thong_bao: "Lỗi server",
    });
  }
};

module.exports = layHoatDongNamHienTai;
