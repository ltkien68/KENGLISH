const express = require("express");

const router = express.Router();

const layTuLuyenTap = require("../controllers/PracticeControllers/layTuLuyenTap");
const xacThucToken = require("../middleware/xacThucToken");

router.get("/words", xacThucToken, layTuLuyenTap);

module.exports = router;