const {
    guiCauHoiGemini
} = require("../../services/GeminiService");

const DANH_SACH_MODEL = [
    "auto",
    "flash",
    "flash-lite"
];

const chatAI = async (req, res) => {
    try {
        const {
            cau_hoi,
            model = "auto"
        } = req.body || {};

        if (
            typeof cau_hoi !== "string" ||
            !cau_hoi.trim()
        ) {
            return res.status(400).json({
                success: false,
                message: "Vui lòng nhập câu hỏi"
            });
        }

        if (cau_hoi.length > 2000) {
            return res.status(400).json({
                success: false,
                message: "Câu hỏi không được vượt quá 2000 ký tự"
            });
        }

        if (
            typeof model !== "string" ||
            !DANH_SACH_MODEL.includes(model)
        ) {
            return res.status(400).json({
                success: false,
                message: "Mô hình AI không hợp lệ"
            });
        }

        console.log("========== KAI CHAT ==========");
        console.log("Model được chọn:", model);

        const traLoi = await guiCauHoiGemini(
            cau_hoi.trim(),
            model
        );

        return res.status(200).json({
            success: true,
            message: "AI trả lời thành công",
            data: {
                cau_hoi: cau_hoi.trim(),
                tra_loi: traLoi
            }
        });

    } catch (error) {
        console.error("Lỗi Gemini:", error);

        if (error.message === "GEMINI_API_KEY_NOT_CONFIGURED") {
            return res.status(503).json({
                success: false,
                message: "Dịch vụ AI chưa được cấu hình"
            });
        }

        if (
            error.message === "KAI_INVALID_MODEL" ||
            error.message === "KAI_INVALID_QUESTION"
        ) {
            return res.status(400).json({
                success: false,
                message: "Yêu cầu hoặc mô hình AI không hợp lệ"
            });
        }

        if (error.message === "GEMINI_EMPTY_RESPONSE") {
            return res.status(502).json({
                success: false,
                message: "Gemini không trả về nội dung"
            });
        }

        const status = Number(
            error.status || error.statusCode || error.code
        );

        if (status === 429) {
            return res.status(429).json({
                success: false,
                message: "Các mô hình AI khả dụng đã đạt giới hạn yêu cầu"
            });
        }

        if ([500, 502, 503, 504].includes(status)) {
            return res.status(503).json({
                success: false,
                message: "Dịch vụ Gemini đang quá tải, vui lòng thử lại sau"
            });
        }

        if (status === 400 || status === 404) {
            return res.status(502).json({
                success: false,
                message: "Yêu cầu Gemini không hợp lệ hoặc model không khả dụng"
            });
        }

        return res.status(502).json({
            success: false,
            message: "Không thể kết nối dịch vụ AI"
        });
    }
};

module.exports = {
    chatAI
};