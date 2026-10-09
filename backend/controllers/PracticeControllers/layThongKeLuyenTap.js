const db = require("../../config/database");

const layThongKeLuyenTap = async (req, res) => {
  try {
    const nguoiDungId = req.nguoiDung.id;

    const [nguoiDungRows] = await db.query(
      `
    SELECT so_luot_choi
    FROM nguoi_dung
    WHERE id = ?
    `,
      [nguoiDungId],
    );

    const [tongQuanRows] = await db.query(
      `
      SELECT
        COUNT(*) AS so_lan_luyen_tap,

        COALESCE(
          SUM(tong_so_cau),
          0
        ) AS tong_so_cau,

        COALESCE(
          SUM(so_dung),
          0
        ) AS tong_so_dung,

        COALESCE(
          SUM(so_sai),
          0
        ) AS tong_so_sai,

        COALESCE(
          ROUND(
            SUM(so_dung) * 100.0 /
            NULLIF(SUM(tong_so_cau), 0),
            0
          ),
          0
        ) AS do_chinh_xac_tb

      FROM lich_su_luyen_tap
      WHERE nguoi_dung_id = ?
      `,
      [nguoiDungId],
    );

    const [theoCheDo] = await db.query(
      `
      SELECT
        che_do,
        COUNT(*) AS so_lan

      FROM lich_su_luyen_tap
      WHERE nguoi_dung_id = ?

      GROUP BY che_do
      ORDER BY so_lan DESC, che_do ASC
      `,
      [nguoiDungId],
    );

    return res.status(200).json({
      thanh_cong: true,
      thong_bao: "Lấy thống kê luyện tập thành công",
      data: {
        ...tongQuanRows[0],
        theo_che_do: theoCheDo,
        so_luot_choi: nguoiDungRows[0]?.so_luot_choi ?? 0,
      },
    });
  } catch (error) {
    console.error("Lỗi thống kê luyện tập:", error);

    return res.status(500).json({
      thanh_cong: false,
      thong_bao: "Không thể lấy thống kê luyện tập",
    });
  }
};

module.exports = layThongKeLuyenTap;
