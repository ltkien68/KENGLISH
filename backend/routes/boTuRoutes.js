const express = require("express");

const router = express.Router();

const xacThucToken = require("../middleware/xacThucToken");

const taoBoTu = require("../controllers/boTuControllers/taoBoTu");
const layDanhSachBoTu = require("../controllers/boTuControllers/layDanhSachBoTu");

const themTu = require("../controllers/vocabControllers/themTu");
const layDanhSachTu = require("../controllers/vocabControllers/layDanhSachTu");
const chuyenBoTuVaoFolder = require("../controllers/boTuControllers/chuyenBoTuVaoFolder");
const xoaTu = require("../controllers/vocabControllers/xoaTu");
const capNhatTrangThaiTu = require("../controllers/vocabControllers/capNhatTrangThaiTu");

router.post("/", xacThucToken, taoBoTu);
router.get("/", xacThucToken, layDanhSachBoTu);

router.post("/:boTuId/words", xacThucToken, themTu);
router.get("/:boTuId/words", xacThucToken, layDanhSachTu);

router.patch("/:boTuId/folder", xacThucToken, chuyenBoTuVaoFolder);

router.patch(
  "/:boTuId/words/:tuVungId/status",
  xacThucToken,
  capNhatTrangThaiTu,
);

router.delete("/:boTuId/words/:tuVungId", xacThucToken, xoaTu);

module.exports = router;
