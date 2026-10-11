package com.example.kenglish;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.kenglish.api.ApiService;
import com.example.kenglish.api.RetrofitClient;
import com.example.kenglish.model.ApiResponse;
import com.example.kenglish.model.HoatDongNamResponse;
import com.example.kenglish.model.LichSuTraTu;
import com.example.kenglish.model.LichSuTraTuResponse;
import com.example.kenglish.model.SuggestionResponse;
import com.example.kenglish.model.ThongKeHocTap;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrangChu extends Fragment {

    // Thông tin người dùng và thống kê
    private TextView txtAnhDaiDien;
    private TextView txtChuoiHoc;
    private TextView txtChuoiHocTuan;
    private TextView txtTenNguoiDung;
    private TextView btnBoTu;
    private TextView txtTongTu;
    private TextView txtDaThuoc;
    private TextView txtTienDo;
    private TextView txtTuDenHan;

    // Bảy ngày trong tuần
    private TextView[] cacNgayTrongTuan;

    // Tra từ
    private EditText edtTraTu;
    private PopupWindow popupGoiY;
    private final Handler handlerTimKiem = new Handler(Looper.getMainLooper());
    private Runnable runnableTimKiem;

    // API
    private ApiService apiService;

    // Các thành phần Trang chủ
    private LinearLayout layoutTuDenHan;
    private LinearLayout layoutLichSuTraTu;
    private LinearLayout btnLuyenTapNhanh;
    private LinearLayout btnTaoBoTuNhanh;
    private LinearLayout btnCongDongNhanh;
    private LinearLayout layoutChuoiHoc;
    private LinearLayout btnXemChuoiHoc;
    private FrameLayout taobotu;
    private ImageView btnXoaLichSu;
    private View khungLichSuTraTu;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.trang_chu,
                container,
                false
        );

        anhXa(view);

        // Hiển thị trạng thái chờ dữ liệu
        hienThiChuoiHoc7Ngay(null);

        layThongTinStreak();
        layThongKeHocTap();
        thietLapSuKienAnhDaiDien();
        thietLapSuKienTuDenHan();
        thietLapSuKienTaoBoTu();
        thietLapTraTu();
        layLichSuTraTu();
        thietLapSuKienXemChuoiHoc();
        thietLapXoaLichSu();
        thietLapSuKienLuyenTapNhanh();
        boFocusKhiChamNgoai(view, edtTraTu);
        layTenNguoiDung();
        thietLapSuKienTaoBoTuNhanh();
        thietLapSuKienCongDongNhanh();

        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden && getView() != null) {
            layThongKeHocTap();
            layThongTinStreak();
        }
    }

    private void anhXa(View view) {

        txtAnhDaiDien = view.findViewById(R.id.txt_anh_dai_dien);
        txtChuoiHoc = view.findViewById(R.id.txt_chuoi_hoc);
        txtTenNguoiDung = view.findViewById(R.id.txt_ten_nguoi_dung);

        taobotu = view.findViewById(R.id.tao_bo_tu);
        btnBoTu = view.findViewById(R.id.btn_tao_bo_tu);

        layoutTuDenHan = view.findViewById(R.id.layout_tudenhan);
        edtTraTu = view.findViewById(R.id.edt_tra_tu);

        apiService = RetrofitClient
                .getClient()
                .create(ApiService.class);

        layoutLichSuTraTu = view.findViewById(R.id.layout_lich_su_tra_tu);
        btnXoaLichSu = view.findViewById(R.id.btn_xoa_lich_su);
        khungLichSuTraTu = view.findViewById(R.id.khung_lich_su_tra_tu);

        btnLuyenTapNhanh = view.findViewById(R.id.btn_luyen_tap_nhanh);
        btnTaoBoTuNhanh = view.findViewById(R.id.btn_tao_bo_tu_nhanh);
        btnCongDongNhanh = view.findViewById(R.id.btn_cong_dong);
        btnXemChuoiHoc = view.findViewById(R.id.btn_xem_chuoi_hoc);

        txtTongTu = view.findViewById(R.id.txt_tong_tu);
        txtDaThuoc = view.findViewById(R.id.txt_da_thuoc);
        txtTienDo = view.findViewById(R.id.txt_tien_do);
        txtTuDenHan = view.findViewById(R.id.txt_tudenhan);

        layoutChuoiHoc = view.findViewById(R.id.khung_chuoi_hoc);

        // Các thành phần mới của lịch học tập
        txtChuoiHocTuan = view.findViewById(R.id.txt_chuoi_hoc_tuan);

        cacNgayTrongTuan = new TextView[]{
                view.findViewById(R.id.ngay_t2),
                view.findViewById(R.id.ngay_t3),
                view.findViewById(R.id.ngay_t4),
                view.findViewById(R.id.ngay_t5),
                view.findViewById(R.id.ngay_t6),
                view.findViewById(R.id.ngay_t7),
                view.findViewById(R.id.ngay_cn)
        };
    }

    // ==================================================
    // CHUỖI HỌC TẬP - BACKEND VÀ LỊCH 7 NGÀY
    // ==================================================

    private void layThongTinStreak() {

        SharedPreferences sharedPreferences =
                requireContext().getSharedPreferences(
                        "Kenglish",
                        Context.MODE_PRIVATE
                );

        String token = sharedPreferences.getString("token", null);

        if (token == null) {
            return;
        }

        apiService.layHoatDongNamHienTai("Bearer " + token)
                .enqueue(new Callback<ApiResponse<HoatDongNamResponse>>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<ApiResponse<HoatDongNamResponse>> call,
                            @NonNull Response<ApiResponse<HoatDongNamResponse>> response) {

                        if (!isAdded() || getView() == null) {
                            return;
                        }

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isThanhCong()
                                && response.body().getData() != null) {

                            HoatDongNamResponse duLieu =
                                    response.body().getData();

                            int streak = duLieu.getCurrentStreak();

                            // Streak ở header
                            txtChuoiHoc.setText(String.valueOf(streak));

                            if (streak == 0) {
                                layoutChuoiHoc.setBackgroundTintList(
                                        ColorStateList.valueOf(
                                                Color.parseColor("#F5F5F6")
                                        )
                                );
                            } else {
                                layoutChuoiHoc.setBackgroundTintList(null);
                            }

                            // Streak trong card tuần
                            txtChuoiHocTuan.setText(
                                    streak + " ngày liên tiếp"
                            );

                            // Lấy dữ liệu ngày từ cùng response
                            hienThiChuoiHoc7Ngay(
                                    duLieu.getNgayHoatDong()
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<ApiResponse<HoatDongNamResponse>> call,
                            @NonNull Throwable t) {
                        // Giữ trạng thái cũ khi mất kết nối.
                    }
                });
    }

    /**
     * Hiển thị thứ Hai đến Chủ nhật của tuần hiện tại.
     *
     * ngayHoatDong:
     * Danh sách ngày yyyy-MM-dd từ backend.
     */
    private void hienThiChuoiHoc7Ngay(List<String> ngayHoatDong) {

        if (cacNgayTrongTuan == null) {
            return;
        }

        Set<String> tapNgayHoatDong = new HashSet<>();

        if (ngayHoatDong != null) {
            tapNgayHoatDong.addAll(ngayHoatDong);
        }

        Calendar homNay = Calendar.getInstance();

        // Clone để không thay đổi đối tượng hôm nay.
        Calendar thuHai = (Calendar) homNay.clone();

        // Calendar: CN = 1, T2 = 2, ..., T7 = 7
        int thuTrongTuan = thuHai.get(Calendar.DAY_OF_WEEK);

        int soNgayLui = (thuTrongTuan + 5) % 7;

        thuHai.add(Calendar.DAY_OF_MONTH, -soNgayLui);

        SimpleDateFormat dinhDangNgay =
                new SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.US
                );

        String ngayHomNay = dinhDangNgay.format(
                homNay.getTime()
        );

        for (int i = 0; i < 7; i++) {

            Calendar ngayDangXet = (Calendar) thuHai.clone();

            ngayDangXet.add(Calendar.DAY_OF_MONTH, i);

            String ngay = dinhDangNgay.format(
                    ngayDangXet.getTime()
            );

            boolean daHoc = tapNgayHoatDong.contains(ngay);

            boolean laHomNay = ngay.equals(ngayHomNay);

            boolean laNgayTuongLai =
                    ngayDangXet.after(homNay);

            capNhatOTheoNgay(
                    cacNgayTrongTuan[i],
                    ngayDangXet.get(Calendar.DAY_OF_MONTH),
                    daHoc,
                    laHomNay,
                    laNgayTuongLai
            );
        }
    }

    /**
     * Cập nhật giao diện của từng ô ngày.
     *
     * Đã học: nền xanh, tick trắng.
     * Chưa học: nền xanh nhạt, hiện ngày.
     * Hôm nay: có viền xanh.
     * Tương lai: màu xám nhạt.
     */
    private void capNhatOTheoNgay(
            TextView oNgay,
            int soNgay,
            boolean daHoc,
            boolean laHomNay,
            boolean laNgayTuongLai) {

        if (oNgay == null) {
            return;
        }

        int mauNen;
        int mauChu;

        if (daHoc) {
            mauNen = Color.parseColor("#087CFF");
            mauChu = Color.WHITE;
            oNgay.setText("✓");
        } else {
            oNgay.setText(String.valueOf(soNgay));

            if (laNgayTuongLai) {
                mauNen = Color.parseColor("#F1F4F8");
                mauChu = Color.parseColor("#A7B1BF");
            } else {
                mauNen = Color.parseColor("#EDF6FF");
                mauChu = Color.parseColor("#7185A3");
            }
        }

        GradientDrawable nen = new GradientDrawable();

        nen.setShape(GradientDrawable.RECTANGLE);
        nen.setColor(mauNen);
        nen.setCornerRadius(dpToPx(18));

        if (laHomNay) {
            nen.setStroke(
                    dpToPx(2),
                    Color.parseColor("#082F70")
            );
        }

        oNgay.setBackground(nen);
        oNgay.setTextColor(mauChu);
    }

    // ==================================================
    // THÔNG TIN NGƯỜI DÙNG VÀ THỐNG KÊ
    // ==================================================

    private void layTenNguoiDung() {

        SharedPreferences sharedPreferences =
                requireContext().getSharedPreferences(
                        "Kenglish",
                        Context.MODE_PRIVATE
                );

        String tenHienThi =
                sharedPreferences.getString(
                        "ten_hien_thi",
                        "Người dùng"
                );

        txtTenNguoiDung.setText(tenHienThi);
    }

    private void layThongKeHocTap() {

        SharedPreferences sharedPreferences =
                requireContext().getSharedPreferences(
                        "Kenglish",
                        Context.MODE_PRIVATE
                );

        String token = sharedPreferences.getString("token", null);

        if (token == null) {
            return;
        }

        apiService.layThongKeHocTap("Bearer " + token)
                .enqueue(new Callback<ApiResponse<ThongKeHocTap>>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<ApiResponse<ThongKeHocTap>> call,
                            @NonNull Response<ApiResponse<ThongKeHocTap>> response) {

                        if (!isAdded() || getView() == null) {
                            return;
                        }

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().getData() != null) {

                            ThongKeHocTap thongKe =
                                    response.body().getData();

                            txtTongTu.setText(
                                    String.valueOf(thongKe.getTongTu())
                            );

                            txtDaThuoc.setText(
                                    String.valueOf(thongKe.getDaThuoc())
                            );

                            txtTienDo.setText(
                                    thongKe.getTienDo() + "%"
                            );

                            txtTuDenHan.setText(
                                    String.valueOf(thongKe.getTuDenHan())
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<ApiResponse<ThongKeHocTap>> call,
                            @NonNull Throwable t) {
                    }
                });
    }

    // ==================================================
    // ĐIỀU HƯỚNG
    // ==================================================

    private void chuyenDenTab(int menuId) {

        BottomNavigationView thanhDieuHuong =
                requireActivity().findViewById(
                        R.id.thanh_dieu_huong
                );

        thanhDieuHuong.setSelectedItemId(menuId);
    }

    private void thietLapSuKienAnhDaiDien() {

        txtAnhDaiDien.setOnClickListener(v ->
                chuyenDenTab(R.id.menu_ca_nhan)
        );
    }

    private void thietLapSuKienXemChuoiHoc() {
        btnXemChuoiHoc.setOnClickListener(v -> chuyenDenTab(R.id.menu_ca_nhan));
    }

    private void thietLapSuKienTuDenHan() {

        layoutTuDenHan.setOnClickListener(v ->
                chuyenDenTab(R.id.menu_luyen_tap)
        );
    }

    private void thietLapSuKienLuyenTapNhanh() {

        btnLuyenTapNhanh.setOnClickListener(v ->
                chuyenDenTab(R.id.menu_luyen_tap)
        );
    }

    private void thietLapSuKienTaoBoTuNhanh() {

        btnTaoBoTuNhanh.setOnClickListener(v ->
                chuyenDenTab(R.id.menu_bo_tu)
        );
    }

    private void thietLapSuKienCongDongNhanh() {

        btnCongDongNhanh.setOnClickListener(v ->
                chuyenDenTab(R.id.menu_xep_hang)
        );
    }

    private void thietLapSuKienTaoBoTu() {

        taobotu.setOnClickListener(v ->
                chuyenDenTab(R.id.menu_bo_tu)
        );
    }

    // ==================================================
    // TRA TỪ VÀ GỢI Ý
    // ==================================================

    private void thietLapTraTu() {

        edtTraTu.addTextChangedListener(new TextWatcher() {

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

                if (runnableTimKiem != null) {
                    handlerTimKiem.removeCallbacks(runnableTimKiem);
                }

                String tuKhoa = s.toString().trim();

                if (tuKhoa.isEmpty()) {
                    dongGoiY();
                    return;
                }

                runnableTimKiem = () -> timKiemTu(tuKhoa);

                handlerTimKiem.postDelayed(
                        runnableTimKiem,
                        400
                );
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void timKiemTu(String tuKhoa) {

        apiService.timKiemTu(tuKhoa)
                .enqueue(new Callback<SuggestionResponse>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<SuggestionResponse> call,
                            @NonNull Response<SuggestionResponse> response) {

                        if (!isAdded() || getView() == null) {
                            return;
                        }

                        // Bỏ response cũ nếu người dùng đã đổi từ khóa.
                        String tuHienTai =
                                edtTraTu.getText().toString().trim();

                        if (!tuKhoa.equals(tuHienTai)) {
                            return;
                        }

                        if (response.isSuccessful()
                                && response.body() != null) {

                            hienThiGoiY(
                                    response.body().getSuggestions()
                            );
                        } else {
                            dongGoiY();
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<SuggestionResponse> call,
                            @NonNull Throwable t) {

                        if (!isAdded() || getView() == null) {
                            return;
                        }

                        if (tuKhoa.equals(
                                edtTraTu.getText().toString().trim()
                        )) {
                            dongGoiY();
                        }
                    }
                });
    }

    private void hienThiGoiY(List<String> danhSachTu) {

        if (danhSachTu == null || danhSachTu.isEmpty()) {
            dongGoiY();
            return;
        }

        dongGoiY();

        View viewPopup = LayoutInflater
                .from(requireContext())
                .inflate(
                        R.layout.trangchu_tratu_popup_goiytu,
                        null
                );

        LinearLayout layoutDanhSach =
                viewPopup.findViewById(
                        R.id.layout_danh_sach_goi_y
                );

        for (String tu : danhSachTu) {

            View item = LayoutInflater
                    .from(requireContext())
                    .inflate(
                            R.layout.trangchu_tratu_goiytu,
                            layoutDanhSach,
                            false
                    );

            TextView txtTu = item.findViewById(
                    R.id.txt_tu_goi_y
            );

            txtTu.setText(tu);

            item.setOnClickListener(v -> moChiTietTu(tu));

            layoutDanhSach.addView(item);
        }

        TextInputLayout khungTraTu =
                requireView().findViewById(
                        R.id.khung_tra_tu
                );

        popupGoiY = new PopupWindow(
                viewPopup,
                khungTraTu.getWidth(),
                ViewGroup.LayoutParams.WRAP_CONTENT,
                false
        );

        popupGoiY.setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT)
        );

        popupGoiY.setOutsideTouchable(true);
        popupGoiY.setElevation(dpToPx(6));

        popupGoiY.showAsDropDown(
                khungTraTu,
                0,
                dpToPx(6)
        );
    }

    private void dongGoiY() {

        if (popupGoiY != null && popupGoiY.isShowing()) {
            popupGoiY.dismiss();
        }
    }

    private void boFocusKhiChamNgoai(View viewGoc, EditText edtTraTu) {
        viewGoc.setFocusableInTouchMode(true);

        viewGoc.setOnTouchListener((v, event) -> {
            if (event.getAction() == android.view.MotionEvent.ACTION_DOWN
                    && edtTraTu.hasFocus()) {

                edtTraTu.clearFocus();

                InputMethodManager imm =
                        (InputMethodManager) requireContext()
                                .getSystemService(Context.INPUT_METHOD_SERVICE);

                if (imm != null) {
                    imm.hideSoftInputFromWindow(
                            edtTraTu.getWindowToken(),
                            0
                    );
                }
            }

            return false;
        });
    }

    private int dpToPx(int dp) {

        return (int) (
                dp * getResources()
                        .getDisplayMetrics()
                        .density
        );
    }

    @Override
    public void onDestroyView() {

        if (runnableTimKiem != null) {
            handlerTimKiem.removeCallbacks(runnableTimKiem);
        }

        dongGoiY();

        super.onDestroyView();
    }

    // ==================================================
    // LỊCH SỬ TRA TỪ
    // ==================================================

    private void layLichSuTraTu() {

        SharedPreferences sharedPreferences =
                requireContext().getSharedPreferences(
                        "Kenglish",
                        Context.MODE_PRIVATE
                );

        String token = sharedPreferences.getString("token", null);

        if (token == null) {
            khungLichSuTraTu.setVisibility(View.GONE);
            return;
        }

        apiService.layLichSuTraTu("Bearer " + token)
                .enqueue(new Callback<LichSuTraTuResponse>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<LichSuTraTuResponse> call,
                            @NonNull Response<LichSuTraTuResponse> response) {

                        if (!isAdded() || getView() == null) {
                            return;
                        }

                        if (response.isSuccessful()
                                && response.body() != null) {

                            hienThiLichSu(
                                    response.body().getHistory()
                            );
                        } else {
                            khungLichSuTraTu.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<LichSuTraTuResponse> call,
                            @NonNull Throwable t) {

                        if (!isAdded() || getView() == null) {
                            return;
                        }

                        khungLichSuTraTu.setVisibility(View.GONE);
                    }
                });
    }

    private void hienThiLichSu(List<LichSuTraTu> danhSach) {

        layoutLichSuTraTu.removeAllViews();

        if (danhSach == null || danhSach.isEmpty()) {
            khungLichSuTraTu.setVisibility(View.GONE);
            return;
        }

        khungLichSuTraTu.setVisibility(View.VISIBLE);

        for (int i = 0; i < danhSach.size(); i++) {

            LichSuTraTu lichSu = danhSach.get(i);

            TextView txtTu = new TextView(requireContext());

            txtTu.setText(lichSu.getWord());
            txtTu.setTextSize(13);
            txtTu.setTextColor(Color.parseColor("#111111"));

            txtTu.setTypeface(
                    android.graphics.Typeface.create(
                            "sans-serif-medium",
                            android.graphics.Typeface.NORMAL
                    )
            );

            txtTu.setGravity(Gravity.CENTER_VERTICAL);

            txtTu.setPadding(
                    dpToPx(14),
                    0,
                    dpToPx(14),
                    0
            );

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            dpToPx(48)
                    );

            txtTu.setLayoutParams(params);

            txtTu.setOnClickListener(v ->
                    moChiTietTu(lichSu.getWord())
            );

            layoutLichSuTraTu.addView(txtTu);

            if (i < danhSach.size() - 1) {

                View divider = new View(requireContext());

                divider.setBackgroundColor(
                        Color.parseColor("#E2E3E3")
                );

                LinearLayout.LayoutParams dividerParams =
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                dpToPx(1)
                        );

                dividerParams.setMarginStart(dpToPx(14));
                dividerParams.setMarginEnd(dpToPx(14));

                divider.setLayoutParams(dividerParams);

                layoutLichSuTraTu.addView(divider);
            }
        }
    }

    private void moChiTietTu(String tu) {

        dongGoiY();
        edtTraTu.setText("");

        Bundle bundle = new Bundle();
        bundle.putString("tu", tu);

        ChiTietTu chiTietTu = new ChiTietTu();
        chiTietTu.setArguments(bundle);

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.khung_noi_dung,
                        chiTietTu
                )
                .addToBackStack(null)
                .commit();
    }

    private void thietLapXoaLichSu() {

        btnXoaLichSu.setOnClickListener(v ->
                xoaLichSuTraTu()
        );
    }

    private void xoaLichSuTraTu() {

        SharedPreferences sharedPreferences =
                requireContext().getSharedPreferences(
                        "Kenglish",
                        Context.MODE_PRIVATE
                );

        String token = sharedPreferences.getString("token", null);

        if (token == null) {
            return;
        }

        apiService.xoaLichSuTraTu("Bearer " + token)
                .enqueue(new Callback<ApiResponse>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<ApiResponse> call,
                            @NonNull Response<ApiResponse> response) {

                        if (!isAdded() || getView() == null) {
                            return;
                        }

                        if (response.isSuccessful()) {

                            layoutLichSuTraTu.removeAllViews();

                            khungLichSuTraTu.setVisibility(
                                    View.GONE
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<ApiResponse> call,
                            @NonNull Throwable t) {

                        if (!isAdded() || getView() == null) {
                            return;
                        }

                        Toast.makeText(
                                requireContext(),
                                "Không thể xóa lịch sử",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }
}
