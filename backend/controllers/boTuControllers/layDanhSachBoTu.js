
const ketNoi = require("../../config/database");

const layDanhSachBoTu = async (req, res) => {

    try {

        const nguoiDungId =
            req.nguoiDung.id;

        const layTatCa =
            req.query.tat_ca === "true";

        let dieuKienFolder = "";

        // Mặc định chỉ lấy bộ từ ngoài folder.
        // Luyện tập truyền tat_ca=true để lấy toàn bộ.
        if (!layTatCa) {
            dieuKienFolder =
                "AND bt.folder_id IS NULL";
        }

        const [boTu] =
            await ketNoi.query(
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
                            WHEN tv.da_thuoc = 1
                            THEN 1
                            ELSE 0
                        END
                    ) AS so_tu_da_thuoc

                FROM bo_tu bt

                LEFT JOIN tu_vung tv
                    ON tv.bo_tu_id = bt.id

                WHERE bt.nguoi_dung_id = ?

                ${dieuKienFolder}

                GROUP BY
                    bt.id,
                    bt.ten_bo_tu,
                    bt.mo_ta,
                    bt.folder_id,
                    bt.ngay_tao

                ORDER BY bt.ngay_tao DESC
                `,
                [nguoiDungId]
            );

        return res.status(200).json({
            thanh_cong: true,
            data: boTu
        });

    } catch (error) {

        console.error(
            "Lỗi lấy bộ từ:",
            error
        );

        return res.status(500).json({
            thanh_cong: false,
            thong_bao: "Lỗi server"
        });
    }
};

module.exports = layDanhSachBoTu;
