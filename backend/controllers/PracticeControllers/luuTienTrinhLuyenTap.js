const db = require("../../config/database");

const tinhStreak = async (connection, nguoiDungId) => {
  const [danhSachNgay] = await connection.query(
    `
    SELECT DATE_FORMAT(ngay, '%Y-%m-%d') AS ngay
    FROM ngay_hoat_dong
    WHERE nguoi_dung_id = ?
      AND ngay <= CURDATE()
    ORDER BY ngay DESC
    `,
    [nguoiDungId]
  );

  const [ngayHienTaiRows] = await connection.query(
    "SELECT DATE_FORMAT(CURDATE(), '%Y-%m-%d') AS hom_nay"
  );

  const homNay = ngayHienTaiRows[0].hom_nay;

  const ngayKiemTra = new Date(
    homNay + "T00:00:00.000Z"
  );

  const tapNgay = new Set(
    danhSachNgay.map((item) => item.ngay)
  );

  let currentStreak = 0;

  while (true) {
    const ngay = ngayKiemTra
      .toISOString()
      .slice(0, 10);

    if (!tapNgay.has(ngay)) {
      break;
    }

    currentStreak++;

    ngayKiemTra.setUTCDate(
      ngayKiemTra.getUTCDate() - 1
    );
  }

  return currentStreak;
};

