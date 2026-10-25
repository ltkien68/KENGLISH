const db = require("../../config/database")

const layThongTinNguoiDung = async (req, res) => {
    try {
        const nguoiDungId = req.nguoiDung.id;

        const [rows] = await db.query(
            `
            SELECT
                id,
                ten_hien_thi,
                avatar_url
            FROM nguoi_dung
            WHERE id = ?
                AND da_xoa = 0
            LIMIT 1
            `,
            [nguoiDungId]
        );

        if (rows.length === 0) {
            return res.status(404).json({
                thanh_cong: false,
                thong_bao: "Không tìm thấy người dùng"
            });
        }

        return res.status(200).json({
            thanh_cong: true,
            data: {
                id: rows[0].id,
                ten_hien_thi: rows[0].ten_hien_thi,
                avatar_url: rows[0].avatar_url
            }
        })
    } catch (error) {
        console.error("Lỗi lấy thông tin người dùng:", error);

        return res.status(500).json({
            thanh_cong: false,
            thong_bao: "Lỗi server"
        });
    }
}

module.exports = layThongTinNguoiDung;