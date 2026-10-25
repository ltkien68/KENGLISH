const db = require("../../config/database");
const cloudinary = require("../../config/cloudinary");
const { fileTypeFromBuffer } = require("file-type");

const capNhatAvatar = async (req, res) => {
  let anhCloudinary = null;

  try {
    const nguoiDungId = req.nguoiDung.id;

    if (!req.file || !req.file.buffer) {
      return res.status(400).json({
        thanh_cong: false,
        thong_bao: "Vui long chon anh avatar",
      });
    }

    // Kiem tra dinh dang thuc te cua file
    const thongTinFile = await fileTypeFromBuffer(req.file.buffer);

    const dinhDangHopLe = ["image/jpeg", "image/png", "image/webp"];

    if (!thongTinFile || !dinhDangHopLe.includes(thongTinFile.mime)) {
      return res.status(400).json({
        thanh_cong: false,
        thong_bao: "File khong phai anh JPG, PNG hoac WEBP hop le",
      });
    }

    // Kiem tra tai khoan truoc khi upload
    const [nguoiDung] = await db.query(
      `
            SELECT id, avatar_url
            FROM nguoi_dung
            WHERE id = ?
                AND da_xoa = 0
            LIMIT 1
            `,
      [nguoiDungId],
    );

    if (nguoiDung.length === 0) {
      return res.status(404).json({
        thanh_cong: false,
        thong_bao: "Khong tim thay nguoi dung",
      });
    }

    // Upload anh len Cloudinary
    const ketQuaUpload = await new Promise((resolve, reject) => {
      const stream = cloudinary.uploader.upload_stream(
        {
          folder: "kenglish/avatars",
          public_id: `user_${nguoiDungId}`,
          overwrite: true,
          resource_type: "image",
          format: thongTinFile.ext,
        },
        (error, result) => {
          if (error) {
            return reject(error);
          }

          resolve(result);
        },
      );

      stream.end(req.file.buffer);
    });

    anhCloudinary = {
      public_id: ketQuaUpload.public_id,
      secure_url: ketQuaUpload.secure_url,
    };

    // Luu URL avatar vao database
    await db.query(
      `
            UPDATE nguoi_dung
            SET avatar_url = ?
            WHERE id = ?
                AND da_xoa = 0
            `,
      [anhCloudinary.secure_url, nguoiDungId],
    );

    return res.status(200).json({
      thanh_cong: true,
      thong_bao: "Cap nhat avatar thanh cong",
      data: {
        avatar_url: anhCloudinary.secure_url,
      },
    });
  } catch (error) {
    console.error("Loi cap nhat avatar:", error);

    // Neu upload thanh cong nhung xu ly sau do that bai,
    // xoa anh vua upload de tranh anh rac tren Cloudinary.
    if (anhCloudinary) {
      try {
        await cloudinary.uploader.destroy(anhCloudinary.public_id, {
          resource_type: "image",
        });
      } catch (loiXoaAnh) {
        console.error("Khong the xoa anh Cloudinary:", loiXoaAnh.message);
      }
    }

    return res.status(500).json({
      thanh_cong: false,
      thong_bao: "Khong the cap nhat avatar",
    });
  }
};

module.exports = capNhatAvatar;
