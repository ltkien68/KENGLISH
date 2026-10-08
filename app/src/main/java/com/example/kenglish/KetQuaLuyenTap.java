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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KetQuaLuyenTap extends Fragment {

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
    private boolean dangLuu = false;
    private boolean daLuuThanhCong = false;

    public static KetQuaLuyenTap newInstance(
            String tenGame,
            List<KetQuaTuVung> ketQua) {

        KetQuaLuyenTap fragment = new KetQuaLuyenTap();

        Bundle bundle = new Bundle();

        bundle.putString("ten_game", tenGame);

        bundle.putSerializable(
                "ket_qua",
                new ArrayList<>(ketQua)
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
        hienThiKetQua();
        xuLySuKien();

        return view;
    }

    private void anhXa(View view) {

        txtTenGame = view.findViewById(R.id.txt_ten_game);
        txtPhanTram = view.findViewById(R.id.txt_phan_tram);
        txtDanhGia = view.findViewById(R.id.txt_danh_gia);
        txtTongKet = view.findViewById(R.id.txt_tong_ket);

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
                "ten_game",
                "Luyện tập"
        );

        Object duLieu = bundle.getSerializable("ket_qua");

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

        txtPhanTram.setText(phanTram + "%");

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
            txtDanhGia.setText("Chưa có kết quả");
        } else if (phanTram == 100) {
            txtDanhGia.setText("Xuất sắc!");
        } else if (phanTram >= 70) {
            txtDanhGia.setText("Làm tốt lắm!");
        } else {
            txtDanhGia.setText("Tiếp tục cố gắng!");
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

        TextView txtTrangThai = item.findViewById(
                R.id.txt_trang_thai_item
        );

        TextView txtTu = item.findViewById(
                R.id.txt_tu_item
        );

        TextView txtNghia = item.findViewById(
                R.id.txt_nghia_item
        );

        txtTrangThai.setText(daThuoc ? "✓" : "✕");

        txtTrangThai.setTextColor(
                Color.parseColor(
                        daThuoc ? "#159947" : "#D83A42"
                )
        );

        txtTu.setText(ketQua.getTu_goc());

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

        return preferences.getString("token", null);
    }

    private void luuTienTrinh() {

        if (danhSachKetQua.isEmpty()) {
            thongBao("Không có kết quả để lưu");
            return;
        }

        String token = layToken();

        if (token == null || token.isEmpty()) {
            thongBao("Vui lòng đăng nhập lại");
            return;
        }

        dangLuu = true;

        btnLuuTienTrinh.setEnabled(false);
        btnLuuTienTrinh.setText("ĐANG LƯU...");

        LuuTienTrinhRequest request =
                new LuuTienTrinhRequest(danhSachKetQua);

        apiService.luuTienTrinhLuyenTap(
                "Bearer " + token,
                request
        ).enqueue(
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

                        btnLuuTienTrinh.setText(
                                "ĐÃ LƯU THÀNH CÔNG"
                        );

                        thongBao("Đã lưu tiến trình luyện tập");

                        // Quay về tab Luyện tập,
                        // đóng cả màn hình kết quả và Flashcard.
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

        btnLuuTienTrinh.setEnabled(true);

        btnLuuTienTrinh.setText("THỬ LƯU LẠI");

        thongBao("Không thể lưu tiến trình");
    }

    private void thongBao(String noiDung) {

        Toast.makeText(
                requireContext(),
                noiDung,
                Toast.LENGTH_SHORT
        ).show();
    }
}
