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
import com.example.kenglish.model.HoatDongNamResponse;
import com.example.kenglish.model.LichSuLuyenTapResponse;
import com.example.kenglish.model.ThongKeLuyenTapResponse;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Date;

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


    // ==================== LỊCH SỬ LUYỆN TẬP ====================

    private ImageView btnChuyenCheDoLichSu;

    private LinearLayout layoutDanhSachLichSu;
    private LinearLayout layoutThongKeLichSu;
    private LinearLayout khungDanhSachLichSu;

    private TextView txtTrangThaiLichSu;
    private TextView txtSoLanLuyenTap;
    private TextView txtDoChinhXacTb;
    private TextView txtSoLuotChoiLichSu;
    private TextView txtStreakLichSu;

    private boolean dangXemThongKe = false;

    private Call<ApiResponse<LichSuLuyenTapResponse>> callLichSu;
    private Call<ApiResponse<ThongKeLuyenTapResponse>> callThongKe;
    private Call<ApiResponse<HoatDongNamResponse>> callStreak;


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

    private int viTriGameDaChon = -1;

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


        btnChuyenCheDoLichSu =
                view.findViewById(R.id.btn_chuyen_che_do_lich_su);

        layoutDanhSachLichSu =
                view.findViewById(R.id.layout_danh_sach_lich_su);

        layoutThongKeLichSu =
                view.findViewById(R.id.layout_thong_ke_lich_su);

        khungDanhSachLichSu =
                view.findViewById(R.id.khung_danh_sach_lich_su);

        txtTrangThaiLichSu =
                view.findViewById(R.id.txt_trang_thai_lich_su);

        txtSoLanLuyenTap =
                view.findViewById(R.id.txt_so_lan_luyen_tap);

        txtDoChinhXacTb =
                view.findViewById(R.id.txt_do_chinh_xac_tb);

        txtSoLuotChoiLichSu =
                view.findViewById(R.id.txt_so_luot_choi_lich_su);

        txtStreakLichSu =
                view.findViewById(R.id.txt_streak_lich_su);


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

                    // Flashcard đang ở vị trí đầu tiên.
                    if (position != 0) {
                        thongBao("Chế độ này đang được phát triển");
                        return;
                    }

                    viTriGameDaChon = position;

                    layTuLuyenTap();
                }
        );


        btnChuyenCheDoLichSu.setOnClickListener(v -> {
            dangXemThongKe = !dangXemThongKe;
            hienThiCheDoLichSu();

            if (dangXemThongKe) {
                layThongKeLichSu();
                layStreakLichSu();
            }
        });

    }


    private void hienThiCheDoLichSu() {
        layoutDanhSachLichSu.setVisibility(
                dangXemThongKe ? View.GONE : View.VISIBLE
        );

        layoutThongKeLichSu.setVisibility(
                dangXemThongKe ? View.VISIBLE : View.GONE
        );

        btnChuyenCheDoLichSu.setImageResource(
                dangXemThongKe
                        ? R.drawable.ic_danhsach_lichsu
                        : R.drawable.ic_thongke
        );

        btnChuyenCheDoLichSu.setContentDescription(
                dangXemThongKe
                        ? "Chuyển sang danh sách"
                        : "Chuyển sang thống kê"
        );
    }


    private void layLichSuLuyenTap() {
        String token = layToken();

        if (token == null || token.isEmpty()) {
            hienThiTrangThaiLichSu("Vui lòng đăng nhập lại");
            return;
        }

        if (callLichSu != null) {
            callLichSu.cancel();
        }

        khungDanhSachLichSu.removeAllViews();
        hienThiTrangThaiLichSu("Đang tải lịch sử...");

        callLichSu = apiService.layLichSuLuyenTap(
                "Bearer " + token,
                1,
                10
        );

        callLichSu.enqueue(
                new Callback<ApiResponse<LichSuLuyenTapResponse>>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<ApiResponse<LichSuLuyenTapResponse>> call,
                            @NonNull Response<ApiResponse<LichSuLuyenTapResponse>> response) {

                        if (!isAdded() || getView() == null || call != callLichSu) {
                            return;
                        }

                        if (!response.isSuccessful()
                                || response.body() == null
                                || response.body().getData() == null) {
                            hienThiTrangThaiLichSu("Không tải được lịch sử");
                            return;
                        }

                        LichSuLuyenTapResponse duLieu =
                                response.body().getData();

                        if (duLieu.getDanhSach() == null
                                || duLieu.getDanhSach().isEmpty()) {
                            hienThiTrangThaiLichSu("Bạn chưa có phiên luyện tập nào");
                            return;
                        }

                        txtTrangThaiLichSu.setVisibility(View.GONE);
                        khungDanhSachLichSu.removeAllViews();

                        for (LichSuLuyenTapResponse.PhienLuyenTap phien
                                : duLieu.getDanhSach()) {
                            themItemLichSu(phien);
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<ApiResponse<LichSuLuyenTapResponse>> call,
                            @NonNull Throwable t) {

                        if (call.isCanceled()
                                || !isAdded()
                                || getView() == null
                                || call != callLichSu) {
                            return;
                        }

                        hienThiTrangThaiLichSu("Không thể kết nối đến server");
                    }
                }
        );
    }

    private void hienThiTrangThaiLichSu(String noiDung) {
        txtTrangThaiLichSu.setText(noiDung);
        txtTrangThaiLichSu.setVisibility(View.VISIBLE);
    }

    private void themItemLichSu(
            LichSuLuyenTapResponse.PhienLuyenTap phien) {

        View itemView = LayoutInflater.from(requireContext())
                .inflate(
                        R.layout.luyentap_item_lichsu,
                        khungDanhSachLichSu,
                        false
                );

        ImageView imgGame =
                itemView.findViewById(R.id.img_game_lich_su);

        TextView txtTenGame =
                itemView.findViewById(R.id.txt_ten_game_lich_su);

        TextView txtKetQua =
                itemView.findViewById(R.id.txt_ket_qua_lich_su);

        TextView txtNgay =
                itemView.findViewById(R.id.txt_ngay_lich_su);

        String cheDo = phien.getCheDo();

        txtTenGame.setText(layTenGameLichSu(cheDo));

        if ("flashcard".equals(cheDo)) {
            imgGame.setImageResource(R.drawable.ic_flashcard);
        } else if ("trac_nghiem".equals(cheDo)) {
            imgGame.setImageResource(R.drawable.ic_tracnghiem);
        } else if ("noi_tu".equals(cheDo)) {
            imgGame.setImageResource(R.drawable.ic_noitu);
        } else if ("go_tu".equals(cheDo)) {
            imgGame.setImageResource(R.drawable.ic_gotu);
        } else if ("nghe_viet".equals(cheDo)) {
            imgGame.setImageResource(R.drawable.ic_ngheviet);
        } else {
            imgGame.setImageResource(R.drawable.ic_game_dacbiet);
        }

        String nhanKetQua = "flashcard".equals(cheDo)
                ? " đã thuộc"
                : " câu đúng";

        String ketQua = phien.getSoDung()
                + "/" + phien.getTongSoCau()
                + nhanKetQua
                + " · " + phien.getPhanTram() + "%";

        txtKetQua.setText(ketQua);
        txtNgay.setText(dinhDangNgayLichSu(phien.getNgayLuyenTap()));

        khungDanhSachLichSu.addView(itemView);
    }

    private String layTenGameLichSu(String cheDo) {
        if (cheDo == null) {
            return "Luyện tập";
        }

        switch (cheDo) {
            case "flashcard":
                return "Flashcard";
            case "trac_nghiem":
                return "Trắc nghiệm";
            case "noi_tu":
                return "Nối từ với nghĩa";
            case "go_tu":
                return "Gõ từ vựng";
            case "nghe_viet":
                return "Nghe viết";
            case "dac_biet":
                return "Đặc biệt";
            default:
                return "Luyện tập";
        }
    }

    private String dinhDangNgayLichSu(String ngay) {
        if (ngay == null || ngay.trim().isEmpty()) {
            return "";
        }

        try {
            SimpleDateFormat dauVao = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    Locale.getDefault()
            );

            SimpleDateFormat dauRa = new SimpleDateFormat(
                    "dd/MM/yyyy · HH:mm",
                    Locale.getDefault()
            );

            Date date = dauVao.parse(ngay);

            return date != null
                    ? dauRa.format(date)
                    : ngay;

        } catch (ParseException e) {
            return ngay;
        }
    }


    private void layThongKeLichSu() {
        String token = layToken();

        if (token == null || token.isEmpty()) {
            txtSoLanLuyenTap.setText("—");
            txtDoChinhXacTb.setText("—");
            txtSoLuotChoiLichSu.setText("—");
            hienThiLoiThongKe("Vui lòng đăng nhập lại");
            return;
        }

        if (callThongKe != null) {
            callThongKe.cancel();
        }

        txtSoLanLuyenTap.setText("…");
        txtDoChinhXacTb.setText("…");
        txtSoLuotChoiLichSu.setText("…");

        callThongKe = apiService.layThongKeLuyenTap(
                "Bearer " + token
        );

        callThongKe.enqueue(
                new Callback<ApiResponse<ThongKeLuyenTapResponse>>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<ApiResponse<ThongKeLuyenTapResponse>> call,
                            @NonNull Response<ApiResponse<ThongKeLuyenTapResponse>> response) {

                        if (!isAdded() || getView() == null || call != callThongKe) {
                            return;
                        }

                        if (!response.isSuccessful()
                                || response.body() == null
                                || response.body().getData() == null) {
                            hienThiLoiThongKe("Không tải được thống kê");
                            return;
                        }

                        ThongKeLuyenTapResponse duLieu =
                                response.body().getData();

                        txtSoLanLuyenTap.setText(
                                String.valueOf(duLieu.getSoLanLuyenTap())
                        );

                        txtDoChinhXacTb.setText(
                                duLieu.getDoChinhXacTb() + "%"
                        );

                        txtSoLuotChoiLichSu.setText(
                                String.valueOf(duLieu.getSoLuotChoi())
                        );

                        if (duLieu.getTheoCheDo() == null
                                || duLieu.getTheoCheDo().isEmpty()) {
                            themDongThongKe("Chưa có dữ liệu", "");
                            return;
                        }

                        for (ThongKeLuyenTapResponse.ThongKeTheoCheDo item
                                : duLieu.getTheoCheDo()) {
                            themDongThongKe(
                                    layTenGameLichSu(item.getCheDo()),
                                    item.getSoLan() + " lần"
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<ApiResponse<ThongKeLuyenTapResponse>> call,
                            @NonNull Throwable t) {

                        if (call.isCanceled()
                                || !isAdded()
                                || getView() == null
                                || call != callThongKe) {
                            return;
                        }

                        hienThiLoiThongKe("Không thể tải thống kê");
                    }
                }
        );
    }

    private void hienThiLoiThongKe(String thongBao) {
        txtSoLanLuyenTap.setText("—");
        txtDoChinhXacTb.setText("—");
        txtSoLuotChoiLichSu.setText("—");
        themDongThongKe(thongBao, "");
    }

    private void themDongThongKe(String tenGame, String soLan) {
        LinearLayout dong = new LinearLayout(requireContext());
        dong.setOrientation(LinearLayout.HORIZONTAL);
        dong.setGravity(android.view.Gravity.CENTER_VERTICAL);

        int padding = (int) (10 * getResources().getDisplayMetrics().density);
        dong.setPadding(0, padding, 0, padding);

        TextView txtTen = new TextView(requireContext());
        txtTen.setText(tenGame);
        txtTen.setTextSize(13);
        txtTen.setTextColor(0xFF7C8492);
        txtTen.setFontFeatureSettings("kern");

        LinearLayout.LayoutParams tenParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                );

        dong.addView(txtTen, tenParams);

        TextView txtSoLan = new TextView(requireContext());
        txtSoLan.setText(soLan);
        txtSoLan.setTextSize(13);
        txtSoLan.setTextColor(0xFF111111);

        dong.addView(txtSoLan);

    }


    private void layStreakLichSu() {
        String token = layToken();

        if (token == null || token.isEmpty()) {
            txtStreakLichSu.setText("—");
            return;
        }

        if (callStreak != null) {
            callStreak.cancel();
        }

        txtStreakLichSu.setText("…");

        callStreak = apiService.layHoatDongNamHienTai(
                "Bearer " + token
        );

        callStreak.enqueue(
                new Callback<ApiResponse<HoatDongNamResponse>>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<ApiResponse<HoatDongNamResponse>> call,
                            @NonNull Response<ApiResponse<HoatDongNamResponse>> response) {

                        if (!isAdded() || getView() == null || call != callStreak) {
                            return;
                        }

                        if (!response.isSuccessful()
                                || response.body() == null
                                || response.body().getData() == null) {
                            txtStreakLichSu.setText("—");
                            return;
                        }

                        int streak = response.body()
                                .getData()
                                .getCurrentStreak();

                        txtStreakLichSu.setText(streak + " ngày");
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<ApiResponse<HoatDongNamResponse>> call,
                            @NonNull Throwable t) {

                        if (call.isCanceled()
                                || !isAdded()
                                || getView() == null
                                || call != callStreak) {
                            return;
                        }

                        txtStreakLichSu.setText("—");
                    }
                }
        );
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden && isAdded() && getView() != null && apiService != null) {
            layLichSuLuyenTap();

            if (dangXemThongKe) {
                layThongKeLichSu();
                layStreakLichSu();
            }
        }
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

    // ==================== ĐIỀU KIỆN SỐ TỪ ====================

    private int laySoTuToiThieu(int viTriGame) {
        return viTriGame == 0 ? 1 : 4;
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


                        int soTuToiThieu = laySoTuToiThieu(viTriGameDaChon);

                        if (duLieu.getTuVung().size() < soTuToiThieu) {
                            thongBao("Trò chơi này cần tối thiểu "
                                    + soTuToiThieu
                                    + " từ vựng phù hợp với bộ lọc");
                            return;
                        }

                        if (viTriGameDaChon == 0) {

                            Flashcard flashcard = Flashcard.newInstance(
                                    duLieu.getTuVung()
                            );

                            requireActivity()
                                    .getSupportFragmentManager()
                                    .beginTransaction()
                                    .hide(LuyenTap.this)
                                    .add(
                                            R.id.khung_noi_dung,
                                            flashcard,
                                            "FLASHCARD"
                                    )
                                    .addToBackStack("FLASHCARD")
                                    .commit();
                        }


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


    public void chonBoTuTuManHinhKhac(
            int boTuId,
            String tenBoTu) {

        if (boTuId <= 0) {
            return;
        }

        boTuIdDaChon = boTuId;

        tenBoTuDaChon =
                tenBoTu != null && !tenBoTu.trim().isEmpty()
                        ? tenBoTu
                        : "Bộ từ";

        // Nếu giao diện đã được tạo thì cập nhật ngay.
        if (txtBoTu != null) {
            txtBoTu.setText(tenBoTuDaChon);
        }
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
