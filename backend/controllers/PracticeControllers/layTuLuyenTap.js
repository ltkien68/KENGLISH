
const db = require("../../config/database");

const layTuLuyenTap = async (req, res) => {
    try {
        const nguoiDungId = req.nguoiDung.id;

        const boTuId = Number(req.query.bo_tu_id ?? -1);
        const trangThai = req.query.trang_thai ?? "all";
        const thuTu = req.query.thu_tu ?? "random";
        const soLuong = Number(req.query.so_luong ?? 20);

        if (
            !Number.isInteger(boTuId) ||
            (boTuId !== -1 && boTuId <= 0) ||
            !["all", "learned", "unlearned"].includes(trangThai) ||
            !["random", "default"].includes(thuTu) ||
            ![10, 20, 50, 100, 200].includes(soLuong)
        ) {
            return res.status(400).json({
                thanh_cong: false,
                thong_bao: "Bộ lọc không hợp lệ"
            });
        }

        const dieuKien = [
            "bt.nguoi_dung_id = ?"
        ];

        const thamSo = [nguoiDungId];

        if (boTuId !== -1) {
            dieuKien.push("tv.bo_tu_id = ?");
            thamSo.push(boTuId);
        }

        if (trangThai === "learned") {
            dieuKien.push("tv.da_thuoc = 1");
        } else if (trangThai === "unlearned") {
            dieuKien.push(
                "(tv.da_thuoc = 0 OR tv.da_thuoc IS NULL)"
            );
        }

        const sapXep =
            thuTu === "random"
                ? "RAND()"
                : "tv.id ASC";

        const [danhSachTu] = await db.query(
            `
            SELECT
                tv.id,
                tv.bo_tu_id,
                bt.ten_bo_tu,
                tv.tu_goc,
                tv.phien_am,
                tv.loai_tu,
                tv.nghia_tieng_viet,
                tv.cau_vi_du,
                tv.tu_dong_nghia,
                tv.tu_trai_nghia,
                tv.da_thuoc
            FROM tu_vung tv
            INNER JOIN bo_tu bt
                ON bt.id = tv.bo_tu_id
            WHERE ${dieuKien.join(" AND ")}
            ORDER BY ${sapXep}
            LIMIT ?
            `,
            [...thamSo, soLuong]
        );

        return res.status(200).json({
            thanh_cong: true,
            data: {
                tong_tu: danhSachTu.length,
                tu_vung: danhSachTu
            }
        });

    } catch (error) {
        console.error("Lỗi lấy từ luyện tập:", error);

        return res.status(500).json({
            thanh_cong: false,
            thong_bao: "Lỗi server"
        });
    }
};

module.exports = layTuLuyenTap;
