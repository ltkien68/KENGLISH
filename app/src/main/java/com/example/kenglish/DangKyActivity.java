package com.example.kenglish;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kenglish.api.ApiService;
import com.example.kenglish.api.RetrofitClient;
import com.example.kenglish.model.ApiResponse;
import com.example.kenglish.model.DangKyRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DangKyActivity extends AppCompatActivity {

    private EditText edtTenHienThi;
    private EditText edtEmail;
    private EditText edtMatKhau;
    private EditText edtXacNhanMatKhau;

    private TextView btnDangKy;
    private TextView txtDangNhap;
    private TextView txtLoiDangKy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dangky);

        anhXa();

        btnDangKy.setOnClickListener(v -> dangKy());

        txtDangNhap.setOnClickListener(v -> finish());

        // Khi người dùng sửa dữ liệu thì ẩn lỗi cũ
        edtTenHienThi.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                anLoiDangKy();
            }
        });

        edtEmail.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                anLoiDangKy();
            }
        });

        edtMatKhau.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                anLoiDangKy();
            }
        });

        edtXacNhanMatKhau.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                anLoiDangKy();
            }
        });
    }

    private void anhXa() {

        edtTenHienThi =
                findViewById(R.id.edt_ten_hien_thi);

        edtEmail =
                findViewById(R.id.edt_email);

        edtMatKhau =
                findViewById(R.id.edt_mat_khau);

        edtXacNhanMatKhau =
                findViewById(R.id.edt_xac_nhan_mat_khau);

        btnDangKy =
                findViewById(R.id.btn_dang_ky);

        txtDangNhap =
                findViewById(R.id.txt_dang_nhap);

        txtLoiDangKy =
                findViewById(R.id.txt_loi_dang_ky);
    }

    private void dangKy() {

        // Ẩn thông báo lỗi cũ trước khi kiểm tra lại
        anLoiDangKy();

        String tenHienThi =
                edtTenHienThi.getText().toString().trim();

        String email =
                edtEmail.getText().toString().trim();

        String matKhau =
                edtMatKhau.getText().toString();

        String xacNhanMatKhau =
                edtXacNhanMatKhau.getText().toString();

        // Validate phía Android
        if (tenHienThi.isEmpty()) {
            edtTenHienThi.setError("Vui lòng nhập tên hiển thị");
            edtTenHienThi.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            edtEmail.setError("Vui lòng nhập email");
            edtEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Email không hợp lệ");
            edtEmail.requestFocus();
            return;
        }

        if (matKhau.isEmpty()) {
            edtMatKhau.setError("Vui lòng nhập mật khẩu");
            edtMatKhau.requestFocus();
            return;
        }

        if (xacNhanMatKhau.isEmpty()) {
            edtXacNhanMatKhau.setError("Vui lòng xác nhận mật khẩu");
            edtXacNhanMatKhau.requestFocus();
            return;
        }

        if (!matKhau.equals(xacNhanMatKhau)) {
            edtXacNhanMatKhau.setError("Mật khẩu không khớp");
            edtXacNhanMatKhau.requestFocus();
            return;
        }

        guiYeuCauDangKy(
                tenHienThi,
                email,
                matKhau
        );
    }

    private void guiYeuCauDangKy(
            String tenHienThi,
            String email,
            String matKhau
    ) {

        DangKyRequest request =
                new DangKyRequest(
                        tenHienThi,
                        email,
                        matKhau
                );

        ApiService apiService =
                RetrofitClient
                        .getClient()
                        .create(ApiService.class);

        apiService.dangKy(request)
                .enqueue(new Callback<ApiResponse>() {

                    @Override
                    public void onResponse(
                            Call<ApiResponse> call,
                            Response<ApiResponse> response
                    ) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            ApiResponse ketQua =
                                    response.body();

                            if (ketQua.isThanhCong()) {

                                // Đăng ký thành công
                                anLoiDangKy();

                                moManHinhXacThuc(email);

                            } else {

                                // Lỗi nghiệp vụ từ backend
                                // Ví dụ: email đã tồn tại
                                hienLoiDangKy(
                                        ketQua.getThongBao()
                                );
                            }

                        } else {

                            // Lỗi HTTP/server vẫn dùng Toast
                            Toast.makeText(
                                    DangKyActivity.this,
                                    "Lỗi server: " + response.code(),
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse> call,
                            Throwable t
                    ) {

                        // Lỗi kết nối vẫn dùng Toast
                        Toast.makeText(
                                DangKyActivity.this,
                                "Không thể kết nối server: "
                                        + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void hienLoiDangKy(String thongBao) {

        txtLoiDangKy.setText(thongBao);
        txtLoiDangKy.setVisibility(View.VISIBLE);
    }

    private void anLoiDangKy() {

        txtLoiDangKy.setVisibility(View.GONE);
    }

    private void moManHinhXacThuc(String email) {

        Intent intent =
                new Intent(
                        DangKyActivity.this,
                        XacThucEmailActivity.class
                );

        intent.putExtra("email", email);

        startActivity(intent);
    }
}