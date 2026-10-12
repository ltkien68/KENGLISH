const { GoogleGenAI } = require("@google/genai");

const ai = process.env.GEMINI_API_KEY
    ? new GoogleGenAI({
        apiKey: process.env.GEMINI_API_KEY
    })
    : null;

const DANH_SACH_MODEL = {
    flash: process.env.GEMINI_MODEL || "gemini-3.8-flash",
    "flash-lite": process.env.GEMINI_FALLBACK_MODEL || "gemini-3.1-flash-lite"
};

const cho = (ms) =>
    new Promise(resolve => setTimeout(resolve, ms));

const layDanhSachModel = (luaChon = "auto") => {
    if (!["auto", "flash", "flash-lite"].includes(luaChon)) {
        const error = new Error("KAI_INVALID_MODEL");
        error.status = 400;
        throw error;
    }

    if (luaChon === "flash") {
        return [DANH_SACH_MODEL.flash];
    }

    if (luaChon === "flash-lite") {
        return [DANH_SACH_MODEL["flash-lite"]];
    }

    return [
        ...new Set([
            DANH_SACH_MODEL.flash,
            DANH_SACH_MODEL["flash-lite"]
        ])
    ];
};

const goiGeminiKai = async ({
    luaChon = "auto",
    contents,
    config
}) => {
    if (!ai) {
        const error = new Error("GEMINI_API_KEY_NOT_CONFIGURED");
        error.status = 503;
        throw error;
    }

    const danhSachModel = layDanhSachModel(luaChon);
    let loiCuoi = null;

    for (const model of danhSachModel) {
        for (let lan = 1; lan <= 2; lan++) {
            try {
                console.log(`[Kai] Model: ${model}, lần ${lan}`);

                const response = await ai.models.generateContent({
                    model,
                    contents,
                    config
                });

                console.log(`[Kai] Thành công: ${model}`);
                return response;

            } catch (error) {
                loiCuoi = error;

                const maLoi = Number(
                    error.status || error.code
                );

                const loiTamThoi = [
                    429, 500, 502, 503, 504
                ].includes(maLoi);

                console.error(
                    `[Kai] ${model} lỗi ${maLoi || "không xác định"}`
                );

                if (!loiTamThoi) {
                    throw error;
                }

                if (lan < 2) {
                    await cho(1000);
                }
            }
        }
    }

    throw loiCuoi || new Error("GEMINI_UNAVAILABLE");
};

module.exports = {
    goiGeminiKai,
    layDanhSachModel
};