const luuTienTrinhLuyenTap = async (req, res) => {
  const nguoiDungId = req.nguoiDung.id;

  const {
    ma_phien_choi,
    che_do,
    ket_qua
  } = req.body || {};

  // ==============================
  // KIỂM TRA MÃ PHIÊN
  // ==============================

  const uuidRegex =
    /^[0-9a-f]{8}-[0-9a-f]{4}-[1-8][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i;

  if (
    typeof ma_phien_choi !== "string" ||
    !uuidRegex.test(ma_phien_choi)
  ) {
    return res.status(400).json({
      thanh_cong: false,
      thong_bao: "Mã phiên luyện tập không hợp lệ"
    });
  }

  // ==============================
  // KIỂM TRA CHẾ ĐỘ
  // ==============================

  const danhSachCheDo = [
    "flashcard",
    "trac_nghiem",
    "noi_tu",
    "go_tu",
    "nghe_viet",
    "dac_biet"
  ];

  if (
    typeof che_do !== "string" ||
    !danhSachCheDo.includes(che_do)
  ) {
    return res.status(400).json({
      thanh_cong: false,
      thong_bao: "Chế độ luyện tập không hợp lệ"
    });
  }

  // ==============================
  // KIỂM TRA DANH SÁCH KẾT QUẢ
  // ==============================

  if (
    !Array.isArray(ket_qua) ||
    ket_qua.length === 0 ||
    ket_qua.length > 200
  ) {
    return res.status(400).json({
      thanh_cong: false,
      thong_bao: "Danh sách kết quả không hợp lệ"
    });
  }

  const danhSachId = new Set();

  let soDung = 0;
  let soSai = 0;

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
        thong_bao:
          "Kết quả chứa dữ liệu không hợp lệ hoặc ID bị trùng"
      });
    }

    danhSachId.add(item.tu_vung_id);

    if (item.da_thuoc === 1) {
      soDung++;
    } else {
      soSai++;
    }
  }

  const tongSoCau = ket_qua.length;

  let connection;
  let daBatDauTransaction = false;

  try {
    connection = await db.getConnection();

    await connection.beginTransaction();
    daBatDauTransaction = true;

    // ==============================
    // KHÓA NGƯỜI DÙNG
    // ==============================

    const [nguoiDungRows] = await connection.query(
      `
      SELECT id, so_luot_choi
      FROM nguoi_dung
      WHERE id = ?
      FOR UPDATE
      `,
      [nguoiDungId]
    );

    if (nguoiDungRows.length === 0) {
      await connection.rollback();
      daBatDauTransaction = false;

      return res.status(404).json({
        thanh_cong: false,
        thong_bao: "Không tìm thấy người dùng"
      });
    }

    // ==============================
    // KIỂM TRA PHIÊN ĐÃ LƯU
    // ==============================

    const [phienDaLuu] = await connection.query(
      `
      SELECT
        id,
        che_do,
        tong_so_cau,
        so_dung,
        so_sai
      FROM lich_su_luyen_tap
      WHERE nguoi_dung_id = ?
        AND ma_phien_choi = ?
      FOR UPDATE
      `,
      [nguoiDungId, ma_phien_choi]
    );

    if (phienDaLuu.length > 0) {
      const phien = phienDaLuu[0];

      // Không cho dùng lại mã phiên
      // với loại game hoặc tổng kết khác.
      if (
        phien.che_do !== che_do ||
        phien.tong_so_cau !== tongSoCau ||
        phien.so_dung !== soDung ||
        phien.so_sai !== soSai
      ) {
        await connection.rollback();
        daBatDauTransaction = false;

        return res.status(409).json({
          thanh_cong: false,
          thong_bao:
            "Mã phiên đã được sử dụng cho kết quả khác"
        });
      }

      const currentStreak = await tinhStreak(
        connection,
        nguoiDungId
      );

      const soLuotChoi =
        nguoiDungRows[0].so_luot_choi;

      await connection.commit();
      daBatDauTransaction = false;

      return res.status(200).json({
        thanh_cong: true,
        thong_bao: "Phiên luyện tập đã được lưu trước đó",
        data: {
          lich_su_id: phien.id,
          ma_phien_choi,
          da_luu_truoc_do: true,
          so_tu_da_luu: phien.tong_so_cau,
          current_streak: currentStreak,
          so_luot_choi: soLuotChoi
        }
      });
    }

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
      [nguoiDungId, danhSachTuId]
    );

    if (danhSachTu.length !== danhSachTuId.length) {
      await connection.rollback();
      daBatDauTransaction = false;

      return res.status(403).json({
        thanh_cong: false,
        thong_bao: "Có từ vựng không thuộc tài khoản"
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
        [
          item.da_thuoc,
          item.tu_vung_id,
          nguoiDungId
        ]
      );
    }

    // ==============================
    // LƯU LỊCH SỬ LUYỆN TẬP
    // ==============================

    const [ketQuaThemLichSu] =
      await connection.query(
        `
        INSERT INTO lich_su_luyen_tap (
          nguoi_dung_id,
          ma_phien_choi,
          che_do,
          tong_so_cau,
          so_dung,
          so_sai,
          ngay_luyen_tap
        )
        VALUES (?, ?, ?, ?, ?, ?, NOW())
        `,
        [
          nguoiDungId,
          ma_phien_choi,
          che_do,
          tongSoCau,
          soDung,
          soSai
        ]
      );

    const lichSuId = ketQuaThemLichSu.insertId;

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
      [nguoiDungId]
    );

    // ==============================
    // TĂNG SỐ LƯỢT CHƠI
    // ==============================

    await connection.query(
      `
      UPDATE nguoi_dung
      SET so_luot_choi = so_luot_choi + 1
      WHERE id = ?
      `,
      [nguoiDungId]
    );

    const soLuotChoi =
      Number(nguoiDungRows[0].so_luot_choi) + 1;

    // ==============================
    // TÍNH STREAK
    // ==============================

    const currentStreak = await tinhStreak(
      connection,
      nguoiDungId
    );

    // ==============================
    // HOÀN TẤT TRANSACTION
    // ==============================

    await connection.commit();
    daBatDauTransaction = false;

    return res.status(200).json({
      thanh_cong: true,
      thong_bao: "Đã lưu tiến trình luyện tập",
      data: {
        lich_su_id: lichSuId,
        ma_phien_choi,
        da_luu_truoc_do: false,
        so_tu_da_luu: tongSoCau,
        current_streak: currentStreak,
        so_luot_choi: soLuotChoi
      }
    });

  } catch (error) {
    if (connection && daBatDauTransaction) {
      try {
        await connection.rollback();
      } catch (rollbackError) {
        console.error(
          "Lỗi rollback tiến trình:",
          rollbackError
        );
      }
    }

    console.error("Lỗi lưu tiến trình:", error);

    return res.status(500).json({
      thanh_cong: false,
      thong_bao: "Không thể lưu tiến trình luyện tập"
    });

  } finally {
    if (connection) {
      connection.release();
    }
  }
};

module.exports = luuTienTrinhLuyenTap;
