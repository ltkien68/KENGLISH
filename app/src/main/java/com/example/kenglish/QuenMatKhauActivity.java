package com.example.kenglish;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kenglish.api.ApiService;
import com.example.kenglish.api.RetrofitClient;
import com.example.kenglish.model.ApiResponse;
import com.example.kenglish.model.QuenMatKhauRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuenMatKhauActivity extends AppCompatActivity {

    private View khungQuenMatKhau;

    private EditText edtEmail;

    private View btnGuiMa;
    private ImageView txtQuayLaiDangNhap;

    private TextView txtLoiQuenMatKhau;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quenmatkhau);

        anhXa();

        // Animation khi mo man hinh
        hienThiAnimation();

        // Quay lai man dang nhap
        txtQuayLaiDangNhap.setOnClickListener(v -> {

            Intent intent = new Intent(
                    QuenMatKhauActivity.this,
                    DangNhapActivity.class
            );

            startActivity(intent);

            overridePendingTransition(
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
            );

            finish();
        });

        // Gui ma xac thuc
        btnGuiMa.setOnClickListener(v -> {
            btnGuiMa.setEnabled(false);

            // Animation khi nhan nut
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

            quenMatKhau();
        });
    }

    private void anhXa() {

        khungQuenMatKhau =
                findViewById(R.id.khung_quen_mat_khau);

        edtEmail =
                findViewById(R.id.edt_email);

        btnGuiMa =
                findViewById(R.id.btn_gui_ma);

        txtQuayLaiDangNhap =
                findViewById(R.id.txt_quay_lai_dang_nhap);

        txtLoiQuenMatKhau =
                findViewById(R.id.txt_loi_quen_mat_khau);
    }

    private void quenMatKhau() {

        String email =
                edtEmail.getText().toString().trim();

        // Xoa loi cu
        txtLoiQuenMatKhau.setVisibility(View.GONE);

        // Kiem tra email trong
        if (email.isEmpty()) {

            edtEmail.setError("Vui lòng nhập email");
            edtEmail.requestFocus();

            return;
        }

        // Kiem tra dinh dang email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            edtEmail.setError("Email không hợp lệ");
            edtEmail.requestFocus();

            return;
        }

        // Tao request
        QuenMatKhauRequest request =
                new QuenMatKhauRequest(email);

        // Tao API service
        ApiService apiService =
                RetrofitClient
                        .getClient()
                        .create(ApiService.class);

        // Gui request
        apiService.quenMatKhau(request)
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
                                Intent i = new Intent(QuenMatKhauActivity.this, XacThucEmailActivity.class);

                                i.putExtra("email", email);
                                i.putExtra("loai", "dat_lai_mat_khau");

                                startActivity(i);

                                overridePendingTransition(
                                        R.anim.slide_in_left,
                                        R.anim.slide_out_right
                                );

                                finish();
                            } else {

                                hienThiLoi(
                                        ketQua.getThongBao()
                                );
                            }

                        } else {

                            hienThiLoi(
                                    "Không thể gửi mã xác thực"
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse> call,
                            Throwable t
                    ) {

                        Toast.makeText(
                                QuenMatKhauActivity.this,
                                "Không thể kết nối server: "
                                        + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void hienThiLoi(String loi) {

        txtLoiQuenMatKhau.setText(loi);
        txtLoiQuenMatKhau.setVisibility(View.VISIBLE);
    }

    private void hienThiAnimation() {

        khungQuenMatKhau.setAlpha(0f);
        khungQuenMatKhau.setTranslationY(20f);

        khungQuenMatKhau.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(350)
                .start();
    }
}