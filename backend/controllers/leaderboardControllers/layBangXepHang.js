const db = require("../../config/database");

const layBangXepHang = async (req, res) => {
    try {
        const loai = req.query.loai || "luot_choi";
        if (!["luot_choi", "streak"].includes(loai)) {
            return res.status(400).json({
                thanh_cong: false,
                thong_bao: "Loại bảng xếp hạng không hợp lệ"
            });
        }

        let danhSach = [];

        if (loai === "luot_choi") {
            const [rows] = await db.query(`
                SELECT
                    id,
                    ten_hien_thi,
                    COALESCE(so_luot_choi, 0) AS so_luot_choi
                FROM nguoi_dung
                WHERE da_xoa = 0
                ORDER BY so_luot_choi DESC, id ASC
                LIMIT 50
            `);

            danhSach = rows.map((item, index) => ({
                hang: index + 1,
                id: item.id,
                ten_hien_thi: item.ten_hien_thi,
                so_luot_choi: Number(item.so_luot_choi),
                current_streak: 0
            }));
        } else {
            const [nguoiDungRows] = await db.query(`
                SELECT
                    id,
                    ten_hien_thi
                FROM nguoi_dung
                WHERE da_xoa = 0
            `);

            const [ngayRows] = await db.query(`
                SELECT
                    hd.nguoi_dung_id,
                    DATE_FORMAT(hd.ngay, '%Y-%m-%d') AS ngay
                FROM ngay_hoat_dong hd
                INNER JOIN nguoi_dung nd
                    ON nd.id = hd.nguoi_dung_id
                WHERE nd.da_xoa = 0
                ORDER BY hd.nguoi_dung_id, hd.ngay DESC
            `);

            const [thoiGianRows] = await db.query(`
                SELECT DATE_FORMAT(
                    CONVERT_TZ(
                        UTC_TIMESTAMP(),
                        '+00:00',
                        '+07:00'
                    ),
                    '%Y-%m-%d'
                ) AS hom_nay
            `);

            const homNay = thoiGianRows[0].hom_nay;

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

            const nhomNgay = new Map();

            for (const item of ngayRows) {
                if (!nhomNgay.has(item.nguoi_dung_id)) {
                    nhomNgay.set(item.nguoi_dung_id, new Set());
                }

                nhomNgay.get(item.nguoi_dung_id).add(item.ngay);
            }

            const soNgayHomNay = chuyenThanhSoNgay(homNay);

            danhSach = nguoiDungRows.map((nguoiDung) => {
                const tapNgay = nhomNgay.get(nguoiDung.id) || new Set();

                let ngayKiemTra = soNgayHomNay;

                if (!tapNgay.has(homNay)) {
                    ngayKiemTra--;
                }

                let streak = 0;

                while (tapNgay.has(chuyenThanhChuoiNgay(ngayKiemTra))) {
                    streak++;
                    ngayKiemTra--;
                }

                return {
                    id: nguoiDung.id,
                    ten_hien_thi: nguoiDung.ten_hien_thi,
                    so_luot_choi: 0,
                    current_streak: streak
                };
            });

            danhSach.sort((a, b) => {
                if (b.current_streak !== a.current_streak) {
                    return b.current_streak - a.current_streak;
                }

                return a.id - b.id;
            });

            danhSach = danhSach.slice(0, 50).map((item, index) => ({
                hang: index + 1,
                ...item
            }));
        }

        return res.status(200).json({
            thanh_cong: true,
            thong_bao: "Lấy bảng xếp hạng thành công",
            data: {
                loai,
                danh_sach: danhSach
            }
        });
    } catch (error) {
        console.error("Lỗi lấy bảng xếp hạng:", error);

        return res.status(500).json({
            thanh_cong: false,
            thong_bao: "Không thể lấy bảng xếp hạng"
        });
    }
};

module.exports = layBangXepHang;
