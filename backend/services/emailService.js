const nodemailer = require("nodemailer");

// Tạo cấu hình gửi email qua Gmail SMTP
const transporter = nodemailer.createTransport({
  host: "smtp.gmail.com",
  port: 587,
  secure: false,

  auth: {
    user: process.env.EMAIL_GUI,
    pass: process.env.EMAIL_APP_PASSWORD,
  },
});

// Gửi mã xác thực đến email người dùng
async function guiMaXacThuc(email, maXacThuc) {
  try {
    const info = await transporter.sendMail({
      from: `"Kenglish" <${process.env.EMAIL_GUI}>`,
      to: email,

      subject: "Kenglish - Xác thực Email",

      html: `
    <div style="
        font-family: Arial, sans-serif;
        max-width: 500px;
        margin: 0 auto;
        padding: 32px 24px;
        text-align: center;
        color: #182230;
    ">

        <h2 style="
            margin-bottom: 12px;
            font-size: 24px;
        ">
            Xác minh tài khoản Kenglish
        </h2>

        <p style="
            margin-bottom: 24px;
            color: #667085;
            font-size: 15px;
        ">
            Sử dụng mã xác minh bên dưới để hoàn tất đăng ký tài khoản của bạn.
        </p>

        <div style="
            display: inline-block;
            padding: 14px 28px;
            background-color: #F2F4F7;
            border-radius: 10px;
            font-size: 32px;
            font-weight: bold;
            letter-spacing: 8px;
            color: #182230;
        ">
            ${maXacThuc}
        </div>

        <p style="
            margin-top: 24px;
            color: #667085;
            font-size: 14px;
        ">
            Mã xác minh có hiệu lực trong <strong>5 phút</strong>.
        </p>

        <p style="
            margin-top: 32px;
            color: #98A2B3;
            font-size: 12px;
        ">
            Nếu bạn không yêu cầu mã xác minh này, bạn có thể bỏ qua email.
        </p>

    </div>
`,

      text:
        `Mã xác thực tài khoản Kenglish của bạn là: ${maXacThuc}. ` +
        `Mã code này sẽ hết hạn trong 5 phút.`,
    });

    console.log("Message ID:", info.messageId);
    console.log("Accepted:", info.accepted);
    console.log("Rejected:", info.rejected);
    console.log("Response:", info.response);

    return true;
  } catch (error) {
    console.error("Lỗi gửi email:", error);

    return false;
  }
}

async function guiMaDatLaiMatKhau(email, maXacThuc) {
    try {
        const info = await transporter.sendMail({
            from: `"Kenglish" <${process.env.EMAIL_GUI}>`,
            to: email,

            subject: "Kenglish - Đặt lại mật khẩu",

            html: `
                <div style="
                    font-family: Arial, sans-serif;
                    max-width: 500px;
                    margin: 0 auto;
                    padding: 32px 24px;
                    text-align: center;
                    color: #182230;
                ">

                    <h2 style="
                        margin-bottom: 12px;
                        font-size: 24px;
                    ">
                        Đặt lại mật khẩu Kenglish
                    </h2>

                    <p style="
                        margin-bottom: 24px;
                        color: #667085;
                        font-size: 15px;
                    ">
                        Sử dụng mã OTP bên dưới để đặt lại mật khẩu tài khoản của bạn.
                    </p>

                    <div style="
                        display: inline-block;
                        padding: 14px 28px;
                        background-color: #F2F4F7;
                        border-radius: 10px;
                        font-size: 32px;
                        font-weight: bold;
                        letter-spacing: 8px;
                        color: #182230;
                    ">
                        ${maXacThuc}
                    </div>

                    <p style="
                        margin-top: 24px;
                        color: #667085;
                        font-size: 14px;
                    ">
                        Mã OTP có hiệu lực trong <strong>5 phút</strong>.
                    </p>

                    <p style="
                        margin-top: 32px;
                        color: #98A2B3;
                        font-size: 12px;
                    ">
                        Nếu bạn không yêu cầu đặt lại mật khẩu, hãy bỏ qua email này.
                    </p>

                </div>
            `,

            text:
                `Mã OTP đặt lại mật khẩu Kenglish của bạn là: ${maXacThuc}. ` +
                `Mã OTP sẽ hết hạn trong 5 phút.`,                   
        });

        console.log("Message ID:", info.messageId);
        console.log("Accepted:", info.accepted);
        console.log("Rejected:", info.rejected);
        console.log("Response:", info.response);

        return true;
    } catch (error) {
        console.error("Lỗi gửi email đặt lại mật khẩu: ", error);

        return false;
    }
}

module.exports = {
  guiMaXacThuc,
  guiMaDatLaiMatKhau,
};
