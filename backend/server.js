process.env.TZ = "Asia/Ho_Chi_Minh";

const express = require("express");
const cors = require("cors");
const http = require("http");
const jwt = require("jsonwebtoken");
const { Server } = require("socket.io");
const cron = require("node-cron");

require("dotenv").config();

const app = express();
const server = http.createServer(app);

const ketNoi = require("./config/database");

// Các routes hiện tại
const authRoutes = require("./routes/authRoutes");
const userRoutes = require("./routes/userRoutes");
const dictionaryRoutes = require("./routes/dictionaryRoutes");
const boTuRoutes = require("./routes/boTuRoutes");
const folderRoutes = require("./routes/folderRoutes");
const activityRoutes = require("./routes/activityRoutes");
const practiceRoutes = require("./routes/practiceRoutes");
const leaderboardRoutes = require("./routes/leaderboardRoutes");
const aiRoutes = require("./routes/aiRoutes");

// Route cộng đồng
const communityRoutes = require("./routes/communityRoutes");

// Middleware
app.use(cors());
app.use(express.json());

// Routes
app.use("/auth", authRoutes);
app.use("/user", userRoutes);
app.use("/dictionary", dictionaryRoutes);
app.use("/vocabulary", boTuRoutes);
app.use("/folder", folderRoutes);
app.use("/activity", activityRoutes);
app.use("/practice", practiceRoutes);
app.use("/leaderboard", leaderboardRoutes);
app.use("/ai", aiRoutes);
app.use("/community", communityRoutes);

// Khởi tạo Socket.IO
const io = new Server(server, {
    cors: {
        origin: "*",
        methods: ["GET", "POST"]
    }
});

// Lưu Socket.IO để controller có thể phát sự kiện
app.set("io", io);

// Xác thực JWT cho Socket.IO
io.use(async (socket, next) => {
    try {
        const token = socket.handshake.auth?.token;

        if (!token) {
            return next(new Error("Chưa cung cấp token"));
        }

        const duLieuToken = jwt.verify(
            token,
            process.env.JWT_SECRET
        );

        // Kiểm tra tài khoản còn hoạt động
        const [rows] = await ketNoi.query(
            `SELECT id FROM nguoi_dung
             WHERE id = ? AND da_xoa = 0`,
            [duLieuToken.id]
        );

        if (rows.length === 0) {
            return next(new Error("Tài khoản không hợp lệ"));
        }

        socket.nguoiDung = duLieuToken;

        next();

    } catch (error) {
        next(new Error("Token không hợp lệ hoặc đã hết hạn"));
    }
});

// Khi người dùng kết nối Socket.IO
io.on("connection", (socket) => {
    console.log(
        `Người dùng ${socket.nguoiDung.id} đã kết nối cộng đồng`
    );

    // Tham gia phòng chat chung
    socket.join("cong_dong");

    socket.on("disconnect", () => {
        console.log(
            `Người dùng ${socket.nguoiDung.id} đã ngắt kết nối`
        );
    });
});

// API kiểm tra server
app.get("/", (req, res) => {
    res.json({
        success: true,
        message: "Kenglish API đang hoạt động"
    });
});

// API kiểm tra database
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

// Tự động xóa tin nhắn quá 3 tháng
cron.schedule("0 3 * * *", async () => {
    try {
        const [ketQua] = await ketNoi.query(`
            DELETE FROM tin_nhan_cong_dong
            WHERE tao_luc < DATE_SUB(NOW(), INTERVAL 3 MONTH)
        `);

        console.log(
            `Đã xóa ${ketQua.affectedRows} tin nhắn quá hạn`
        );

    } catch (error) {
        console.error("Lỗi dọn tin nhắn cũ:", error);
    }
}, {
    timezone: "Asia/Ho_Chi_Minh"
});

// Khởi động server
const PORT = process.env.PORT || 3000;

server.listen(PORT, "0.0.0.0", () => {
    console.log(`Kenglish API đang chạy tại http://localhost:${PORT}`);
    console.log("Socket.IO đã sẵn sàng");
});
