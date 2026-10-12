const { GoogleGenAI } = require("@google/genai");

const apiKey = process.env.GEMINI_API_KEY;

if (!apiKey) {
    console.warn("Chua cau hinh GEMINI_API_KEY trong .env");
}

const ai = apiKey ? new GoogleGenAI({ apiKey }) : null;

const SYSTEM_INSTRUCTION = `
Bạn là Kenglish AI, trợ lý học tiếng Anh của ứng dụng Kenglish.

Nhiệm vụ:
- Giải thích từ vựng tiếng Anh.
- Giải thích ngữ pháp dễ hiểu.
- Đưa ra ví dụ tiếng Anh và nghĩa tiếng Việt.
- Hỗ trợ học phát âm, ghi nhớ từ vựng.
- Gợi ý phương pháp học tiếng Anh hiệu quả.
- Giải đáp thắc mắc liên quan đến việc học tiếng Anh.

Quy tắc:
- Ưu tiên trả lời bằng tiếng Việt.
- Trả lời rõ ràng, thân thiện, chính xác.
- Không giải thích quá dài nếu người dùng không yêu cầu.
- Khi giải thích từ vựng, ưu tiên đưa ví dụ thực tế.
- Không tự nhận đã tạo, sửa hoặc xóa dữ liệu trong Kenglish.
- Không giả vờ có quyền truy cập dữ liệu cá nhân của người dùng.
`;

const guiCauHoiGemini = async (cauHoi) => {
    if (!ai) {
        throw new Error("GEMINI_API_KEY_NOT_CONFIGURED");
    }

    const response = await ai.models.generateContent({
        model: process.env.GEMINI_MODEL || "gemini-2.5-flash",
        contents: cauHoi,
        config: {
            systemInstruction: SYSTEM_INSTRUCTION,
            temperature: 0.7,
            maxOutputTokens: 1024
        }
    });

    const traLoi = response.text;

    if (!traLoi || !traLoi.trim()) {
        throw new Error("GEMINI_EMPTY_RESPONSE");
    }

    return traLoi.trim();
};

module.exports = {
    guiCauHoiGemini
};