const db = require("../../config/database");

const themTu = async (req, res) => {
    try {

        const nguoiDungId = req.nguoiDung.id;
        const boTuId = req.params.boTuId;

        const {
            tu_goc,
            phien_am,
            loai_tu,
            nghia_tieng_viet,
            cau_vi_du,
            tu_dong_nghia,
            tu_trai_nghia
        } = req.body;


        // Kiểm tra dữ liệu bắt buộc
        if (!tu_goc || !tu_goc.trim()) {
            return res.status(400).json({
                thanh_cong: false,
                thong_bao: "Từ gốc không được để trống"
            });
        }

        if (!nghia_tieng_viet || !nghia_tieng_viet.trim()) {
            return res.status(400).json({
                thanh_cong: false,
                thong_bao: "Nghĩa tiếng Việt không được để trống"
            });
        }


        // Kiểm tra bộ từ có thuộc user hiện tại không
        const [boTu] = await db.query(
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


        if (boTu.length === 0) {
            return res.status(404).json({
                thanh_cong: false,
                thong_bao: "Bộ từ không tồn tại"
            });
        }


        // Thêm từ
        const [ketQua] = await db.query(
            `
            INSERT INTO tu_vung (
                bo_tu_id,
                tu_goc,
                phien_am,
                loai_tu,
                nghia_tieng_viet,
                cau_vi_du,
                tu_dong_nghia,
                tu_trai_nghia
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            `,
            [
                boTuId,
                tu_goc.trim(),
                phien_am?.trim() || null,
                loai_tu?.trim() || null,
                nghia_tieng_viet.trim(),
                cau_vi_du?.trim() || null,
                tu_dong_nghia?.trim() || null,
                tu_trai_nghia?.trim() || null
            ]
        );


        return res.status(201).json({
            thanh_cong: true,
            thong_bao: "Thêm từ thành công",

            data: {
                id: ketQua.insertId,
                bo_tu_id: Number(boTuId),
                tu_goc: tu_goc.trim(),
                phien_am: phien_am?.trim() || null,
                loai_tu: loai_tu?.trim() || null,
                nghia_tieng_viet: nghia_tieng_viet.trim(),
                cau_vi_du: cau_vi_du?.trim() || null,
                tu_dong_nghia: tu_dong_nghia?.trim() || null,
                tu_trai_nghia: tu_trai_nghia?.trim() || null,
                da_thuoc: 0
            }
        });

    } catch (error) {

        console.error("Lỗi thêm từ:", error);

        return res.status(500).json({
            thanh_cong: false,
            thong_bao: "Lỗi server"
        });
    }
};

module.exports = themTu;