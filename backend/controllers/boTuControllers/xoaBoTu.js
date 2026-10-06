const db = require("../../config/database");


const xoaBoTu = async (req, res) => {
    try {
        const nguoiDungId =
            req.nguoiDung.id;

        const boTuId =
            req.params.boTuId;
        /*
         * Kiểm tra bộ từ có tồn tại
         * và thuộc user hiện tại.
         */
        const [danhSachBoTu] =
            await db.query(
                `
                SELECT id
                FROM bo_tu

                WHERE id = ?
                AND nguoi_dung_id = ?
                `,
                [
                    boTuId,
                    nguoiDungId
                ]
            );

        if (danhSachBoTu.length === 0) {
            return res.status(404).json({
                thanh_cong: false,
                thong_bao: "Bộ từ không tồn tại"
            });
        }
        /*
         * Xóa bộ từ.
         *
         * Các từ thuộc bộ từ sẽ tự động
         * bị xóa nhờ ON DELETE CASCADE.
         */
        await db.query(
            `
            DELETE FROM bo_tu

            WHERE id = ?
            AND nguoi_dung_id = ?
            `,
            [
                boTuId,
                nguoiDungId
            ]
        );

        return res.status(200).json({
            thanh_cong: true,
            thong_bao: "Xóa bộ từ thành công"
        });


    } catch (error) {
        console.error(
            "Lỗi xóa bộ từ:",
            error
        );

        return res.status(500).json({
            thanh_cong: false,
            thong_bao: "Lỗi server"
        });
    }
};


module.exports = xoaBoTu;