package com.example.kenglish;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.util.AttributeSet;
import android.view.inputmethod.InputMethodManager;
import android.view.View;

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
    private TextView txtGuiLai;

    private final EditText[] danhSachOtp = new EditText[6];

    private String email;
    private boolean dangCapNhatOtp = false;
    private boolean dangGuiXacThuc = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xacthucemail);

        anhXa();
        khoiTaoOtp();

        email = getIntent().getStringExtra("email");

        if (email != null && !email.trim().isEmpty()) {
            txtEmail.setText(
                    "Mã xác thực đã được gửi tới\n" + email
            );
        }

        btnXacThuc.setOnClickListener(v -> xacThucEmail());

        txtGuiLai.setOnClickListener(v -> {
            Toast.makeText(
                    this,
                    "Chức năng gửi lại mã chưa được kết nối",
                    Toast.LENGTH_SHORT
            ).show();
        });
    }

    private void anhXa() {
        txtEmail = findViewById(R.id.txt_email);
        btnXacThuc = findViewById(R.id.btn_xac_thuc);
        txtGuiLai = findViewById(R.id.txt_gui_lai);
    }

    /**
     * Khởi tạo sáu ô nhập OTP.
     */
    private void khoiTaoOtp() {

        int[] ids = {
                R.id.edt_otp_1,
                R.id.edt_otp_2,
                R.id.edt_otp_3,
                R.id.edt_otp_4,
                R.id.edt_otp_5,
                R.id.edt_otp_6
        };

        for (int i = 0; i < ids.length; i++) {
            danhSachOtp[i] = findViewById(ids[i]);
        }

        for (int i = 0; i < danhSachOtp.length; i++) {

            final int viTri = i;
            EditText oHienTai = danhSachOtp[i];

            oHienTai.setInputType(InputType.TYPE_CLASS_NUMBER);
            oHienTai.setSingleLine(true);
            oHienTai.setSelectAllOnFocus(true);

            // Cho phép nhận chuỗi dài khi dán OTP.
            // TextWatcher sẽ phân phối về sáu ô.
            oHienTai.setFilters(new InputFilter[]{
                    new InputFilter.LengthFilter(6)
            });

            oHienTai.setImeOptions(
                    i == 5
                            ? EditorInfo.IME_ACTION_DONE
                            : EditorInfo.IME_ACTION_NEXT
            );

            oHienTai.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(
                        CharSequence s,
                        int start,
                        int count,
                        int after) {
                }

                @Override
                public void onTextChanged(
                        CharSequence s,
                        int start,
                        int before,
                        int count) {

                    if (dangCapNhatOtp) {
                        return;
                    }

                    String giaTri = s.toString();

                    if (giaTri.length() > 1) {
                        phanPhoiMaOtp(viTri, giaTri);
                        return;
                    }

                    if (giaTri.length() == 1 && viTri < 5) {
                        danhSachOtp[viTri + 1].requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            oHienTai.setOnKeyListener((v, keyCode, event) -> {

                if (keyCode == KeyEvent.KEYCODE_DEL
                        && event.getAction() == KeyEvent.ACTION_DOWN
                        && oHienTai.length() == 0
                        && viTri > 0) {

                    xoaOTruoc(viTri);
                    return true;
                }

                return false;
            });

            oHienTai.setOnEditorActionListener(
                    (v, actionId, event) -> {

                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            xacThucEmail();
                            return true;
                        }

                        return false;
                    }
            );
        }

        danhSachOtp[0].requestFocus();

        // Không tự mở bàn phím khi vừa vào màn.
        getWindow().setSoftInputMode(
                android.view.WindowManager.LayoutParams
                        .SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
    }

    /**
     * Dán nhiều chữ số vào một ô.
     * Phân phối các số vào các ô tiếp theo.
     */
    private void phanPhoiMaOtp(int viTriBatDau, String noiDung) {

        String chiChuaSo = noiDung.replaceAll("[^0-9]", "");

        if (chiChuaSo.isEmpty()) {
            return;
        }

        dangCapNhatOtp = true;

        int viTriCuoi = viTriBatDau;

        for (int i = 0;
             i < chiChuaSo.length()
                     && viTriBatDau + i < danhSachOtp.length;
             i++) {

            int viTri = viTriBatDau + i;

            danhSachOtp[viTri].setText(
                    String.valueOf(chiChuaSo.charAt(i))
            );

            viTriCuoi = viTri;
        }

        dangCapNhatOtp = false;

        if (viTriCuoi < 5) {
            danhSachOtp[viTriCuoi + 1].requestFocus();
        } else {
            danhSachOtp[5].requestFocus();
            danhSachOtp[5].setSelection(
                    danhSachOtp[5].length()
            );
        }
    }

    /**
     * Xóa ô trước và đưa focus về ô đó.
     */
    private void xoaOTruoc(int viTriHienTai) {

        if (viTriHienTai <= 0) {
            return;
        }

        EditText oTruoc = danhSachOtp[viTriHienTai - 1];

        oTruoc.requestFocus();
        oTruoc.setText("");
    }

    /**
     * Ghép sáu ô thành mã OTP.
     */
    private String layMaOtp() {

        StringBuilder maOtp = new StringBuilder();

        for (EditText oNhap : danhSachOtp) {
            maOtp.append(oNhap.getText().toString().trim());
        }

        return maOtp.toString();
    }

    /**
     * Kiểm tra OTP trước khi gửi.
     */
    private void xacThucEmail() {

        if (dangGuiXacThuc) {
            return;
        }

        String maXacThuc = layMaOtp();

        for (int i = 0; i < danhSachOtp.length; i++) {

            if (danhSachOtp[i].length() != 1) {

                Toast.makeText(
                        this,
                        "Vui lòng nhập đủ 6 chữ số",
                        Toast.LENGTH_SHORT
                ).show();

                danhSachOtp[i].requestFocus();
                return;
            }
        }

        if (!maXacThuc.matches("[0-9]{6}")) {

            Toast.makeText(
                    this,
                    "Mã xác thực không hợp lệ",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (email == null || email.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Không tìm thấy email cần xác thực",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        guiYeuCauXacThuc(maXacThuc);
    }

    /**
     * Gửi mã OTP đến backend.
     */
    private void guiYeuCauXacThuc(String maXacThuc) {

        dangGuiXacThuc = true;
        btnXacThuc.setEnabled(false);
        btnXacThuc.setAlpha(0.6f);

        XacThucEmailRequest request =
                new XacThucEmailRequest(email, maXacThuc);

        ApiService apiService =
                RetrofitClient.getClient()
                        .create(ApiService.class);

        apiService.xacThucEmail(request)
                .enqueue(new Callback<ApiResponse>() {

                    @Override
                    public void onResponse(
                            Call<ApiResponse> call,
                            Response<ApiResponse> response) {

                        dangGuiXacThuc = false;
                        btnXacThuc.setEnabled(true);
                        btnXacThuc.setAlpha(1f);

                        if (response.isSuccessful()
                                && response.body() != null) {

                            ApiResponse ketQua = response.body();

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
                            Throwable t) {

                        dangGuiXacThuc = false;
                        btnXacThuc.setEnabled(true);
                        btnXacThuc.setAlpha(1f);

                        Toast.makeText(
                                XacThucEmailActivity.this,
                                "Không thể kết nối server: "
                                        + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    /**
     * Mở màn hình đăng nhập sau khi xác thực.
     */
    private void moManHinhDangNhap() {

        Intent intent = new Intent(
                XacThucEmailActivity.this,
                DangNhapActivity.class
        );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_NEW_TASK
        );

        startActivity(intent);
        finish();
    }
}
