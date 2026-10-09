const db = require("../../config/database");

const layHoatDongNamHienTai = async (req, res) => {
    try {
        const nguoiDungId = req.nguoiDung.id;

        // Lấy ngày hiện tại theo múi giờ Việt Nam.
        const [ngayRows] = await db.query(`
            SELECT
                DATE_FORMAT(
                    CONVERT_TZ(UTC_TIMESTAMP(), '+00:00', '+07:00'),
                    '%Y-%m-%d'
                ) AS hom_nay,
                YEAR(
                    CONVERT_TZ(UTC_TIMESTAMP(), '+00:00', '+07:00')
                ) AS nam
        `);

        const homNay = ngayRows[0].hom_nay;
        const namHienTai = Number(ngayRows[0].nam);

        // Lấy toàn bộ ngày hoạt động để tính streak
        // xuyên năm, không chỉ trong năm hiện tại.
        const [rows] = await db.query(
            `
            SELECT
                DATE_FORMAT(ngay, '%Y-%m-%d') AS ngay
            FROM ngay_hoat_dong
            WHERE nguoi_dung_id = ?
            ORDER BY ngay DESC
            `,
            [nguoiDungId]
        );

        const danhSachNgay = rows.map(item => item.ngay);
        const tapNgay = new Set(danhSachNgay);

        // Chuyển ngày YYYY-MM-DD thành số ngày UTC
        // để cộng/trừ ngày không phụ thuộc timezone server.
        const chuyenThanhSoNgay = (chuoiNgay) => {
            const [nam, thang, ngay] = chuoiNgay
                .split("-")
                .map(Number);

            return Math.floor(
                Date.UTC(nam, thang - 1, ngay) / 86400000
            );
        };

        const chuyenThanhChuoiNgay = (soNgay) => {
            return new Date(soNgay * 86400000)
                .toISOString()
                .slice(0, 10);
        };

        const soNgayHomNay = chuyenThanhSoNgay(homNay);

        let ngayBatDau = soNgayHomNay;
        let streak = 0;

        // Nếu hôm nay chưa học, bắt đầu kiểm tra hôm qua.
        if (!tapNgay.has(homNay)) {
            ngayBatDau = soNgayHomNay - 1;
        }

        // Đếm ngược các ngày học liên tiếp.
        while (true) {
            const ngayKiemTra =
                chuyenThanhChuoiNgay(ngayBatDau);

            if (!tapNgay.has(ngayKiemTra)) {
                break;
            }

            streak++;
            ngayBatDau--;
        }

        // Danh sách hoạt động trả về vẫn chỉ thuộc
        // năm hiện tại, phục vụ biểu đồ lịch học.
        const danhSachNgayNamHienTai =
            danhSachNgay
                .filter(ngay => ngay.startsWith(namHienTai + "-"))
                .reverse();

        return res.status(200).json({
            thanh_cong: true,
            data: {
                nam: namHienTai,
                current_streak: streak,
                ngay_hoat_dong: danhSachNgayNamHienTai
            }
        });

    } catch (error) {
        console.error("Lỗi lấy hoạt động:", error);

        return res.status(500).json({
            thanh_cong: false,
            thong_bao: "Lỗi server"
        });
    }
};

module.exports = layHoatDongNamHienTai;
