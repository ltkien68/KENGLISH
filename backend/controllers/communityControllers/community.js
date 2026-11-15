const ketNoi = require("../../config/database");

// Kiểm tra định dạng ngày YYYY-MM-DD
function ngayHopLe(ngay) {
  if (!/^\d{4}-\d{2}-\d{2}$/.test(ngay)) {
    return false;
  }

  const [nam, thang, ngayTrongThang] = ngay.split("-").map(Number);
  const date = new Date(Date.UTC(nam, thang - 1, ngayTrongThang));

  return (
    date.getUTCFullYear() === nam &&
    date.getUTCMonth() === thang - 1 &&
    date.getUTCDate() === ngayTrongThang
  );
}

// Lấy danh sách ngày có tin nhắn trong 3 tháng gần nhất
async function layDanhSachNgay(req, res) {
  try {
    const [rows] = await ketNoi.query(`
            SELECT DISTINCT DATE_FORMAT(tao_luc, '%Y-%m-%d') AS ngay
            FROM tin_nhan_cong_dong
            WHERE tao_luc >= DATE_SUB(CURDATE(), INTERVAL 3 MONTH)
              AND trang_thai = 'hien_thi'
            ORDER BY ngay DESC
        `);

    const homNay = new Date().toLocaleDateString("en-CA", {
      timeZone: "Asia/Ho_Chi_Minh",
    });

    const ngayHomQua = new Date(
      new Date(homNay + "T00:00:00+07:00").getTime() - 24 * 60 * 60 * 1000,
    )
      .toISOString()
      .slice(0, 10);

    const danhSachNgay = [
      ...new Set([homNay, ngayHomQua, ...rows.map((item) => item.ngay)]),
    ];

    return res.json({
      thanh_cong: true,
      thong_bao: "Lấy danh sách ngày thành công",
      data: danhSachNgay,
    });
  } catch (error) {
    console.error("Lỗi lấy ngày cộng đồng:", error);

    return res.status(500).json({
      thanh_cong: false,
      thong_bao: "Không thể lấy danh sách ngày",
    });
  }
}

// Lấy tin nhắn theo ngày, có phân trang
async function layTinNhan(req, res) {
  try {
    const { ngay } = req.query;

    if (!ngay || !ngayHopLe(ngay)) {
      return res.status(400).json({
        thanh_cong: false,
        thong_bao: "Ngày không hợp lệ",
      });
    }

    const gioiHan = Math.min(
      Math.max(parseInt(req.query.limit, 10) || 30, 1),
      100,
    );

    const truocId = parseInt(req.query.before_id, 10);

    const coTruocId = Number.isSafeInteger(truocId) && truocId > 0;

    let sql = `
            SELECT
                tn.id,
                tn.nguoi_dung_id,
                nd.ten_hien_thi,
                nd.avatar_url,
                tn.noi_dung,
                DATE_FORMAT(tn.tao_luc, '%Y-%m-%d %H:%i:%s') AS tao_luc
            FROM tin_nhan_cong_dong tn
            INNER JOIN nguoi_dung nd
                ON tn.nguoi_dung_id = nd.id
            WHERE DATE(tn.tao_luc) = ?
              AND tn.trang_thai = 'hien_thi'
              AND nd.da_xoa = 0
        `;

    const thamSo = [ngay];

    if (coTruocId) {
      sql += " AND tn.id < ?";
      thamSo.push(truocId);
    }

    sql += " ORDER BY tn.id DESC LIMIT ?";
    thamSo.push(gioiHan);

    const [rows] = await ketNoi.query(sql, thamSo);

    // Đảo lại để hiển thị từ cũ đến mới
    rows.reverse();

    return res.json({
      thanh_cong: true,
      thong_bao: "Lấy tin nhắn thành công",
      data: {
        danh_sach: rows,
        co_the_tai_them: rows.length === gioiHan,
        tin_nhan_cu_nhat_id: rows.length > 0 ? rows[0].id : null,
      },
    });
  } catch (error) {
    console.error("Lỗi lấy tin nhắn:", error);

    return res.status(500).json({
      thanh_cong: false,
      thong_bao: "Không thể lấy tin nhắn",
    });
  }
}

// Gửi tin nhắn
async function guiTinNhan(req, res) {
  try {
    const nguoiDungId = req.nguoiDung.id;
    const noiDung =
      typeof req.body.noi_dung === "string" ? req.body.noi_dung.trim() : "";

    if (!noiDung) {
      return res.status(400).json({
        thanh_cong: false,
        thong_bao: "Nội dung tin nhắn không được để trống",
      });
    }

    if (noiDung.length > 1000) {
      return res.status(400).json({
        thanh_cong: false,
        thong_bao: "Tin nhắn không được vượt quá 1000 ký tự",
      });
    }

    // Kiểm tra tài khoản còn hoạt động
    const [nguoiDung] = await ketNoi.query(
      `SELECT id FROM nguoi_dung
             WHERE id = ? AND da_xoa = 0`,
      [nguoiDungId],
    );

    if (nguoiDung.length === 0) {
      return res.status(403).json({
        thanh_cong: false,
        thong_bao: "Tài khoản không còn hoạt động",
      });
    }

    // Lưu tin nhắn vào database
    const [ketQua] = await ketNoi.query(
      `INSERT INTO tin_nhan_cong_dong
                (nguoi_dung_id, noi_dung)
             VALUES (?, ?)`,
      [nguoiDungId, noiDung],
    );

    // Lấy lại tin nhắn vừa lưu cùng thông tin người gửi
    const [rows] = await ketNoi.query(
      `
            SELECT
                tn.id,
                tn.nguoi_dung_id,
                nd.ten_hien_thi,
                nd.avatar_url,
                tn.noi_dung,
                DATE_FORMAT(tn.tao_luc, '%Y-%m-%d %H:%i:%s') AS tao_luc
            FROM tin_nhan_cong_dong tn
            INNER JOIN nguoi_dung nd
                ON tn.nguoi_dung_id = nd.id
            WHERE tn.id = ?
        `,
      [ketQua.insertId],
    );

    const tinNhan = rows[0];

    // Phát tin nhắn đến tất cả thành viên đang online
    const io = req.app.get("io");

    if (io) {
      io.to("cong_dong").emit("tin_nhan_moi", tinNhan);
    }

    return res.status(201).json({
      thanh_cong: true,
      thong_bao: "Gửi tin nhắn thành công",
      data: tinNhan,
    });
  } catch (error) {
    console.error("Lỗi gửi tin nhắn:", error);

    return res.status(500).json({
      thanh_cong: false,
      thong_bao: "Không thể gửi tin nhắn",
    });
  }
}

module.exports = {
  layDanhSachNgay,
  layTinNhan,
  guiTinNhan,
};
