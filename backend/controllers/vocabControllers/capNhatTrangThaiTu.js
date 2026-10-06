const db = require("../../config/database");


const capNhatTrangThaiTu = async (req, res) => {

    try {

        const nguoiDungId =
            req.nguoiDung.id;

        const boTuId =
            req.params.boTuId;

        const tuVungId =
            req.params.tuVungId;


        const {
            da_thuoc
        } = req.body || {};


        // Chỉ chấp nhận 0 hoặc 1
        if (
            da_thuoc !== 0 &&
            da_thuoc !== 1
        ) {

            return res.status(400).json({
                thanh_cong: false,
                thong_bao:
                    "Trạng thái không hợp lệ"
            });
        }


        // Kiểm tra từ có thuộc bộ từ của user không
        // Đồng thời lấy trạng thái hiện tại
        const [danhSachTu] =
            await db.query(
                `
                SELECT
                    tu_vung.id,
                    tu_vung.da_thuoc

                FROM tu_vung

                INNER JOIN bo_tu
                    ON tu_vung.bo_tu_id =
                       bo_tu.id

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


        // Không tìm thấy từ
        if (danhSachTu.length === 0) {

            return res.status(404).json({
                thanh_cong: false,
                thong_bao:
                    "Từ vựng không tồn tại"
            });
        }


        // Lấy trạng thái trước khi cập nhật
        const trangThaiCu =
            danhSachTu[0].da_thuoc;


        // Cập nhật trạng thái
        await db.query(
            `
            UPDATE tu_vung

            SET
                da_thuoc = ?,

                ngay_da_thuoc =
                    CASE

                        -- Chưa thuộc -> Đã thuộc
                        -- Bắt đầu tính ngày ôn
                        WHEN ? = 1
                            AND ? = 0
                            THEN NOW()

                        -- Đã thuộc -> Chưa thuộc
                        -- Xóa ngày đã thuộc
                        WHEN ? = 0
                            THEN NULL

                        -- Trạng thái không đổi
                        -- Giữ nguyên ngày cũ
                        ELSE ngay_da_thuoc

                    END

            WHERE id = ?

            AND bo_tu_id = ?
            `,
            [
                da_thuoc,
                da_thuoc,
                trangThaiCu,
                da_thuoc,
                tuVungId,
                boTuId
            ]
        );


        return res.status(200).json({
            thanh_cong: true,

            thong_bao:
                da_thuoc === 1
                    ? "Đã đánh dấu từ là đã thuộc"
                    : "Đã đánh dấu từ là chưa thuộc",

            data: {
                id: Number(tuVungId),
                bo_tu_id: Number(boTuId),
                da_thuoc: da_thuoc
            }
        });


    } catch (error) {

        console.error(
            "Lỗi cập nhật trạng thái từ:",
            error
        );


        return res.status(500).json({
            thanh_cong: false,
            thong_bao:
                "Lỗi server"
        });
    }
};


module.exports =
    capNhatTrangThaiTu;