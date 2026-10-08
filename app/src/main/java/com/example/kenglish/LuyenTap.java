
package com.example.kenglish;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.kenglish.adapter.GameLuyenTapAdapter;
import com.example.kenglish.api.ApiService;
import com.example.kenglish.api.RetrofitClient;
import com.example.kenglish.model.ApiResponse;
import com.example.kenglish.model.BoTuModel;
import com.example.kenglish.model.DanhSachTuResponse;
import com.example.kenglish.model.GameLuyenTap;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LuyenTap extends Fragment {

    private LinearLayout btnChonBoTu;
    private LinearLayout btnChonTrangThai;
    private LinearLayout btnChonThuTu;
    private LinearLayout btnChonSoLuong;

    private TextView txtBoTu;
    private TextView txtTrangThai;
    private TextView txtThuTu;
    private TextView txtSoLuong;

    private ImageView imgMuiTenBoTu;
    private ImageView imgMuiTenTrangThai;
    private ImageView imgMuiTenThuTu;
    private ImageView imgMuiTenSoLuong;

    private GridView gridGame;

    private final List<GameLuyenTap> danhSachGame =
            new ArrayList<>();

    private final List<BoTuModel> danhSachBoTu =
            new ArrayList<>();

    private GameLuyenTapAdapter gameAdapter;
    private ApiService apiService;

    // ==================== GIÁ TRỊ BỘ LỌC ====================

    // -1: Tất cả bộ từ
    private int boTuIdDaChon = -1;
    private String tenBoTuDaChon = "Tất cả bộ từ";

    // all / learned / unlearned
    private String trangThaiDaChon = "all";

    // random / default
    private String thuTuDaChon = "random";

    // Giới hạn tối đa số từ
    private int soLuongDaChon = 20;

    private boolean dangTaiTuLuyenTap = false;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.luyen_tap,
                container,
                false
        );

        anhXa(view);
        khoiTaoDuLieuGame();
        khoiTaoAdapter();
        xuLySuKien();

        return view;
    }

    // ==================== ÁNH XẠ ====================

    private void anhXa(View view) {

        btnChonBoTu =
                view.findViewById(R.id.btn_chon_bo_tu);

        btnChonTrangThai =
                view.findViewById(R.id.btn_chon_trang_thai);

        btnChonThuTu =
                view.findViewById(R.id.btn_chon_thu_tu);

        btnChonSoLuong =
                view.findViewById(R.id.btn_chon_so_luong);

        txtBoTu =
                view.findViewById(R.id.txt_bo_tu);

        txtTrangThai =
                view.findViewById(R.id.txt_trang_thai);

        txtThuTu =
                view.findViewById(R.id.txt_thu_tu);

        txtSoLuong =
                view.findViewById(R.id.txt_so_luong);

        imgMuiTenBoTu =
                view.findViewById(R.id.img_mui_ten_bo_tu);

        imgMuiTenTrangThai =
                view.findViewById(R.id.img_mui_ten_trang_thai);

        imgMuiTenThuTu =
                view.findViewById(R.id.img_mui_ten_thu_tu);

        imgMuiTenSoLuong =
                view.findViewById(R.id.img_mui_ten_so_luong);

        gridGame =
                view.findViewById(R.id.grid_game);

        apiService =
                RetrofitClient.getClient()
                        .create(ApiService.class);

        hienThiBoLoc();
    }

    private void hienThiBoLoc() {

        txtBoTu.setText(tenBoTuDaChon);

        txtTrangThai.setText(
                layTenTrangThai(trangThaiDaChon)
        );

        txtThuTu.setText(
                layTenThuTu(thuTuDaChon)
        );

        txtSoLuong.setText(
                soLuongDaChon + " từ"
        );
    }

    // ==================== DANH SÁCH GAME ====================

    private void khoiTaoDuLieuGame() {

        danhSachGame.clear();

        danhSachGame.add(
                new GameLuyenTap(
                        "Flashcard",
                        "Lật thẻ và ghi nhớ",
                        R.drawable.ic_flashcard,
                        5,
                        R.drawable.nen_luyentap_game_flashcard
                )
        );

        danhSachGame.add(
                new GameLuyenTap(
                        "Trắc nghiệm",
                        "Chọn đáp án đúng",
                        R.drawable.ic_tracnghiem,
                        10,
                        R.drawable.nen_luyentap_game_tracnghiem
                )
        );

        danhSachGame.add(
                new GameLuyenTap(
                        "Nối từ với nghĩa",
                        "Ghép các cặp thật nhanh",
                        R.drawable.ic_noitu,
                        10,
                        R.drawable.nen_luyentap_game_noitu
                )
        );

        danhSachGame.add(
                new GameLuyenTap(
                        "Gõ từ vựng",
                        "Nhớ nghĩa và gõ từ",
                        R.drawable.ic_gotu,
                        10,
                        R.drawable.nen_luyentap_game_gotu
                )
        );

        danhSachGame.add(
                new GameLuyenTap(
                        "Nghe viết",
                        "Nghe và nhập từ đúng",
                        R.drawable.ic_ngheviet,
                        15,
                        R.drawable.nen_luyentap_game_ngheviet
                )
        );

        danhSachGame.add(
                new GameLuyenTap(
                        "Đặc biệt",
                        "Thử các chế độ mới",
                        R.drawable.ic_game_dacbiet,
                        20,
                        R.drawable.nen_luyentap_game_dacbiet
                )
        );
    }

    private void khoiTaoAdapter() {

        gameAdapter = new GameLuyenTapAdapter(
                requireContext(),
                danhSachGame
        );

        gridGame.setAdapter(gameAdapter);
    }

    // ==================== SỰ KIỆN ====================

    private void xuLySuKien() {

        btnChonBoTu.setOnClickListener(v -> {
            layDanhSachBoTu();
        });

        btnChonTrangThai.setOnClickListener(v -> {

            moPopupLuaChon(
                    "Chọn trạng thái từ",
                    Arrays.asList(
                            "Tất cả",
                            "Chưa thuộc",
                            "Đã thuộc"
                    ),
                    layTenTrangThai(trangThaiDaChon),
                    txtTrangThai,
                    noiDung -> {
                        trangThaiDaChon =
                                layMaTrangThai(noiDung);
                    }
            );
        });

        btnChonThuTu.setOnClickListener(v -> {

            moPopupLuaChon(
                    "Chọn thứ tự",
                    Arrays.asList(
                            "Ngẫu nhiên",
                            "Theo thứ tự"
                    ),
                    layTenThuTu(thuTuDaChon),
                    txtThuTu,
                    noiDung -> {
                        thuTuDaChon =
                                layMaThuTu(noiDung);
                    }
            );
        });

        btnChonSoLuong.setOnClickListener(v -> {

            moPopupLuaChon(
                    "Chọn số lượng từ",
                    Arrays.asList(
                            "10 từ",
                            "20 từ",
                            "50 từ",
                            "100 từ",
                            "200 từ"
                    ),
                    soLuongDaChon + " từ",
                    txtSoLuong,
                    noiDung -> {
                        String giaTri =
                                noiDung.replace(" từ", "");

                        soLuongDaChon =
                                Integer.parseInt(giaTri);
                    }
            );
        });

        gridGame.setOnItemClickListener(
                (parent, view, position, id) -> {

                    if (position < 0 ||
                            position >= danhSachGame.size()) {
                        return;
                    }

                    layTuLuyenTap();
                }
        );
    }

    // ==================== CHUYỂN ĐỔI GIÁ TRỊ ====================

    private String layMaTrangThai(String ten) {

        switch (ten) {
            case "Đã thuộc":
                return "learned";

            case "Chưa thuộc":
                return "unlearned";

            default:
                return "all";
        }
    }

    private String layTenTrangThai(String ma) {

        switch (ma) {
            case "learned":
                return "Đã thuộc";

            case "unlearned":
                return "Chưa thuộc";

            default:
                return "Tất cả";
        }
    }

    private String layMaThuTu(String ten) {

        if ("Theo thứ tự".equals(ten)) {
            return "default";
        }

        return "random";
    }

    private String layTenThuTu(String ma) {

        if ("default".equals(ma)) {
            return "Theo thứ tự";
        }

        return "Ngẫu nhiên";
    }

    // ==================== TOKEN ====================

    private String layToken() {

        SharedPreferences sharedPreferences =
                requireContext().getSharedPreferences(
                        "Kenglish",
                        Context.MODE_PRIVATE
                );

        return sharedPreferences.getString(
                "token",
                null
        );
    }

    // ==================== LẤY BỘ TỪ ====================

    private void layDanhSachBoTu() {

        String token = layToken();

        if (token == null || token.isEmpty()) {

            thongBao("Vui lòng đăng nhập lại");
            return;
        }

        btnChonBoTu.setEnabled(false);

        apiService.layTatCaBoTuLuyenTap(
                "Bearer " + token,
                true
        ).enqueue(
                new Callback<ApiResponse<List<BoTuModel>>>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<ApiResponse<List<BoTuModel>>> call,
                            @NonNull Response<ApiResponse<List<BoTuModel>>> response) {

                        if (!isAdded() || getView() == null) {
                            return;
                        }

                        btnChonBoTu.setEnabled(true);

                        if (!response.isSuccessful()
                                || response.body() == null
                                || response.body().getData() == null) {

                            thongBao(
                                    "Không tải được danh sách bộ từ"
                            );
                            return;
                        }

                        danhSachBoTu.clear();

                        danhSachBoTu.addAll(
                                response.body().getData()
                        );

                        // Nếu bộ từ đang chọn đã bị xóa,
                        // tự quay về tất cả bộ từ.
                        if (boTuIdDaChon != -1) {

                            boolean conTonTai = false;

                            for (BoTuModel boTu : danhSachBoTu) {

                                if (boTu.getId() == boTuIdDaChon) {

                                    conTonTai = true;

                                    tenBoTuDaChon =
                                            boTu.getTenBoTu();

                                    break;
                                }
                            }

                            if (!conTonTai) {

                                boTuIdDaChon = -1;
                                tenBoTuDaChon =
                                        "Tất cả bộ từ";
                            }

                            txtBoTu.setText(tenBoTuDaChon);
                        }

                        moPopupChonBoTu();
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<ApiResponse<List<BoTuModel>>> call,
                            @NonNull Throwable t) {

                        if (!isAdded() || getView() == null) {
                            return;
                        }

                        btnChonBoTu.setEnabled(true);

                        thongBao(
                                "Không thể kết nối đến server"
                        );
                    }
                }
        );
    }

    // ==================== POPUP BỘ TỪ ====================

    private void moPopupChonBoTu() {

        BottomSheetDialog dialog =
                new BottomSheetDialog(requireContext());

        View popupView =
                LayoutInflater.from(requireContext())
                        .inflate(
                                R.layout.luyentap_popup_luachon,
                                null
                        );

        dialog.setContentView(popupView);

        TextView txtTieuDe =
                popupView.findViewById(
                        R.id.txt_tieu_de_popup
                );

        LinearLayout khungDanhSach =
                popupView.findViewById(
                        R.id.khung_danh_sach_lua_chon
                );

        txtTieuDe.setText("Chọn bộ từ");

        themItemBoTu(
                khungDanhSach,
                dialog,
                "Tất cả bộ từ",
                -1
        );

        for (BoTuModel boTu : danhSachBoTu) {

            themItemBoTu(
                    khungDanhSach,
                    dialog,
                    boTu.getTenBoTu(),
                    boTu.getId()
            );
        }

        dialog.show();
    }

    private void themItemBoTu(
            LinearLayout khungDanhSach,
            BottomSheetDialog dialog,
            String tenBoTu,
            int boTuId) {

        View itemView =
                LayoutInflater.from(requireContext())
                        .inflate(
                                R.layout.luyentap_item_luachon,
                                khungDanhSach,
                                false
                        );

        LinearLayout khungLuaChon =
                itemView.findViewById(
                        R.id.khung_lua_chon
                );

        ImageView imgDauChon =
                itemView.findViewById(
                        R.id.txt_dau_chon
                );

        TextView txtNoiDung =
                itemView.findViewById(
                        R.id.txt_noi_dung_lua_chon
                );

        txtNoiDung.setText(tenBoTu);

        boolean dangChon =
                boTuId == boTuIdDaChon;

        capNhatGiaoDienLuaChon(
                khungLuaChon,
                imgDauChon,
                txtNoiDung,
                dangChon
        );

        itemView.setOnClickListener(v -> {

            boTuIdDaChon = boTuId;
            tenBoTuDaChon = tenBoTu;

            txtBoTu.setText(tenBoTuDaChon);

            dialog.dismiss();
        });

        khungDanhSach.addView(itemView);
    }

    // ==================== POPUP DÙNG CHUNG ====================

    private interface XuLyLuaChon {
        void khiChon(String noiDung);
    }

    private void moPopupLuaChon(
            String tieuDe,
            List<String> danhSach,
            String giaTriDangChon,
            TextView textViewCanCapNhat,
            XuLyLuaChon xuLyLuaChon) {

        BottomSheetDialog dialog =
                new BottomSheetDialog(requireContext());

        View popupView =
                LayoutInflater.from(requireContext())
                        .inflate(
                                R.layout.luyentap_popup_luachon,
                                null
                        );

        dialog.setContentView(popupView);

        TextView txtTieuDe =
                popupView.findViewById(
                        R.id.txt_tieu_de_popup
                );

        LinearLayout khungDanhSach =
                popupView.findViewById(
                        R.id.khung_danh_sach_lua_chon
                );

        txtTieuDe.setText(tieuDe);

        for (String noiDung : danhSach) {

            View itemView =
                    LayoutInflater.from(requireContext())
                            .inflate(
                                    R.layout.luyentap_item_luachon,
                                    khungDanhSach,
                                    false
                            );

            LinearLayout khungLuaChon =
                    itemView.findViewById(
                            R.id.khung_lua_chon
                    );

            ImageView imgDauChon =
                    itemView.findViewById(
                            R.id.txt_dau_chon
                    );

            TextView txtNoiDung =
                    itemView.findViewById(
                            R.id.txt_noi_dung_lua_chon
                    );

            txtNoiDung.setText(noiDung);

            boolean dangChon =
                    noiDung.equals(giaTriDangChon);

            capNhatGiaoDienLuaChon(
                    khungLuaChon,
                    imgDauChon,
                    txtNoiDung,
                    dangChon
            );

            itemView.setOnClickListener(v -> {

                xuLyLuaChon.khiChon(noiDung);

                textViewCanCapNhat.setText(noiDung);

                dialog.dismiss();
            });

            khungDanhSach.addView(itemView);
        }

        dialog.show();
    }

    // ==================== GIAO DIỆN LỰA CHỌN ====================

    private void capNhatGiaoDienLuaChon(
            LinearLayout khungLuaChon,
            ImageView imgDauChon,
            TextView txtNoiDung,
            boolean dangChon) {

        if (dangChon) {

            khungLuaChon.setBackgroundResource(
                    R.drawable.nen_luyentap_luachon_chon
            );

            imgDauChon.setImageResource(
                    R.drawable.ic_check
            );

            imgDauChon.setColorFilter(
                    0xFF37659C
            );

            txtNoiDung.setTextColor(
                    0xFF37659C
            );

        } else {

            khungLuaChon.setBackgroundResource(
                    R.drawable.nen_luyentap_luachon
            );

            imgDauChon.setImageResource(
                    R.drawable.ic_bullet
            );

            imgDauChon.setColorFilter(
                    0xFFA9A9A9
            );

            txtNoiDung.setTextColor(
                    0xFF111111
            );
        }
    }

    // ==================== LẤY TỪ LUYỆN TẬP ====================

    private void layTuLuyenTap() {

        if (dangTaiTuLuyenTap) {
            return;
        }

        String token = layToken();

        if (token == null || token.isEmpty()) {

            thongBao("Vui lòng đăng nhập lại");
            return;
        }

        dangTaiTuLuyenTap = true;

        apiService.layTuLuyenTap(
                "Bearer " + token,
                boTuIdDaChon,
                trangThaiDaChon,
                thuTuDaChon,
                soLuongDaChon
        ).enqueue(
                new Callback<ApiResponse<DanhSachTuResponse>>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<ApiResponse<DanhSachTuResponse>> call,
                            @NonNull Response<ApiResponse<DanhSachTuResponse>> response) {

                        dangTaiTuLuyenTap = false;

                        if (!isAdded() || getView() == null) {
                            return;
                        }

                        if (!response.isSuccessful()
                                || response.body() == null
                                || response.body().getData() == null) {

                            thongBao(
                                    "Không tải được từ vựng luyện tập"
                            );
                            return;
                        }

                        DanhSachTuResponse duLieu =
                                response.body().getData();

                        if (duLieu.getTuVung() == null
                                || duLieu.getTuVung().isEmpty()) {

                            thongBao(
                                    "Không có từ vựng phù hợp với bộ lọc"
                            );
                            return;
                        }

                        int soTuThucTe =
                                duLieu.getTuVung().size();

                        // Chưa có màn hình Flashcard.
                        // Kiểm tra dữ liệu API trước.
                        thongBao(
                                "Đã chuẩn bị "
                                        + soTuThucTe
                                        + " từ luyện tập"
                        );
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<ApiResponse<DanhSachTuResponse>> call,
                            @NonNull Throwable t) {

                        dangTaiTuLuyenTap = false;

                        if (!isAdded() || getView() == null) {
                            return;
                        }

                        thongBao(
                                "Không thể kết nối đến server"
                        );
                    }
                }
        );
    }

    // ==================== THÔNG BÁO ====================

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
}
