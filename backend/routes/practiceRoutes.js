const express = require("express");

const router = express.Router();

const layTuLuyenTap = require("../controllers/PracticeControllers/layTuLuyenTap");
const xacThucToken = require("../middleware/xacThucToken");
const luuTienTrinhLuyenTap = require("../controllers/PracticeControllers/luuTienTrinhLuyenTap");

router.get("/words", xacThucToken, layTuLuyenTap);
router.post("/save-progress", xacThucToken, luuTienTrinhLuyenTap);

module.exports = router;