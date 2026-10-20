package com.example.kenglish;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DatLaiMatKhauActivity extends AppCompatActivity {

    private EditText edtMatKhauMoi;
    private EditText edtNhapLaiMatKhau;
    private View btnDatLaiMatKhau;
    private TextView txtLoiDatLaiMatKhau;

    private String email;
    private String maXacThuc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datlaimatkhau);

        anhXa();

        email = getIntent().getStringExtra("email");
        maXacThuc = getIntent().getStringExtra("ma_xac_thuc");

        btnDatLaiMatKhau.setOnClickListener(v -> xuLyDatLaiMatKhau());
    }

    private void anhXa() {
        edtMatKhauMoi = findViewById(R.id.edt_mat_khau_moi);
        edtNhapLaiMatKhau = findViewById(R.id.edt_nhap_lai_mat_khau);
        btnDatLaiMatKhau = findViewById(R.id.btn_dat_lai_mat_khau);
        txtLoiDatLaiMatKhau = findViewById(R.id.txt_loi_dat_lai_mat_khau);
    }

    private void xuLyDatLaiMatKhau() {
        String matKhauMoi = edtMatKhauMoi.getText().toString().trim();
        String nhapLaiMatKhau = edtNhapLaiMatKhau.getText().toString().trim();

        txtLoiDatLaiMatKhau.setVisibility(View.GONE);

        if (TextUtils.isEmpty(matKhauMoi)) {
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

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(maXacThuc)) {
            hienLoi("Phiên xác thực không hợp lệ. Vui lòng thực hiện lại.");
            return;
        }

        // Tạm thời chưa gọi API.
        // Bước tiếp theo sẽ gửi:
        // email + maXacThuc + matKhauMoi
        // lên POST /auth/dat-lai-mat-khau
    }

    private void hienLoi(String noiDung) {
        txtLoiDatLaiMatKhau.setText(noiDung);
        txtLoiDatLaiMatKhau.setVisibility(View.VISIBLE);
    }
}