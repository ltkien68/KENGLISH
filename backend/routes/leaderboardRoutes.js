const express = require("express");
const router = express.Router();

const xacThucToken = require("../middleware/xacThucToken");
const layBangXepHang = require("../controllers/leaderboardControllers/layBangXepHang");

router.get("/", xacThucToken, layBangXepHang);

module.exports = router;
