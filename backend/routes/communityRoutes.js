const express = require("express");
const router = express.Router();

const xacThucToken = require("../middleware/xacThucToken");

const {
    layDanhSachNgay,
    layTinNhan,
    guiTinNhan
} = require("../controllers/communityControllers/community");

// Tất cả API cộng đồng đều yêu cầu đăng nhập
router.use(xacThucToken);

router.get("/days", layDanhSachNgay);
router.get("/messages", layTinNhan);
router.post("/messages", guiTinNhan);

module.exports = router;
