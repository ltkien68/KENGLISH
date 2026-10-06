const db = require("../../config/database");


const layThongKeHocTap = async (req, res) => {

    try {

        const nguoiDungId =
            req.nguoiDung.id;


        const [rows] =
            await db.query(
                `
                SELECT
                    COUNT(tu_vung.id)
                        AS tong_tu,

                    SUM(
                        CASE
                            WHEN tu_vung.da_thuoc = 1
                            THEN 1
                            ELSE 0
                        END
                    ) AS da_thuoc,

                    SUM(
                        CASE
                            WHEN tu_vung.da_thuoc = 1
                            AND tu_vung.ngay_da_thuoc
                                <= NOW() - INTERVAL 3 DAY
                            THEN 1
                            ELSE 0
                        END
                    ) AS tu_den_han

                FROM bo_tu

                LEFT JOIN tu_vung
                    ON tu_vung.bo_tu_id = bo_tu.id

                WHERE bo_tu.nguoi_dung_id = ?
                `,
                [nguoiDungId]
            );


        const tongTu =
            Number(rows[0].tong_tu) || 0;

        const daThuoc =
            Number(rows[0].da_thuoc) || 0;

        const tuDenHan =
            Number(rows[0].tu_den_han) || 0;


        const tienDo =
            tongTu > 0
                ? Math.round(
                    (daThuoc / tongTu) * 100
                )
                : 0;


        return res.status(200).json({
            thanh_cong: true,

            data: {
                tong_tu: tongTu,
                da_thuoc: daThuoc,
                tien_do: tienDo,
                tu_den_han: tuDenHan
            }
        });


    } catch (error) {

        console.error(
            "Lỗi lấy thống kê học tập:",
            error
        );


        return res.status(500).json({
            thanh_cong: false,
            thong_bao: "Lỗi server"
        });
    }
};


module.exports =
    layThongKeHocTap;