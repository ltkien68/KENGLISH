const express = require("express");
const dangKy = require("../controllers/authControllers/dangKyController");
const xacThucEmail = require("../controllers/authControllers/xacThucEmailController");
const guiLaiMaXacThuc = require("../controllers/authControllers/guiLaiMaController");
const dangNhap = require("../controllers/authControllers/dangNhapController");
const dangXuat = require("../controllers/authControllers/dangXuatController");
const quenMatKhau = require("../controllers/authControllers/quenMatKhau");
const doiMatKhau = require("../controllers/authControllers/doiMatKhau");
const datLaiMatKhau = require("../controllers/authControllers/datLaiMatKhau");
const xacThucToken = require("../middleware/xacThucToken");
const router = express.Router();

router.post("/dang-ky", dangKy);
router.post("/xac-thuc-email", xacThucEmail);
router.post("/gui-lai-ma-xac-thuc", guiLaiMaXacThuc);
router.post("/dang-nhap", dangNhap);
router.post("/dang-xuat", xacThucToken, dangXuat);
router.post("/quen-mat-khau", quenMatKhau);
router.post("/dat-lai-mat-khau", datLaiMatKhau);
router.post("/doi-mat-khau", doiMatKhau);

module.exports = router;
