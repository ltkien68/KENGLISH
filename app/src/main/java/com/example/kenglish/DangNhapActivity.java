package com.example.kenglish;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kenglish.api.ApiService;
import com.example.kenglish.api.RetrofitClient;
import com.example.kenglish.model.DangNhapRequest;
import com.example.kenglish.model.DangNhapResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DangNhapActivity extends AppCompatActivity {

    private View khungDangNhap;

    private EditText edtEmail;
    private EditText edtMatKhau;

    private View btnDangNhap;
    private View txtDangKy;

    private TextView txtLoiDangNhap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dangnhap);

        anhXa();

        // Animation nhẹ khi mở màn hình
        hienThiAnimation();

        // Chuyển sang màn đăng ký
        txtDangKy.setOnClickListener(v -> {

            Intent intent = new Intent(
                    DangNhapActivity.this,
                    DangKyActivity.class
            );

            startActivity(intent);

            overridePendingTransition(
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
            );
        });

        // Xử lý khi nhấn nút đăng nhập
        btnDangNhap.setOnClickListener(v -> {

            // Animation khi nhấn nút
            v.animate()
                    .scaleX(0.97f)
                    .scaleY(0.97f)
                    .setDuration(80)
                    .withEndAction(() ->
                            v.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(120)
                                    .start()
                    )
                    .start();

            dangNhap();
        });
    }

    private void anhXa() {

        khungDangNhap =
                findViewById(R.id.khung_dang_nhap);

        edtEmail =
                findViewById(R.id.edt_email);

        edtMatKhau =
                findViewById(R.id.edt_mat_khau);

        btnDangNhap =
                findViewById(R.id.btn_dang_nhap);

        txtDangKy =
                findViewById(R.id.txt_dang_ky);

        txtLoiDangNhap =
                findViewById(R.id.txt_loi_dang_nhap);
    }

    private void dangNhap() {

        String email =
                edtEmail.getText().toString().trim();

        String matKhau =
                edtMatKhau.getText().toString();

        // Kiểm tra email trống
        if (email.isEmpty()) {
            edtEmail.setError("Vui lòng nhập email");
            edtEmail.requestFocus();
            return;
        }

        // Kiểm tra định dạng email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Email không hợp lệ");
            edtEmail.requestFocus();
            return;
        }

        // Kiểm tra mật khẩu trống
        if (matKhau.isEmpty()) {
            edtMatKhau.setError("Vui lòng nhập mật khẩu");
            edtMatKhau.requestFocus();
            return;
        }

        // Tạo dữ liệu gửi lên backend
        DangNhapRequest request =
                new DangNhapRequest(
                        email,
                        matKhau
                );

        // Tạo API service
        ApiService apiService =
                RetrofitClient
                        .getClient()
                        .create(ApiService.class);

        // Gửi request đăng nhập
        apiService.dangNhap(request)
                .enqueue(new Callback<DangNhapResponse>() {

                    @Override
                    public void onResponse(
                            Call<DangNhapResponse> call,
                            Response<DangNhapResponse> response
                    ) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            DangNhapResponse ketQua =
                                    response.body();

                            if (ketQua.isThanhCong()) {

                                // Lấy token backend trả về
                                String token =
                                        ketQua.getToken();

                                // Lưu token vào máy
                                luuToken(token);

                                Toast.makeText(
                                        DangNhapActivity.this,
                                        ketQua.getThongBao(),
                                        Toast.LENGTH_SHORT
                                ).show();

                                // Đăng nhập thành công -> Trang chủ
                                moTrangChu();

                            } else {

                                // Ví dụ:
                                // - Sai email
                                // - Sai mật khẩu
                                // - Email chưa xác thực
                                txtLoiDangNhap.setText(
                                        "Tài khoản hoặc mật khẩu không chính xác"
                                );

                                txtLoiDangNhap.setVisibility(View.VISIBLE);
                            }

                        } else {

                            Toast.makeText(
                                    DangNhapActivity.this,
                                    "Lỗi server: " + response.code(),
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<DangNhapResponse> call,
                            Throwable t
                    ) {

                        Toast.makeText(
                                DangNhapActivity.this,
                                "Không thể kết nối server: "
                                        + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void luuToken(String token) {

        SharedPreferences sharedPreferences =
                getSharedPreferences(
                        "Kenglish",
                        MODE_PRIVATE
                );

        sharedPreferences
                .edit()
                .putString("token", token)
                .apply();
    }

    private void moTrangChu() {

        Intent intent = new Intent(
                DangNhapActivity.this,
                MainActivity.class
        );

        startActivity(intent);

        // Không cho bấm Back quay lại Login
        finish();
    }

    private void hienThiAnimation() {

        khungDangNhap.setAlpha(0f);
        khungDangNhap.setTranslationY(20f);

        khungDangNhap.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(350)
                .start();
    }
}