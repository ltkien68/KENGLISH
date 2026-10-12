const {
    phanTichYeuCauKai
} = require("../../services/KaiIntentService");

const DANH_SACH_MODEL = [
    "auto",
    "flash",
    "flash-lite"
];

const phanTichYeuCau = async (req, res) => {
    try {
        const {
            cau_hoi,
            model = "auto"
        } = req.body || {};

        // Kiểm tra nội dung yêu cầu
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

        // Chỉ cho phép các model đã định nghĩa
        if (
            typeof model !== "string" ||
            !DANH_SACH_MODEL.includes(model)
        ) {
            return res.status(400).json({
                thanh_cong: false,
                thong_bao: "Mô hình AI không hợp lệ"
            });
        }

        console.log("========== KAI INTENT ==========");
        console.log("Câu hỏi:", cau_hoi.trim());
        console.log("Model được chọn:", model);

        // Truyền model từ Android xuống service
        const duLieu = await phanTichYeuCauKai(
            cau_hoi.trim(),
            model
        );

        return res.status(200).json({
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

        if (
            error.message === "KAI_INVALID_MODEL" ||
            error.message === "KAI_INVALID_QUESTION"
        ) {
            return res.status(400).json({
                thanh_cong: false,
                thong_bao: "Yêu cầu hoặc mô hình AI không hợp lệ"
            });
        }

        if (
            error.message === "KAI_INVALID_JSON" ||
            error.message === "KAI_INVALID_INTENT"
        ) {
            return res.status(502).json({
                thanh_cong: false,
                thong_bao: "Gemini trả về dữ liệu không hợp lệ"
            });
        }

        const maLoi = Number(
            error.status || error.code
        );

        if (maLoi === 429) {
            return res.status(429).json({
                thanh_cong: false,
                thong_bao: "Các mô hình AI khả dụng đã đạt giới hạn yêu cầu"
            });
        }

        if ([500, 502, 503, 504].includes(maLoi)) {
            return res.status(503).json({
                thanh_cong: false,
                thong_bao: "Dịch vụ Gemini đang quá tải, vui lòng thử lại sau"
            });
        }

        return res.status(502).json({
            thanh_cong: false,
            thong_bao: "Kai chưa phân tích được yêu cầu"
        });
    }
};

module.exports = phanTichYeuCau;