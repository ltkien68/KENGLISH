const db = require("../config/database");


const ghiNhanHoatDong = async (nguoiDungId) => {

    await db.query(
        `
        INSERT IGNORE INTO ngay_hoat_dong (
            nguoi_dung_id,
            ngay
        )
        VALUES (?, CURDATE())
        `,
        [nguoiDungId]
    );
};


module.exports = {
    ghiNhanHoatDong
};