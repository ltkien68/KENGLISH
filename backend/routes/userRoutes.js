const express = require("express");

const xacThucToken = require("../middleware/xacThucToken");

const xoaTaiKhoan = require("../controllers/userControllers/xoaTaiKhoanController");

const layThongKeHocTap = require("../controllers/userControllers/layThongKeHocTapController");

const router = express.Router();

router.get("/thong-ke-hoc-tap", xacThucToken, layThongKeHocTap);

router.delete("/me", xacThucToken, xoaTaiKhoan);

module.exports = router;
