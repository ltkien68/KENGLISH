package com.example.kenglish;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kenglish.api.ApiService;
import com.example.kenglish.api.RetrofitClient;
import com.example.kenglish.model.ApiResponse;
import com.example.kenglish.model.XacThucEmailRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class XacThucEmailActivity extends AppCompatActivity {

    private TextView txtEmail;
    private TextView btnXacThuc;
    private EditText edtMaXacThuc;

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xacthucemail);

        anhXa();

        // Nhận email từ màn đăng ký
        email = getIntent().getStringExtra("email");

        if (email != null) {
            txtEmail.setText(
                    "Mã xác thực đã được gửi tới\n" + email
            );
        }

        // Xử lý khi nhấn nút xác thực
        btnXacThuc.setOnClickListener(v -> xacThucEmail());
    }

    private void anhXa() {

        txtEmail =
                findViewById(R.id.txt_email);

        edtMaXacThuc =
                findViewById(R.id.edt_ma_xac_thuc);

        btnXacThuc =
                findViewById(R.id.btn_xac_thuc);
    }

    private void xacThucEmail() {

        String maXacThuc =
                edtMaXacThuc.getText().toString().trim();

        // Validate phía Android
        if (maXacThuc.isEmpty()) {
            edtMaXacThuc.setError("Vui lòng nhập mã xác thực");
            return;
        }

        if (maXacThuc.length() != 6) {
            edtMaXacThuc.setError("Mã xác thực phải có 6 số");
            return;
        }

        if (email == null || email.isEmpty()) {

            Toast.makeText(
                    this,
                    "Không tìm thấy email cần xác thực",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        guiYeuCauXacThuc(maXacThuc);
    }

    private void guiYeuCauXacThuc(String maXacThuc) {

        XacThucEmailRequest request =
                new XacThucEmailRequest(
                        email,
                        maXacThuc
                );

        ApiService apiService =
                RetrofitClient
                        .getClient()
                        .create(ApiService.class);

        apiService.xacThucEmail(request)
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

                                Toast.makeText(
                                        XacThucEmailActivity.this,
                                        "Xác thực email thành công",
                                        Toast.LENGTH_SHORT
                                ).show();

                                moManHinhDangNhap();

                            } else {

                                Toast.makeText(
                                        XacThucEmailActivity.this,
                                        ketQua.getThongBao(),
                                        Toast.LENGTH_SHORT
                                ).show();
                            }

                        } else {

                            Toast.makeText(
                                    XacThucEmailActivity.this,
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

                        Toast.makeText(
                                XacThucEmailActivity.this,
                                "Không thể kết nối server: "
                                        + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void moManHinhDangNhap() {

        Intent intent =
                new Intent(
                        XacThucEmailActivity.this,
                        DangNhapActivity.class
                );

        // Xóa màn đăng ký + OTP khỏi back stack
        intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_NEW_TASK
        );

        startActivity(intent);

        finish();
    }
}