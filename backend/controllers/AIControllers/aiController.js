const { guiCauHoiGemini } = require("../../services/GeminiService");

const chatAI = async (req, res) => {
    try {
        const { cau_hoi } = req.body || {};

        if (typeof cau_hoi !== "string" || !cau_hoi.trim()) {
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

        const traLoi = await guiCauHoiGemini(cau_hoi.trim());

        return res.status(200).json({
            success: true,
            message: "AI trả lời thành công",
            data: {
                cau_hoi: cau_hoi.trim(),
                tra_loi: traLoi
            }
        });
    } catch (error) {
        console.error("Loi Gemini:", error.message);

        if (error.message === "GEMINI_API_KEY_NOT_CONFIGURED") {
            return res.status(503).json({
                success: false,
                message: "Dịch vụ AI chưa được cấu hình"
            });
        }

        const status = error.status || error.statusCode;

        if (status === 429) {
            return res.status(429).json({
                success: false,
                message: "AI đã đạt giới hạn yêu cầu. Vui lòng thử lại sau."
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