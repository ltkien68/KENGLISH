const db = require("../../config/database");

const {
    taoDanhSachTuAI
} = require("../../services/kaiVocabularyService");

const taoBoTuBangAI = async (req, res) => {
    const nguoiDungId = req.nguoiDung.id;

    const {
        chu_de,
        so_luong = 5
    } = req.body || {};

    if (
        typeof chu_de !== "string" ||
        !chu_de.trim() ||
        chu_de.trim().length > 100
    ) {
        return res.status(400).json({
            thanh_cong: false,
            thong_bao: "Chủ đề phải có từ 1 đến 100 ký tự"
        });
    }

    if (
        !Number.isInteger(so_luong) ||
        so_luong < 1 ||
        so_luong > 10
    ) {
        return res.status(400).json({
            thanh_cong: false,
            thong_bao: "Số lượng từ phải từ 1 đến 10"
        });
    }

    let ketNoi = null;

    try {
        // Gọi Gemini trước, tránh giữ transaction
        // trong thời gian chờ AI phản hồi.
        const duLieuAI = await taoDanhSachTuAI(
            chu_de.trim(),
            so_luong
        );

        ketNoi = await db.getConnection();

        await ketNoi.beginTransaction();

        const [ketQuaBoTu] = await ketNoi.query(
            `
            INSERT INTO bo_tu (
                nguoi_dung_id,
                folder_id,
                ten_bo_tu,
                mo_ta
            )
            VALUES (?, NULL, ?, ?)
            `,
            [
                nguoiDungId,
                duLieuAI.ten_bo_tu,
                duLieuAI.mo_ta
            ]
        );

        const boTuId = ketQuaBoTu.insertId;

        for (const tu of duLieuAI.danh_sach_tu) {
            await ketNoi.query(
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
                    tu.tu_goc,
                    tu.phien_am,
                    tu.loai_tu,
                    tu.nghia_tieng_viet,
                    tu.cau_vi_du,
                    tu.tu_dong_nghia,
                    tu.tu_trai_nghia
                ]
            );
        }

        await ketNoi.commit();

        return res.status(201).json({
            thanh_cong: true,
            thong_bao: "Kai đã tạo bộ từ thành công",
            data: {
                bo_tu_id: boTuId,
                ten_bo_tu: duLieuAI.ten_bo_tu,
                mo_ta: duLieuAI.mo_ta,
                so_tu: duLieuAI.danh_sach_tu.length,
                danh_sach_tu: duLieuAI.danh_sach_tu,
                hanh_dong_tiep_theo: "mo_luyen_tap"
            }
        });

    } catch (error) {
        if (ketNoi) {
            try {
                await ketNoi.rollback();
            } catch (rollbackError) {
                console.error(
                    "Lỗi rollback:",
                    rollbackError
                );
            }
        }

        console.error("Lỗi Kai tạo bộ từ:", error);

        if (error.message === "GEMINI_API_KEY_NOT_CONFIGURED") {
            return res.status(503).json({
                thanh_cong: false,
                thong_bao: "Gemini API chưa được cấu hình"
            });
        }

        if (
            error.message === "GEMINI_INVALID_JSON" ||
            error.message === "GEMINI_INVALID_VOCABULARY" ||
            error.message === "GEMINI_INSUFFICIENT_VOCABULARY" ||
            error.message === "GEMINI_EMPTY_RESPONSE"
        ) {
            return res.status(502).json({
                thanh_cong: false,
                thong_bao: "Kai chưa tạo được danh sách từ hợp lệ"
            });
        }

        if (error.status === 429 || error.code === 429) {
            return res.status(429).json({
                thanh_cong: false,
                thong_bao: "Gemini đã đạt giới hạn yêu cầu"
            });
        }

        return res.status(500).json({
            thanh_cong: false,
            thong_bao: "Không thể tạo bộ từ bằng Kai"
        });

    } finally {
        if (ketNoi) {
            ketNoi.release();
        }
    }
};

module.exports = taoBoTuBangAI;