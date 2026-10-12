const express = require("express");
const router = express.Router();

const xacThucToken = require("../middleware/xacThucToken");
const { chatAI } = require("../controllers/AIControllers/aiController");
const taoBoTuBangAI = require(
    "../controllers/AIControllers/taoBoTuBangAI"
);
const phanTichYeuCau = require(
    "../controllers/AIControllers/phanTichYeuCau"
);

router.post("/chat", xacThucToken, chatAI);
router.post(
    "/create-vocabulary",
    xacThucToken,
    taoBoTuBangAI
);
router.post(
    "/intent",
    xacThucToken,
    phanTichYeuCau
);

module.exports = router;