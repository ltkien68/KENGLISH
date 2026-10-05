const express =
        require("express");

const router =
        express.Router();


const xacThucToken =
        require("../middleware/xacThucToken");


const layHoatDongNamHienTai =
        require(
            "../controllers/activityControllers/layHoatDongNamHienTai"
        );


router.get(
    "/current-year",
    xacThucToken,
    layHoatDongNamHienTai
);


module.exports = router;