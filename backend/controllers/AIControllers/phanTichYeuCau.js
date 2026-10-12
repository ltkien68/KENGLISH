const {
    phanTichYeuCauKai
} = require("../../services/KaiIntentService");

const phanTichYeuCau = async (req, res) => {
    try {
        const { cau_hoi } = req.body || {};

        if (
            typeof cau_hoi !== "string" ||
            !cau_hoi.trim() ||
            cau_hoi.length > 2000
        ) {
            return res.status(400).json({
                thanh_cong: false,
                thong_bao: "Câu hỏi không hợp lệ"
            });
        }

        const duLieu = await phanTichYeuCauKai(
            cau_hoi.trim()
        );

        return res.json({
            thanh_cong: true,
            thong_bao: "Phân tích thành công",
            data: duLieu
        });

    } catch (error) {
        console.error("Lỗi phân tích Kai:", error);

        if (error.message === "GEMINI_API_KEY_NOT_CONFIGURED") {
            return res.status(503).json({
                thanh_cong: false,
                thong_bao: "Chưa cấu hình Gemini API"
            });
        }

        if (error.status === 429 || error.code === 429) {
            return res.status(429).json({
                thanh_cong: false,
                thong_bao: "Gemini đã đạt giới hạn yêu cầu"
            });
        }

        return res.status(502).json({
            thanh_cong: false,
            thong_bao: "Kai chưa phân tích được yêu cầu"
        });
    }
};

module.exports = phanTichYeuCau;