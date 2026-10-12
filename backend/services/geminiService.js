const {
    goiGeminiKai
} = require("./kaiModelService");

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

const guiCauHoiGemini = async (cauHoi, luaChon = "auto") => {
    if (
        typeof cauHoi !== "string" ||
        !cauHoi.trim() ||
        cauHoi.length > 2000
    ) {
        throw new Error("KAI_INVALID_QUESTION");
    }

    const response = await goiGeminiKai({
        luaChon,
        contents: cauHoi.trim(),
        config: {
            systemInstruction: SYSTEM_INSTRUCTION,
            temperature: 0.7,
            maxOutputTokens: 2048
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