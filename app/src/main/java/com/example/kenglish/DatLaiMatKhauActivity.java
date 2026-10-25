
package com.example.kenglish;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kenglish.api.ApiService;
import com.example.kenglish.api.RetrofitClient;
import com.example.kenglish.model.ApiResponse;
import com.example.kenglish.model.DatLaiMatKhauRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DatLaiMatKhauActivity extends AppCompatActivity {

    private EditText edtMatKhauMoi;
    private EditText edtNhapLaiMatKhau;
    private View btnDatLaiMatKhau;
    private TextView txtLoiDatLaiMatKhau;

    private String email;
    private String maXacThuc;

    private boolean dangGuiYeuCau = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datlaimatkhau);

        anhXa();

        email = getIntent().getStringExtra("email");
        maXacThuc = getIntent().getStringExtra("ma_xac_thuc");

        btnDatLaiMatKhau.setOnClickListener(
                v -> xuLyDatLaiMatKhau()
        );
    }

    private void anhXa() {
        edtMatKhauMoi = findViewById(R.id.edt_mat_khau_moi);
        edtNhapLaiMatKhau = findViewById(R.id.edt_nhap_lai_mat_khau);
        btnDatLaiMatKhau = findViewById(R.id.btn_dat_lai_mat_khau);
        txtLoiDatLaiMatKhau = findViewById(R.id.txt_loi_dat_lai_mat_khau);
    }

    private void xuLyDatLaiMatKhau() {
        if (dangGuiYeuCau) {
            return;
        }

        String matKhauMoi =
                edtMatKhauMoi.getText().toString();

        String nhapLaiMatKhau =
                edtNhapLaiMatKhau.getText().toString();

        txtLoiDatLaiMatKhau.setVisibility(View.GONE);

        if (TextUtils.isEmpty(matKhauMoi.trim())) {
            hienLoi("Vui lòng nhập mật khẩu mới.");
            return;
        }

        if (matKhauMoi.length() < 6) {
            hienLoi("Mật khẩu phải có ít nhất 6 ký tự.");
            return;
        }

        if (TextUtils.isEmpty(nhapLaiMatKhau)) {
            hienLoi("Vui lòng nhập lại mật khẩu.");
            return;
        }

        if (!matKhauMoi.equals(nhapLaiMatKhau)) {
            hienLoi("Mật khẩu nhập lại không khớp.");
            return;
        }

        if (TextUtils.isEmpty(email)
                || TextUtils.isEmpty(maXacThuc)) {
            hienLoi(
                    "Phiên xác thực không hợp lệ. Vui lòng thực hiện lại."
            );
            return;
        }

        guiYeuCauDatLaiMatKhau(matKhauMoi);
    }

    private void guiYeuCauDatLaiMatKhau(String matKhauMoi) {
        dangGuiYeuCau = true;
        btnDatLaiMatKhau.setEnabled(false);
        btnDatLaiMatKhau.setAlpha(0.6f);

        DatLaiMatKhauRequest request =
                new DatLaiMatKhauRequest(
                        email,
                        maXacThuc,
                        matKhauMoi
                );

        ApiService apiService =
                RetrofitClient.getClient()
                        .create(ApiService.class);

        apiService.datLaiMatKhau(request)
                .enqueue(new Callback<ApiResponse>() {

                    @Override
                    public void onResponse(
                            Call<ApiResponse> call,
                            Response<ApiResponse> response
                    ) {
                        dangGuiYeuCau = false;
                        btnDatLaiMatKhau.setEnabled(true);
                        btnDatLaiMatKhau.setAlpha(1f);

                        if (response.isSuccessful()
                                && response.body() != null) {

                            ApiResponse ketQua = response.body();

                            if (ketQua.isThanhCong()) {
                                Toast.makeText(
                                        DatLaiMatKhauActivity.this,
                                        ketQua.getThongBao(),
                                        Toast.LENGTH_SHORT
                                ).show();

                                Intent intent = new Intent(
                                        DatLaiMatKhauActivity.this,
                                        DangNhapActivity.class
                                );

                                intent.addFlags(
                                        Intent.FLAG_ACTIVITY_CLEAR_TOP
                                                | Intent.FLAG_ACTIVITY_NEW_TASK
                                                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                );

                                startActivity(intent);

                                overridePendingTransition(
                                        R.anim.slide_in_left,
                                        R.anim.slide_out_right
                                );

                                finish();

                            } else {
                                hienLoi(ketQua.getThongBao());
                            }

                        } else {
                            hienLoi(
                                    "Không thể đặt lại mật khẩu. Mã lỗi: "
                                            + response.code()
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse> call,
                            Throwable t
                    ) {
                        dangGuiYeuCau = false;
                        btnDatLaiMatKhau.setEnabled(true);
                        btnDatLaiMatKhau.setAlpha(1f);

                        hienLoi(
                                "Không thể kết nối server. Vui lòng thử lại."
                        );
                    }
                });
    }

    private void hienLoi(String noiDung) {
        txtLoiDatLaiMatKhau.setText(noiDung);
        txtLoiDatLaiMatKhau.setVisibility(View.VISIBLE);
    }
}
