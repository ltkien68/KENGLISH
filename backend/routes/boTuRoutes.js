const express = require("express");

const router = express.Router();

const xacThucToken = require("../middleware/xacThucToken");

const taoBoTu = require("../controllers/boTuControllers/taoBoTu");
const layDanhSachBoTu = require("../controllers/boTuControllers/layDanhSachBoTu");

const themTu = require("../controllers/vocabControllers/themTu");
const layDanhSachTu = require("../controllers/vocabControllers/layDanhSachTu");
const chuyenBoTuVaoFolder = require("../controllers/boTuControllers/chuyenBoTuVaoFolder");

router.post("/", xacThucToken, taoBoTu);
router.get("/", xacThucToken, layDanhSachBoTu);

router.post("/:boTuId/words", xacThucToken, themTu);
router.get("/:boTuId/words", xacThucToken, layDanhSachTu);

router.patch("/:boTuId/folder", xacThucToken, chuyenBoTuVaoFolder);


module.exports = router;