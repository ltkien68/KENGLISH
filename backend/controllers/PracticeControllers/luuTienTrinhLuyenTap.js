const db = require("../../config/database");

const luuTienTrinhLuyenTap = async (req, res) => {
  const nguoiDungId = req.nguoiDung.id;
  const { ket_qua } = req.body;

  // ==============================
  // KIỂM TRA DỮ LIỆU
  // ==============================

  if (!Array.isArray(ket_qua) || ket_qua.length === 0 || ket_qua.length > 200) {
    return res.status(400).json({
      thanh_cong: false,
      thong_bao: "Danh sách kết quả không hợp lệ",
    });
  }

  const danhSachId = new Set();

  for (const item of ket_qua) {
    if (
      !item ||
      !Number.isInteger(item.tu_vung_id) ||
      item.tu_vung_id <= 0 ||
      !Number.isInteger(item.da_thuoc) ||
      ![0, 1].includes(item.da_thuoc) ||
      danhSachId.has(item.tu_vung_id)
    ) {
      return res.status(400).json({
        thanh_cong: false,
        thong_bao: "Kết quả chứa dữ liệu không hợp lệ hoặc ID bị trùng",
      });
    }

    danhSachId.add(item.tu_vung_id);
  }

  let connection;

  try {
    connection = await db.getConnection();

    await connection.beginTransaction();

    // ==============================
    // KIỂM TRA QUYỀN SỞ HỮU
    // ==============================

    const danhSachTuId = [...danhSachId];

    const [danhSachTu] = await connection.query(
      `
        SELECT tv.id
        FROM tu_vung tv
        INNER JOIN bo_tu bt
            ON tv.bo_tu_id = bt.id
        WHERE bt.nguoi_dung_id = ?
        AND tv.id IN (?)
        FOR UPDATE
        `,
      [nguoiDungId, danhSachTuId],
    );

    if (danhSachTu.length !== danhSachTuId.length) {
      await connection.rollback();

      return res.status(403).json({
        thanh_cong: false,
        thong_bao: "Có từ vựng không thuộc tài khoản",
      });
    }

    // ==============================
    // CẬP NHẬT TRẠNG THÁI TỪ VỰNG
    // ==============================

    for (const item of ket_qua) {
      await connection.query(
        `
        UPDATE tu_vung tv
        INNER JOIN bo_tu bt
        ON tv.bo_tu_id = bt.id
        SET tv.da_thuoc = ?
        WHERE tv.id = ?
        AND bt.nguoi_dung_id = ?
        `,
        [item.da_thuoc, item.tu_vung_id, nguoiDungId],
      );
    }

    // ==============================
    // GHI NHẬN NGÀY HOẠT ĐỘNG
    // ==============================

    await connection.query(
      `
            INSERT INTO ngay_hoat_dong
                (nguoi_dung_id, ngay)
            VALUES (?, CURDATE())
            ON DUPLICATE KEY UPDATE id = id
            `,
      [nguoiDungId],
    );

    await connection.query(
      `UPDATE nguoi_dung
     SET so_luot_choi = so_luot_choi + 1
     WHERE id = ?`,
      [nguoiDungId],
    );

    // ==============================
    // TÍNH STREAK
    // ==============================

    const [danhSachNgay] = await connection.query(
      `
            SELECT DATE_FORMAT(ngay, '%Y-%m-%d') AS ngay
            FROM ngay_hoat_dong
            WHERE nguoi_dung_id = ?
              AND ngay <= CURDATE()
            ORDER BY ngay DESC
            `,
      [nguoiDungId],
    );

    let currentStreak = 0;

    // Tính streak theo chuỗi ngày liên tiếp.
    // Dùng phép tính ngày trên UTC để tránh lệch DST
    // khi trừ từng ngày; ngày gốc lấy từ MySQL.
    const [ngayHienTaiRows] = await connection.query(
      "SELECT DATE_FORMAT(CURDATE(), '%Y-%m-%d') AS hom_nay",
    );

    const homNay = ngayHienTaiRows[0].hom_nay;

    const ngayKiemTra = new Date(homNay + "T00:00:00.000Z");

    const tapNgay = new Set(danhSachNgay.map((item) => item.ngay));

    while (true) {
      const ngay = ngayKiemTra.toISOString().slice(0, 10);

      if (!tapNgay.has(ngay)) {
        break;
      }

      currentStreak++;

      ngayKiemTra.setUTCDate(ngayKiemTra.getUTCDate() - 1);
    }

    const [thongTinNguoiDung] = await connection.query(
      `SELECT so_luot_choi
     FROM nguoi_dung
     WHERE id = ?`,
      [nguoiDungId],
    );

    const soLuotChoi = thongTinNguoiDung[0].so_luot_choi;

    await connection.commit();

    return res.status(200).json({
      thanh_cong: true,
      thong_bao: "Đã lưu tiến trình luyện tập",
      data: {
        so_tu_da_luu: ket_qua.length,
        current_streak: currentStreak,
        so_luot_choi: soLuotChoi,
      },
    });
  } catch (error) {
    if (connection) {
      await connection.rollback();
    }

    console.error("Lỗi lưu tiến trình:", error);

    return res.status(500).json({
      thanh_cong: false,
      thong_bao: "Không thể lưu tiến trình luyện tập",
    });
  } finally {
    if (connection) {
      connection.release();
    }
  }
};

module.exports = luuTienTrinhLuyenTap;
