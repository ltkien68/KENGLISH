const { goiGeminiKai } = require("./kaiModelService");

const gioiHan = (giaTri, doDai) => {
    if (typeof giaTri !== "string") {
        return "";
    }

    return giaTri.trim().slice(0, doDai);
};

const taoDanhSachTuAI = async (chuDe, soLuong) => {
    if (
        typeof chuDe !== "string" ||
        !chuDe.trim() ||
        !Number.isInteger(soLuong) ||
        soLuong < 1 ||
        soLuong > 10
    ) {
        throw new Error("KAI_INVALID_INTENT");
    }

    const prompt = `
Bạn là Kai, trợ lý học tiếng Anh của Kenglish.

Hãy tạo chính xác ${soLuong} từ vựng tiếng Anh
thuộc chủ đề: "${chuDe.trim()}".

Yêu cầu:
- Chọn từ thực tế, hữu ích cho người học.
- Không tạo từ trùng nhau.
- Nghĩa tiếng Việt phải chính xác, dễ hiểu.
- Phiên âm IPA nếu biết chắc chắn.
- Mỗi từ có một câu ví dụ tiếng Anh tự nhiên.
- Từ đồng nghĩa, trái nghĩa có thể để chuỗi rỗng.
- Không viết giải thích ngoài JSON.
- Phải tạo đủ chính xác ${soLuong} từ.
- Không tự ý giảm số lượng từ.

Trả về một đối tượng JSON theo cấu trúc:

{
  "ten_bo_tu": "Tên bộ từ ngắn gọn",
  "mo_ta": "Mô tả ngắn",
  "danh_sach_tu": [
    {
      "tu_goc": "example",
      "phien_am": "/ɪɡˈzɑːmpəl/",
      "loai_tu": "noun",
      "nghia_tieng_viet": "ví dụ",
      "cau_vi_du": "This is an example.",
      "tu_dong_nghia": "sample",
      "tu_trai_nghia": ""
    }
  ]
}
`;

    console.log("========== KAI VOCAB ==========");
    console.log("Model: gemini-3.1-flash-lite");
    console.log("Chủ đề:", chuDe);
    console.log("Số lượng:", soLuong);

    const response = await goiGeminiKai({
        contents: prompt,
        config: {
            responseMimeType: "application/json",
            temperature: 0.4,
            maxOutputTokens: 4096
        }
    });

    if (!response.text || !response.text.trim()) {
        throw new Error("GEMINI_EMPTY_RESPONSE");
    }

    let duLieu;

    try {
        duLieu = JSON.parse(response.text);
    } catch (error) {
        console.error("JSON không hợp lệ:", error.message);
        throw new Error("GEMINI_INVALID_JSON");
    }

    if (
        !duLieu ||
        !Array.isArray(duLieu.danh_sach_tu)
    ) {
        throw new Error("GEMINI_INVALID_VOCABULARY");
    }

    const danhSachTu = [];
    const tuDaCo = new Set();

    for (const item of duLieu.danh_sach_tu) {
        if (!item || typeof item !== "object") {
            continue;
        }

        const tuGoc = gioiHan(item.tu_goc, 100);
        const nghia = gioiHan(
            item.nghia_tieng_viet,
            255
        );

        if (!tuGoc || !nghia) {
            continue;
        }

        const khoa = tuGoc.toLowerCase();

        if (tuDaCo.has(khoa)) {
            continue;
        }

        tuDaCo.add(khoa);

        danhSachTu.push({
            tu_goc: tuGoc,
            phien_am: gioiHan(item.phien_am, 100) || null,
            loai_tu: gioiHan(item.loai_tu, 50) || null,
            nghia_tieng_viet: nghia,
            cau_vi_du: gioiHan(item.cau_vi_du, 500) || null,
            tu_dong_nghia: gioiHan(item.tu_dong_nghia, 255) || null,
            tu_trai_nghia: gioiHan(item.tu_trai_nghia, 255) || null
        });

        if (danhSachTu.length === soLuong) {
            break;
        }
    }

    if (danhSachTu.length !== soLuong) {
        throw new Error("GEMINI_INSUFFICIENT_VOCABULARY");
    }

    console.log("Số từ tạo thành công:", danhSachTu.length);
    console.log("==============================");

    return {
        ten_bo_tu: gioiHan(duLieu.ten_bo_tu, 100)
            || `Kai - ${chuDe}`.slice(0, 100),

        mo_ta: gioiHan(duLieu.mo_ta, 255)
            || `Bộ từ vựng chủ đề ${chuDe}`,

        danh_sach_tu: danhSachTu
    };
};

module.exports = {
    taoDanhSachTuAI
};
