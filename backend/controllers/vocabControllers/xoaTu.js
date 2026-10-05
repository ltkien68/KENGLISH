const db = require("../../config/database");


const xoaTu = async (req, res) => {

    try {

        const nguoiDungId =
            req.nguoiDung.id;

        const boTuId =
            req.params.boTuId;

        const tuVungId =
            req.params.tuVungId;


        /*
         * Kiểm tra từ có tồn tại
         * và bộ từ có thuộc user hiện tại.
         */
        const [danhSachTu] =
            await db.query(
                `
                SELECT tu_vung.id
                FROM tu_vung

                INNER JOIN bo_tu
                    ON tu_vung.bo_tu_id = bo_tu.id

                WHERE tu_vung.id = ?
                AND tu_vung.bo_tu_id = ?
                AND bo_tu.nguoi_dung_id = ?
                `,
                [
                    tuVungId,
                    boTuId,
                    nguoiDungId
                ]
            );


        if (danhSachTu.length === 0) {

            return res.status(404).json({
                thanh_cong: false,
                thong_bao: "Từ vựng không tồn tại"
            });
        }


        /*
         * Xóa từ.
         */
        await db.query(
            `
            DELETE FROM tu_vung
            WHERE id = ?
            AND bo_tu_id = ?
            `,
            [
                tuVungId,
                boTuId
            ]
        );


        return res.status(200).json({
            thanh_cong: true,
            thong_bao: "Xóa từ thành công"
        });


    } catch (error) {

        console.error(
            "Lỗi xóa từ:",
            error
        );


        return res.status(500).json({
            thanh_cong: false,
            thong_bao: "Lỗi server"
        });
    }
};


module.exports = xoaTu;