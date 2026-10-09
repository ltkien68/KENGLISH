const express = require("express");
const cors = require("cors");
require("dotenv").config();

// Tạo ứng dụng Express
const app = express();
const ketNoi = require("./config/database");
const authRoutes = require("./routes/authRoutes");
const userRoutes = require("./routes/userRoutes");
const dictionaryRoutes = require("./routes/dictionaryRoutes");
const boTuRoutes = require("./routes/boTuRoutes");
const folderRoutes = require("./routes/folderRoutes");
const activityRoutes = require("./routes/activityRoutes");
const practiceRoutes = require("./routes/practiceRoutes");
const leaderboardRoutes = require("./routes/leaderboardRoutes");


// Middleware
app.use(cors());
app.use(express.json());

app.use("/auth", authRoutes);
app.use("/user", userRoutes);
app.use("/dictionary", dictionaryRoutes);
app.use("/vocabulary", boTuRoutes);
app.use("/folder", folderRoutes);
app.use("/activity", activityRoutes);
app.use("/practice", practiceRoutes);
app.use("/leaderboard", leaderboardRoutes);


// API kiểm tra server
app.get("/", (req, res) => {
  res.json({
    success: true,
    message: "Kenglish API đang hoạt động",
  });
});

app.get("/test-db", async (req, res) => {
    try {
        await ketNoi.query("SELECT 1");

        res.json({
            success: true,
            message: "Ket noi MySQL thanh cong"
        });
    } catch (error) {
        console.error(error);

        res.status(500).json({
            success: false,
            message: "Ket noi MySQL that bai"
        });
    }
});

// Cổng chạy server
const PORT = process.env.PORT || 3000;

app.listen(PORT, () => {
  console.log(`Kenglish API đang chạy tại http://localhost:${PORT}`);
});
