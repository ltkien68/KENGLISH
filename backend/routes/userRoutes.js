const express = require("express");

const xacThucToken = require("../middleware/xacThucToken");
const xoaTaiKhoan = require("../controllers/userControllers/xoaTaiKhoanController");
const layThongKeHocTap = require("../controllers/userControllers/layThongKeHocTapController");
const layThongTinNguoiDung = require("../controllers/userControllers/layThongTinNguoiDung");
const uploadAvatar = require("../middleware/uploadAvatar");
const capNhatAvatar = require("../controllers/userControllers/capNhatAvatar");
const router = express.Router();

router.get("/thong-ke-hoc-tap", xacThucToken, layThongKeHocTap);
router.get("/me", xacThucToken, layThongTinNguoiDung);
router.delete("/me", xacThucToken, xoaTaiKhoan);
router.put("/avatar", xacThucToken, uploadAvatar, capNhatAvatar);

module.exports = router;
