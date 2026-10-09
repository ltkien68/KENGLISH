const express = require("express");

const router = express.Router();

const layTuLuyenTap = require("../controllers/practiceControllers/layTuLuyenTap");
const xacThucToken = require("../middleware/xacThucToken");
const luuTienTrinhLuyenTap = require("../controllers/PracticeControllers/luuTienTrinhLuyenTap");
const layLichSuLuyenTap = require("../controllers/PracticeControllers/layLichSuLuyenTap");
const layThongKeLuyenTap = require("../controllers/PracticeControllers/layThongKeLuyenTap");


router.get("/words", xacThucToken, layTuLuyenTap);
router.post("/save-progress", xacThucToken, luuTienTrinhLuyenTap);
router.get("/history", xacThucToken, layLichSuLuyenTap);
router.get("/statistics", xacThucToken, layThongKeLuyenTap);

module.exports = router;