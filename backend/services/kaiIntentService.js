const { GoogleGenAI } = require("@google/genai");

const ai = process.env.GEMINI_API_KEY
  ? new GoogleGenAI({
      apiKey: process.env.GEMINI_API_KEY,
    })
  : null;

const phanTichYeuCauKai = async (cauHoi) => {
  if (!ai) {
    throw new Error("GEMINI_API_KEY_NOT_CONFIGURED");
  }

  const response = await ai.models.generateContent({
    model: process.env.GEMINI_INTENT_MODEL || "gemini-3.1-flash-lite",
    contents: cauHoi,
    config: {
      systemInstruction: `
Bạn là bộ phân tích ý định của trợ lý Kai trong ứng dụng Kenglish.

Chỉ phân loại yêu cầu, không thực hiện hành động.

Hành động được hỗ trợ:
- "tao_bo_tu": người dùng yêu cầu tạo một bộ từ mới.
- "chat": mọi trường hợp khác.

Nếu người dùng yêu cầu tạo bộ từ:
- Trích xuất chủ đề tiếng Anh cần học.
- Trích xuất số lượng từ, mặc định là 5.
- Số lượng phải là số nguyên từ 1 đến 10.
- Nếu người dùng yêu cầu nhiều hơn 10 từ, không tự giảm xuống 10.
- Xác định có yêu cầu mở luyện tập sau khi tạo hay không.
- Không tự nhận đã tạo dữ liệu.

Nếu người dùng chỉ hỏi cách tạo bộ từ hoặc hỏi về từ vựng,
phân loại là "chat".

Trả JSON có các trường:
{
  "hanh_dong": "tao_bo_tu",
  "chu_de": "Công nghệ thông tin",
  "so_luong": 5,
  "mo_luyen_tap": true
}

Với yêu cầu chat:
{
  "hanh_dong": "chat",
  "chu_de": "",
  "so_luong": 5,
  "mo_luyen_tap": false
}
`,
      responseMimeType: "application/json",
      temperature: 0.1,
      maxOutputTokens: 2048
    },
  });

  const noiDung = response.text || "";

  console.log("========== KAI INTENT ==========");
  console.log("Gemini response:", JSON.stringify(noiDung));
  console.log("Finish reason:", response.candidates?.[0]?.finishReason);
  console.log("================================");

  let duLieu;

  try {
    duLieu = JSON.parse(noiDung);
  } catch (error) {
    console.error("Gemini trả JSON không hợp lệ:", error.message);
    throw new Error("KAI_INVALID_JSON");
  }

  if (duLieu.hanh_dong !== "tao_bo_tu") {
    return {
      hanh_dong: "chat",
      chu_de: "",
      so_luong: 5,
      mo_luyen_tap: false,
    };
  }

  const chuDe = typeof duLieu.chu_de === "string" ? duLieu.chu_de.trim() : "";

  const soLuong = duLieu.so_luong;

  if (
    !chuDe ||
    chuDe.length > 100 ||
    !Number.isInteger(soLuong) ||
    soLuong < 1 ||
    soLuong > 10 ||
    typeof duLieu.mo_luyen_tap !== "boolean"
  ) {
    throw new Error("KAI_INVALID_INTENT");
  }

  return {
    hanh_dong: "tao_bo_tu",
    chu_de: chuDe,
    so_luong: soLuong,
    mo_luyen_tap: duLieu.mo_luyen_tap,
  };
};

module.exports = {
  phanTichYeuCauKai,
};
