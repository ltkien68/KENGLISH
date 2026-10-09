package com.example.kenglish;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.kenglish.api.ApiService;
import com.example.kenglish.api.RetrofitClient;
import com.example.kenglish.model.ApiResponse;
import com.example.kenglish.model.KetQuaTuVung;
import com.example.kenglish.model.LuuTienTrinhRequest;
import com.example.kenglish.model.LuuTienTrinhResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KetQuaLuyenTap extends Fragment {

    private static final String KEY_TEN_GAME = "ten_game";
    private static final String KEY_KET_QUA = "ket_qua";
    private static final String KEY_MA_PHIEN = "ma_phien_choi";
    private static final String KEY_DA_LUU = "da_luu_thanh_cong";

    private final List<KetQuaTuVung> danhSachKetQua =
            new ArrayList<>();

    private TextView txtTenGame;
    private TextView txtPhanTram;
    private TextView txtDanhGia;
    private TextView txtTongKet;
    private TextView txtSoTuDaThuoc;
    private TextView txtSoTuChuaThuoc;
    private TextView txtStreak;
    private TextView btnLuuTienTrinh;

    private LinearLayout layoutDaThuoc;
    private LinearLayout layoutChuaThuoc;

    private ApiService apiService;

    private String tenGame = "Luyện tập";
    private String maPhienChoi;

    private boolean dangLuu = false;
    private boolean daLuuThanhCong = false;

    private Call<ApiResponse<LuuTienTrinhResponse>> callLuu;

    public static KetQuaLuyenTap newInstance(
            String tenGame,
            List<KetQuaTuVung> ketQua) {

        KetQuaLuyenTap fragment =
                new KetQuaLuyenTap();

        Bundle bundle = new Bundle();

        bundle.putString(
                KEY_TEN_GAME,
                tenGame
        );

        bundle.putSerializable(
                KEY_KET_QUA,
                new ArrayList<>(ketQua)
        );

        bundle.putString(
                KEY_MA_PHIEN,
                UUID.randomUUID().toString()
        );

        fragment.setArguments(bundle);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.ketqua_luyentap,
                container,
                false
        );

        anhXa(view);
        layDuLieu();

        if (savedInstanceState != null) {
            daLuuThanhCong = savedInstanceState.getBoolean(
                    KEY_DA_LUU,
                    false
            );
        }

        hienThiKetQua();
        xuLySuKien();
        capNhatNutLuu();

        return view;
    }

    private void anhXa(View view) {

        txtTenGame = view.findViewById(
                R.id.txt_ten_game
        );

        txtPhanTram = view.findViewById(
                R.id.txt_phan_tram
        );

        txtDanhGia = view.findViewById(
                R.id.txt_danh_gia
        );

        txtTongKet = view.findViewById(
                R.id.txt_tong_ket
        );

        txtSoTuDaThuoc = view.findViewById(
                R.id.txt_so_tu_da_thuoc
        );

        txtSoTuChuaThuoc = view.findViewById(
                R.id.txt_so_tu_chua_thuoc
        );

        txtStreak = view.findViewById(
                R.id.txt_streak_ket_qua
        );

        layoutDaThuoc = view.findViewById(
                R.id.layout_da_thuoc
        );

        layoutChuaThuoc = view.findViewById(
                R.id.layout_chua_thuoc
        );

        btnLuuTienTrinh = view.findViewById(
                R.id.btn_luu_tien_trinh
        );

        apiService = RetrofitClient.getClient()
                .create(ApiService.class);
    }

    @SuppressWarnings("unchecked")
    private void layDuLieu() {

        Bundle bundle = getArguments();

        if (bundle == null) {
            return;
        }

        tenGame = bundle.getString(
                KEY_TEN_GAME,
                "Luyện tập"
        );

        maPhienChoi = bundle.getString(
                KEY_MA_PHIEN
        );

        if (maPhienChoi == null ||
                maPhienChoi.trim().isEmpty()) {

            maPhienChoi =
                    UUID.randomUUID().toString();

            bundle.putString(
                    KEY_MA_PHIEN,
                    maPhienChoi
            );
        }

        Object duLieu = bundle.getSerializable(
                KEY_KET_QUA
        );

        if (duLieu instanceof ArrayList) {

            danhSachKetQua.clear();

            danhSachKetQua.addAll(
                    (ArrayList<KetQuaTuVung>) duLieu
            );
        }
    }

    private void hienThiKetQua() {

        txtTenGame.setText(tenGame);

        layoutDaThuoc.removeAllViews();
        layoutChuaThuoc.removeAllViews();

        int soDaThuoc = 0;
        int soChuaThuoc = 0;

        for (KetQuaTuVung ketQua : danhSachKetQua) {

            if (ketQua.getDa_thuoc() == 1) {

                soDaThuoc++;

                themItemKetQua(
                        layoutDaThuoc,
                        ketQua,
                        true
                );

            } else {

                soChuaThuoc++;

                themItemKetQua(
                        layoutChuaThuoc,
                        ketQua,
                        false
                );
            }
        }

        int tongSoTu = danhSachKetQua.size();

        int phanTram = tongSoTu == 0
                ? 0
                : soDaThuoc * 100 / tongSoTu;

        txtPhanTram.setText(
                phanTram + "%"
        );

        txtTongKet.setText(
                soDaThuoc + " đã thuộc · "
                        + soChuaThuoc + " chưa thuộc"
        );

        txtSoTuDaThuoc.setText(
                soDaThuoc + " từ"
        );

        txtSoTuChuaThuoc.setText(
                soChuaThuoc + " từ"
        );

        if (tongSoTu == 0) {

            txtDanhGia.setText(
                    "Chưa có kết quả"
            );

        } else if (phanTram == 100) {

            txtDanhGia.setText(
                    "Xuất sắc!"
            );

        } else if (phanTram >= 70) {

            txtDanhGia.setText(
                    "Làm tốt lắm!"
            );

        } else {

            txtDanhGia.setText(
                    "Tiếp tục cố gắng!"
            );
        }
    }

    private void themItemKetQua(
            LinearLayout container,
            KetQuaTuVung ketQua,
            boolean daThuoc) {

        View item = LayoutInflater
                .from(requireContext())
                .inflate(
                        R.layout.ketqua_luyentap_item,
                        container,
                        false
                );

        TextView txtTrangThai =
                item.findViewById(
                        R.id.txt_trang_thai_item
                );

        TextView txtTu =
                item.findViewById(
                        R.id.txt_tu_item
                );

        TextView txtNghia =
                item.findViewById(
                        R.id.txt_nghia_item
                );

        txtTrangThai.setText(
                daThuoc ? "✓" : "✕"
        );

        txtTrangThai.setTextColor(
                Color.parseColor(
                        daThuoc
                                ? "#159947"
                                : "#D83A42"
                )
        );

        txtTu.setText(
                ketQua.getTu_goc()
        );

        txtNghia.setText(
                ketQua.getNghia_tieng_viet()
        );

        container.addView(item);
    }

    private void xuLySuKien() {

        btnLuuTienTrinh.setOnClickListener(v -> {

            if (!dangLuu && !daLuuThanhCong) {
                luuTienTrinh();
            }
        });
    }

    private String layToken() {

        SharedPreferences preferences =
                requireContext().getSharedPreferences(
                        "Kenglish",
                        Context.MODE_PRIVATE
                );

        return preferences.getString(
                "token",
                null
        );
    }

    private String layMaCheDo() {

        if ("Flashcard".equalsIgnoreCase(tenGame)) {
            return "flashcard";
        }

        if ("Trắc nghiệm".equalsIgnoreCase(tenGame)) {
            return "trac_nghiem";
        }

        if ("Nối từ với nghĩa".equalsIgnoreCase(tenGame)) {
            return "noi_tu";
        }

        if ("Gõ từ vựng".equalsIgnoreCase(tenGame)) {
            return "go_tu";
        }

        if ("Nghe viết".equalsIgnoreCase(tenGame)) {
            return "nghe_viet";
        }

        if ("Đặc biệt".equalsIgnoreCase(tenGame)) {
            return "dac_biet";
        }

        return null;
    }

    private void luuTienTrinh() {

        if (danhSachKetQua.isEmpty()) {
            thongBao("Không có kết quả để lưu");
            return;
        }

        String cheDo = layMaCheDo();

        if (cheDo == null) {
            thongBao("Chế độ luyện tập không hợp lệ");
            return;
        }

        String token = layToken();

        if (token == null || token.isEmpty()) {
            thongBao("Vui lòng đăng nhập lại");
            return;
        }

        if (maPhienChoi == null ||
                maPhienChoi.trim().isEmpty()) {

            thongBao("Mã phiên luyện tập không hợp lệ");
            return;
        }

        dangLuu = true;
        capNhatNutLuu();

        LuuTienTrinhRequest request =
                new LuuTienTrinhRequest(
                        maPhienChoi,
                        cheDo,
                        danhSachKetQua
                );

        callLuu = apiService.luuTienTrinhLuyenTap(
                "Bearer " + token,
                request
        );

        callLuu.enqueue(
                new Callback<ApiResponse<LuuTienTrinhResponse>>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<ApiResponse<LuuTienTrinhResponse>> call,
                            @NonNull Response<ApiResponse<LuuTienTrinhResponse>> response) {

                        if (!isAdded() || getView() == null) {
                            return;
                        }

                        dangLuu = false;

                        if (!response.isSuccessful()
                                || response.body() == null
                                || response.body().getData() == null) {

                            xuLyLuuThatBai();
                            return;
                        }

                        daLuuThanhCong = true;

                        LuuTienTrinhResponse duLieu =
                                response.body().getData();

                        txtStreak.setText(
                                "Streak hiện tại: "
                                        + duLieu.getCurrent_streak()
                                        + " ngày"
                        );

                        capNhatNutLuu();

                        thongBao(
                                "Đã lưu tiến trình luyện tập"
                        );

                        requireActivity()
                                .getSupportFragmentManager()
                                .popBackStack(
                                        "FLASHCARD",
                                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                                );
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<ApiResponse<LuuTienTrinhResponse>> call,
                            @NonNull Throwable t) {

                        if (!isAdded() || getView() == null) {
                            return;
                        }

                        dangLuu = false;

                        xuLyLuuThatBai();
                    }
                }
        );
    }

    private void xuLyLuuThatBai() {

        daLuuThanhCong = false;

        capNhatNutLuu();

        btnLuuTienTrinh.setText(
                "THỬ LƯU LẠI"
        );

        thongBao(
                "Không thể lưu tiến trình"
        );
    }

    private void capNhatNutLuu() {

        if (btnLuuTienTrinh == null) {
            return;
        }

        if (dangLuu) {

            btnLuuTienTrinh.setEnabled(false);

            btnLuuTienTrinh.setText(
                    "ĐANG LƯU..."
            );

        } else if (daLuuThanhCong) {

            btnLuuTienTrinh.setEnabled(false);

            btnLuuTienTrinh.setText(
                    "ĐÃ LƯU THÀNH CÔNG"
            );

        } else {

            btnLuuTienTrinh.setEnabled(true);

            btnLuuTienTrinh.setText(
                    "LƯU TIẾN TRÌNH"
            );
        }
    }

    private void thongBao(String noiDung) {

        if (!isAdded()) {
            return;
        }

        Toast.makeText(
                requireContext(),
                noiDung,
                Toast.LENGTH_SHORT
        ).show();
    }

    @Override
    public void onSaveInstanceState(
            @NonNull Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putBoolean(
                KEY_DA_LUU,
                daLuuThanhCong
        );
    }

    @Override
    public void onDestroyView() {

        if (callLuu != null && !callLuu.isCanceled()) {
            callLuu.cancel();
        }

        dangLuu = false;

        super.onDestroyView();
    }
}
