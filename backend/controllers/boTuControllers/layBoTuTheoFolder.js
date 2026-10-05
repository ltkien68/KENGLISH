const ketNoi = require("../../config/database");

const layBoTuTheoFolder = async (req, res) => {
  try {
    const nguoiDungId = req.nguoiDung.id;
    const folderId = req.params.folderId;

    // Kiểm tra folder có thuộc user không
    const [folder] = await ketNoi.query(
      `
            SELECT
                id,
                ten_folder

            FROM folder

            WHERE id = ?
            AND nguoi_dung_id = ?
            `,
      [folderId, nguoiDungId],
    );

    if (folder.length === 0) {
      return res.status(404).json({
        thanh_cong: false,
        thong_bao: "Folder không tồn tại",
      });
    }

    // Lấy các bộ từ trong folder
    const [boTu] = await ketNoi.query(
        `
        SELECT
            bt.id,
            bt.ten_bo_tu,
            bt.mo_ta,
            bt.folder_id,
            bt.ngay_tao,

            COUNT(tv.id) AS so_luong_tu,

            SUM(
                CASE
                    WHEN tv.da_thuoc = 1 THEN 1
                    ELSE 0
                END
            ) AS so_tu_da_thuoc

        FROM bo_tu bt

        LEFT JOIN tu_vung tv
            ON tv.bo_tu_id = bt.id

        WHERE bt.folder_id = ?
        AND bt.nguoi_dung_id = ?

        GROUP BY
            bt.id,
            bt.ten_bo_tu,
            bt.mo_ta,
            bt.folder_id,
            bt.ngay_tao

        ORDER BY bt.ngay_tao DESC
        `,
      [folderId, nguoiDungId],
    );

    return res.status(200).json({
      thanh_cong: true,

      data: {
        folder: folder[0],
        bo_tu: boTu,
      },
    });
  } catch (error) {
    console.error("Lỗi lấy bộ từ trong folder:", error);

    return res.status(500).json({
      thanh_cong: false,
      thong_bao: "Lỗi server",
    });
  }
};

module.exports = layBoTuTheoFolder;
