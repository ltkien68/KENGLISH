const { GoogleGenAI } = require("@google/genai");

const ai = process.env.GEMINI_API_KEY
    ? new GoogleGenAI({
        apiKey: process.env.GEMINI_API_KEY
    })
    : null;

// KAI chỉ sử dụng duy nhất Gemini 3.1 Flash-Lite
const MODEL_KAI = "gemini-3.1-flash-lite";

const cho = (ms) =>
    new Promise(resolve => setTimeout(resolve, ms));

const goiGeminiKai = async ({
    contents,
    config
}) => {
    if (!ai) {
        const error = new Error("GEMINI_API_KEY_NOT_CONFIGURED");
        error.status = 503;
        throw error;
    }

    if (
        typeof contents !== "string" ||
        !contents.trim()
    ) {
        const error = new Error("KAI_INVALID_QUESTION");
        error.status = 400;
        throw error;
    }

    let loiCuoi = null;

    // Tối đa 2 lần gọi, luôn sử dụng cùng một model
    for (let lan = 1; lan <= 2; lan++) {
        try {
            console.log(`[Kai] Model: ${MODEL_KAI}, lần ${lan}`);

            const response = await ai.models.generateContent({
                model: MODEL_KAI,
                contents,
                config
            });

            console.log(`[Kai] Thành công: ${MODEL_KAI}`);

            return response;

        } catch (error) {
            loiCuoi = error;

            const maLoi = Number(
                error.status || error.statusCode || error.code
            );

            const loiTamThoi = [
                429, 500, 502, 503, 504
            ].includes(maLoi);

            console.error(
                `[Kai] ${MODEL_KAI} lỗi ${maLoi || "không xác định"}`
            );

            // Lỗi không thể phục hồi: dừng ngay
            if (!loiTamThoi) {
                throw error;
            }

            // Chỉ thử lại chính model Gemini 3.1 Flash-Lite
            if (lan < 2) {
                await cho(1000);
            }
        }
    }

    throw loiCuoi || new Error("GEMINI_UNAVAILABLE");
};

module.exports = {
    goiGeminiKai
};
