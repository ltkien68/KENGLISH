const multer = require("multer");

const uploadAvatar = multer({
  storage: multer.memoryStorage(),

  limits: {
    fileSize: 5 * 1024 * 1024,
    files: 1,
  },

  fileFilter: (req, file, callback) => {
    const dinhDangHopLe = ["image/jpeg", "image/png", "image/webp"];

    if (!dinhDangHopLe.includes(file.mimetype)) {
      return callback(new Error("Chi chap nhan anh JPG, PNG hoac WEBP"));
    }

    callback(null, true);
  },
}).single("avatar");

module.exports = uploadAvatar;
