const express = require("express");

const router = express.Router();

const xacThucToken = require("../middleware/xacThucToken");

const taoFolder = require("../controllers/folderControllers/taoFolder");
const layDanhSachFolder = require("../controllers/folderControllers/layDanhSachFolder");
const layBoTuTheoFolder = require("../controllers/boTuControllers/layBoTuTheoFolder");


router.post("/", xacThucToken, taoFolder);

router.get("/", xacThucToken, layDanhSachFolder);

router.get("/:folderId/vocabulary", xacThucToken, layBoTuTheoFolder);

module.exports = router;