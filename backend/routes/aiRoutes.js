const express = require("express");
const router = express.Router();

const xacThucToken = require("../middleware/xacThucToken");
const { chatAI } = require("../controllers/AIControllers/aiController");

router.post("/chat", xacThucToken, chatAI);

module.exports = router;