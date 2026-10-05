const ketNoi = require("../../config/database");

const chuyenBoTuVaoFolder = async (req, res) => {

    try {

        const nguoiDungId =
            req.nguoiDung.id;

        const boTuId =
            req.params.boTuId;

        const { folder_id } =
            req.body;


        if (!folder_id) {

            return res.status(400).json({
                thanh_cong: false,
                thong_bao: "Vui lòng chọn thư mục"
            });
        }


        /*
         * Kiểm tra folder có thực sự
         * thuộc user đang đăng nhập không.
         */
        const [folders] =
            await ketNoi.query(
                `
                SELECT id
                FROM folder
                WHERE id = ?
                AND nguoi_dung_id = ?
                `,
                [
                    folder_id,
                    nguoiDungId
                ]
            );


        if (folders.length === 0) {

            return res.status(404).json({
                thanh_cong: false,
                thong_bao: "Không tìm thấy thư mục"
            });
        }


        /*
         * Chuyển bộ từ vào folder.
         *
         * Điều kiện nguoi_dung_id giúp user
         * không thể sửa bộ từ của người khác.
         */
        const [ketQua] =
            await ketNoi.query(
                `
                UPDATE bo_tu
                SET folder_id = ?
                WHERE id = ?
                AND nguoi_dung_id = ?
                `,
                [
                    folder_id,
                    boTuId,
                    nguoiDungId
                ]
            );


        if (ketQua.affectedRows === 0) {

            return res.status(404).json({
                thanh_cong: false,
                thong_bao: "Không tìm thấy bộ từ"
            });
        }


        return res.status(200).json({
            thanh_cong: true,
            thong_bao: "Đã chuyển bộ từ vào thư mục"
        });


    } catch (error) {

        console.error(
            "Lỗi chuyển bộ từ vào folder:",
            error
        );

        return res.status(500).json({
            thanh_cong: false,
            thong_bao: "Lỗi server"
        });
    }
};

module.exports =
    chuyenBoTuVaoFolder;