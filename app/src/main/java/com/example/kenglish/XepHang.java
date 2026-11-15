package com.example.kenglish;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.kenglish.adapter.CongDongAdapter;
import com.example.kenglish.adapter.XepHangAdapter;
import com.example.kenglish.api.ApiService;
import com.example.kenglish.api.RetrofitClient;
import com.example.kenglish.model.ApiResponse;
import com.example.kenglish.model.BaiVietCongDong;
import com.example.kenglish.model.BangXepHangResponse;
import com.example.kenglish.model.DanhSachTinNhanCongDongResponse;
import com.example.kenglish.model.GuiTinNhanCongDongRequest;
import com.example.kenglish.model.NguoiDungXepHang;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import io.socket.client.IO;
import io.socket.client.Socket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class XepHang extends Fragment {

    private static final String TAB_LUOT_CHOI = "luot_choi";
    private static final String TAB_STREAK = "streak";
    private static final String TAB_CONG_DONG = "cong_dong";

    private static final int SO_TIN_MOI_LAN_TAI = 30;
    private static final String MUI_GIO = "Asia/Ho_Chi_Minh";

    // Giao diện xếp hạng
    private TextView tabLuotChoi;
    private TextView tabStreak;
    private TextView tabCongDong;
    private TextView txtTieuDeDiem;

    private View thanhChonXepHang;

    private FrameLayout khungTabXepHang;
    private FrameLayout khungCongDong;

    private LinearLayout khungBangXepHang;
    private LinearLayout khungNgayCongDong;

    private ListView listXepHang;
    private ListView listCongDong;

    private EditText edtNoiDungCongDong;

    private TextView btnGuiCongDong;
    private ImageView btnLamMoiCongDong;

    // Dữ liệu
    private final List<NguoiDungXepHang> danhSachNguoiDung =
            new ArrayList<>();

    private final List<BaiVietCongDong> danhSachBaiViet =
            new ArrayList<>();

    private final List<String> danhSachNgay =
            new ArrayList<>();

    private XepHangAdapter xepHangAdapter;
    private CongDongAdapter congDongAdapter;

    private String tabDangChon = TAB_LUOT_CHOI;
    private String ngayDangChon = "";

    // API
    private ApiService apiService;

    private Call<ApiResponse<BangXepHangResponse>> requestBangXepHang;
    private Call<ApiResponse<List<String>>> requestDanhSachNgay;
    private Call<ApiResponse<DanhSachTinNhanCongDongResponse>>
            requestTinNhan;
    private Call<ApiResponse<BaiVietCongDong>> requestGuiTinNhan;

    private int phienTaiDuLieu = 0;

    // Người dùng hiện tại
    private String token = "";
    private long nguoiDungHienTaiId = -1;

    // Phân trang
    private Long tinNhanCuNhatId = null;
    private boolean coTheTaiThem = false;
    private boolean dangTaiTinNhan = false;
    private boolean dangTaiTinCu = false;

    // Socket.IO
    private Socket socket;
    private boolean socketDangKetNoi = false;

    private final Gson gson = new Gson();

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.xep_hang,
                container,
                false
        );

        tabDangChon = TAB_LUOT_CHOI;

        anhXa(view);
        khoiTaoApi();
        khoiTaoNguoiDungHienTai();
        khoiTaoAdapter();
        xuLySuKien();

        capNhatMauTab();

        khungTabXepHang.post(
                this::capNhatKichThuocThanhChon
        );

        layBangXepHang(TAB_LUOT_CHOI);

        return view;
    }

    // ==================== KHỞI TẠO ====================

    private void anhXa(View view) {

        tabLuotChoi = view.findViewById(R.id.tab_luot_choi);
        tabStreak = view.findViewById(R.id.tab_streak);
        tabCongDong = view.findViewById(R.id.tab_cong_dong);

        txtTieuDeDiem = view.findViewById(R.id.txt_tieu_de_diem);

        thanhChonXepHang =
                view.findViewById(R.id.thanh_chon_xep_hang);

        khungTabXepHang =
                view.findViewById(R.id.khung_tab_xep_hang);

        khungBangXepHang =
                view.findViewById(R.id.khung_bang_xep_hang);

        khungCongDong =
                view.findViewById(R.id.khung_cong_dong);

        listXepHang = view.findViewById(R.id.list_xep_hang);
        listCongDong = view.findViewById(R.id.list_cong_dong);

        edtNoiDungCongDong =
                view.findViewById(R.id.edt_noi_dung_cong_dong);

        btnGuiCongDong =
                view.findViewById(R.id.btn_gui_cong_dong);

        btnLamMoiCongDong =
                view.findViewById(R.id.btn_lam_moi_cong_dong);

        khungNgayCongDong = view.findViewById(
                R.id.khung_ngay_cong_dong
        );
    }

    private void khoiTaoApi() {

        apiService = RetrofitClient.getClient()
                .create(ApiService.class);
    }

    private void khoiTaoNguoiDungHienTai() {

        SharedPreferences preferences = requireContext()
                .getSharedPreferences(
                        "Kenglish",
                        Context.MODE_PRIVATE
                );

        token = preferences.getString("token", "");

        nguoiDungHienTaiId = layIdTuToken(token);
    }

    // Lấy ID từ payload JWT
    private long layIdTuToken(String jwt) {

        try {

            if (jwt == null || jwt.trim().isEmpty()) {
                return -1;
            }

            String[] cacPhan = jwt.split("\\.");

            if (cacPhan.length < 2) {
                return -1;
            }

            byte[] duLieu = Base64.decode(
                    cacPhan[1],
                    Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING
            );

            String payload = new String(
                    duLieu,
                    java.nio.charset.StandardCharsets.UTF_8
            );

            JSONObject jsonObject = new JSONObject(payload);

            return jsonObject.optLong("id", -1);

        } catch (Exception e) {

            android.util.Log.e(
                    "CongDong",
                    "Không đọc được ID từ JWT",
                    e
            );

            return -1;
        }
    }

    private void khoiTaoAdapter() {

        xepHangAdapter = new XepHangAdapter(
                requireContext(),
                danhSachNguoiDung,
                XepHangAdapter.LOAI_LUOT_CHOI
        );

        listXepHang.setAdapter(xepHangAdapter);

        congDongAdapter = new CongDongAdapter(
                requireContext(),
                danhSachBaiViet
        );

        congDongAdapter.setNguoiDungHienTaiId(
                nguoiDungHienTaiId
        );

        listCongDong.setAdapter(congDongAdapter);
    }

    // ==================== SỰ KIỆN ====================

    private void xuLySuKien() {

        tabLuotChoi.setOnClickListener(v ->
                chuyenTab(TAB_LUOT_CHOI, 0)
        );

        tabStreak.setOnClickListener(v ->
                chuyenTab(TAB_STREAK, 1)
        );

        tabCongDong.setOnClickListener(v ->
                chuyenTab(TAB_CONG_DONG, 2)
        );

        btnGuiCongDong.setOnClickListener(v ->
                guiTinNhan()
        );

        btnLamMoiCongDong.setOnClickListener(v ->
                lamMoiCongDong()
        );

        listCongDong.setOnScrollListener(
                new android.widget.AbsListView.OnScrollListener() {

                    @Override
                    public void onScrollStateChanged(
                            android.widget.AbsListView view,
                            int scrollState) {
                    }

                    @Override
                    public void onScroll(
                            android.widget.AbsListView view,
                            int firstVisibleItem,
                            int visibleItemCount,
                            int totalItemCount) {

                        if (totalItemCount == 0
                                || dangTaiTinCu
                                || !coTheTaiThem
                                || dangTaiTinNhan) {
                            return;
                        }

                        // Danh sách API sắp xếp mới nhất trước.
                        // Cuộn xuống cuối để tải các tin cũ hơn.
                        if (firstVisibleItem + visibleItemCount
                                >= totalItemCount - 3) {

                            taiTinNhanCu();
                        }
                    }
                }
        );
    }

    // ==================== CHUYỂN TAB ====================

    private void chuyenTab(String tabMoi, int viTri) {

        if (tabMoi.equals(tabDangChon)) {
            return;
        }

        tabDangChon = tabMoi;

        diChuyenThanhChon(viTri);
        capNhatMauTab();

        if (TAB_CONG_DONG.equals(tabMoi)) {

            huyRequestBangXepHang();
            hienThiCongDong();

            ketNoiSocket();
            taiDanhSachNgay();

        } else {

            ngatSocket();

            huyRequestCongDong();

            hienThiBangXepHang(tabMoi);
            layBangXepHang(tabMoi);
        }
    }

    private void hienThiBangXepHang(String loai) {

        khungBangXepHang.setVisibility(View.VISIBLE);
        khungCongDong.setVisibility(View.GONE);

        if (TAB_STREAK.equals(loai)) {

            txtTieuDeDiem.setText("STREAK");

            xepHangAdapter.setLoaiXepHang(
                    XepHangAdapter.LOAI_STREAK
            );

        } else {

            txtTieuDeDiem.setText("LƯỢT");

            xepHangAdapter.setLoaiXepHang(
                    XepHangAdapter.LOAI_LUOT_CHOI
            );
        }
    }

    private void hienThiCongDong() {

        khungBangXepHang.setVisibility(View.GONE);
        khungCongDong.setVisibility(View.VISIBLE);
    }

    // ==================== BẢNG XẾP HẠNG ====================

    private void layBangXepHang(String loai) {

        huyRequestBangXepHang();

        danhSachNguoiDung.clear();
        xepHangAdapter.notifyDataSetChanged();

        final int phienYeuCau = phienTaiDuLieu;

        if (!kiemTraToken()) {
            return;
        }

        requestBangXepHang = apiService.layBangXepHang(
                "Bearer " + token,
                loai
        );

        requestBangXepHang.enqueue(
                new Callback<ApiResponse<BangXepHangResponse>>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<ApiResponse<BangXepHangResponse>> call,
                            @NonNull Response<ApiResponse<BangXepHangResponse>> response) {

                        if (!duocPhepCapNhat(phienYeuCau, loai)) {
                            return;
                        }

                        if (!response.isSuccessful()
                                || response.body() == null
                                || response.body().getData() == null) {

                            thongBaoLoi(
                                    "Không thể tải bảng xếp hạng"
                            );

                            return;
                        }

                        BangXepHangResponse duLieu =
                                response.body().getData();

                        if (duLieu.getDanhSach() == null) {

                            thongBaoLoi(
                                    "Dữ liệu bảng xếp hạng không hợp lệ"
                            );

                            return;
                        }

                        danhSachNguoiDung.clear();

                        danhSachNguoiDung.addAll(
                                duLieu.getDanhSach()
                        );

                        xepHangAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<ApiResponse<BangXepHangResponse>> call,
                            @NonNull Throwable throwable) {

                        if (call.isCanceled()
                                || !duocPhepCapNhat(phienYeuCau, loai)) {
                            return;
                        }

                        thongBaoLoi(
                                "Lỗi kết nối bảng xếp hạng"
                        );
                    }
                }
        );
    }

    private boolean duocPhepCapNhat(
            int phienYeuCau,
            String loai) {

        return isAdded()
                && getView() != null
                && phienYeuCau == phienTaiDuLieu
                && loai.equals(tabDangChon);
    }

    private void huyRequestBangXepHang() {

        phienTaiDuLieu++;

        if (requestBangXepHang != null) {

            requestBangXepHang.cancel();
            requestBangXepHang = null;
        }
    }

    // ==================== CỘNG ĐỒNG: DANH SÁCH NGÀY ====================

    private void taiDanhSachNgay() {

        if (!kiemTraToken() || !TAB_CONG_DONG.equals(tabDangChon)) {
            return;
        }

        if (requestDanhSachNgay != null) {
            requestDanhSachNgay.cancel();
        }

        requestDanhSachNgay = apiService.layDanhSachNgayCongDong(
                "Bearer " + token
        );

        requestDanhSachNgay.enqueue(
                new Callback<ApiResponse<List<String>>>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<ApiResponse<List<String>>> call,
                            @NonNull Response<ApiResponse<List<String>>> response) {

                        if (!dangOCommunity()) {
                            return;
                        }

                        if (!response.isSuccessful()
                                || response.body() == null
                                || response.body().getData() == null) {

                            thongBaoLoi(
                                    "Không thể tải danh sách ngày"
                            );

                            return;
                        }

                        danhSachNgay.clear();

                        danhSachNgay.addAll(
                                response.body().getData()
                        );

                        String homNay = layNgayHienTai();

                        // Luôn có tab hôm nay
                        if (!danhSachNgay.contains(homNay)) {
                            danhSachNgay.add(0, homNay);
                        }

                        veDanhSachNgay();

                        if (ngayDangChon.isEmpty()
                                || !danhSachNgay.contains(ngayDangChon)) {

                            ngayDangChon = homNay;
                        }

                        capNhatTrangThaiChipNgay();
                        taiTinNhanTheoNgay(ngayDangChon, false);
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<ApiResponse<List<String>>> call,
                            @NonNull Throwable throwable) {

                        if (call.isCanceled() || !dangOCommunity()) {
                            return;
                        }

                        thongBaoLoi(
                                "Lỗi kết nối danh sách ngày"
                        );
                    }
                }
        );
    }

    private void veDanhSachNgay() {

        if (khungNgayCongDong == null) {
            return;
        }

        khungNgayCongDong.removeAllViews();

        for (String ngay : danhSachNgay) {

            TextView chip = new TextView(requireContext());

            chip.setText(layNhanNgay(ngay));
            chip.setTextSize(12);
            chip.setGravity(android.view.Gravity.CENTER);
            chip.setPadding(
                    dp(14),
                    dp(7),
                    dp(14),
                    dp(7)
            );

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            dp(34)
                    );

            params.setMargins(
                    0,
                    0,
                    dp(8),
                    0
            );

            chip.setLayoutParams(params);
            chip.setTag(ngay);

            chip.setOnClickListener(v -> {

                String ngayDuocChon = (String) v.getTag();

                if (ngayDuocChon.equals(ngayDangChon)) {
                    return;
                }

                ngayDangChon = ngayDuocChon;

                capNhatTrangThaiChipNgay();

                taiTinNhanTheoNgay(
                        ngayDangChon,
                        false
                );
            });

            khungNgayCongDong.addView(chip);
        }

        capNhatTrangThaiChipNgay();
    }

    private void capNhatTrangThaiChipNgay() {

        if (khungNgayCongDong == null) {
            return;
        }

        for (int i = 0; i < khungNgayCongDong.getChildCount(); i++) {

            View view = khungNgayCongDong.getChildAt(i);

            if (!(view instanceof TextView)) {
                continue;
            }

            TextView chip = (TextView) view;

            boolean duocChon =
                    ngayDangChon.equals(chip.getTag());

            GradientDrawable nen = new GradientDrawable();

            nen.setCornerRadius(dp(18));

            if (duocChon) {

                nen.setColor(Color.parseColor("#1877F2"));
                nen.setStroke(
                        dp(1),
                        Color.parseColor("#1877F2")
                );

                chip.setTextColor(Color.WHITE);
                chip.setTypeface(null, Typeface.BOLD);

            } else {

                nen.setColor(Color.WHITE);
                nen.setStroke(
                        dp(1),
                        Color.parseColor("#DDE8F5")
                );

                chip.setTextColor(
                        Color.parseColor("#526B8C")
                );

                chip.setTypeface(null, Typeface.NORMAL);
            }

            chip.setBackground(nen);
        }
    }

    private String layNhanNgay(String ngay) {

        if (ngay.equals(layNgayHienTai())) {
            return "Hôm nay";
        }

        if (ngay.equals(layNgayHomQua())) {
            return "Hôm qua";
        }

        try {

            SimpleDateFormat dauVao =
                    new SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.getDefault()
                    );

            SimpleDateFormat dauRa =
                    new SimpleDateFormat(
                            "dd/MM",
                            Locale.getDefault()
                    );

            Date date = dauVao.parse(ngay);

            return date == null ? ngay : dauRa.format(date);

        } catch (Exception e) {
            return ngay;
        }
    }

    // ==================== CỘNG ĐỒNG: TẢI TIN NHẮN ====================

    private void taiTinNhanTheoNgay(
            String ngay,
            boolean taiTinCu) {

        if (!kiemTraToken() || !dangOCommunity()) {
            return;
        }

        if (dangTaiTinNhan) {
            return;
        }

        if (!taiTinCu) {

            if (requestTinNhan != null) {
                requestTinNhan.cancel();
            }

            danhSachBaiViet.clear();

            congDongAdapter.notifyDataSetChanged();

            tinNhanCuNhatId = null;
            coTheTaiThem = false;
        }

        dangTaiTinNhan = true;

        if (taiTinCu) {
            dangTaiTinCu = true;
        }

        Long beforeId = taiTinCu
                ? tinNhanCuNhatId
                : null;

        requestTinNhan = apiService.layTinNhanCongDong(
                "Bearer " + token,
                ngay,
                SO_TIN_MOI_LAN_TAI,
                beforeId
        );

        requestTinNhan.enqueue(
                new Callback<ApiResponse<DanhSachTinNhanCongDongResponse>>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<ApiResponse<DanhSachTinNhanCongDongResponse>> call,
                            @NonNull Response<ApiResponse<DanhSachTinNhanCongDongResponse>> response) {

                        dangTaiTinNhan = false;
                        dangTaiTinCu = false;

                        if (!dangOCommunity()
                                || !ngay.equals(ngayDangChon)) {
                            return;
                        }

                        if (!response.isSuccessful()
                                || response.body() == null
                                || response.body().getData() == null) {

                            thongBaoLoi(
                                    "Không thể tải tin nhắn"
                            );

                            return;
                        }

                        DanhSachTinNhanCongDongResponse duLieu =
                                response.body().getData();

                        List<BaiVietCongDong> tinNhan =
                                duLieu.getDanhSach();

                        if (tinNhan != null) {

                            if (taiTinCu) {

                                // Ghi nhớ vị trí cuộn trước khi thêm tin cũ
                                int viTriCu = listCongDong.getFirstVisiblePosition();

                                View viewCu = listCongDong.getChildAt(0);

                                int doLechCu = viewCu == null
                                        ? 0
                                        : viewCu.getTop();

                                for (BaiVietCongDong tin : tinNhan) {
                                    themTinNhanNeuChuaCo(tin, false);
                                }

                                congDongAdapter.notifyDataSetChanged();

                                listCongDong.setSelectionFromTop(
                                        viTriCu + tinNhan.size(),
                                        doLechCu
                                );

                            } else {

                                danhSachBaiViet.clear();

                                for (BaiVietCongDong tin : tinNhan) {
                                    themTinNhanNeuChuaCo(tin, false);
                                }

                                congDongAdapter.notifyDataSetChanged();

                                listCongDong.setSelection(0);
                            }
                        }

                        coTheTaiThem = duLieu.isCoTheTaiThem();

                        tinNhanCuNhatId =
                                duLieu.getTinNhanCuNhatId();

                        if (tinNhanCuNhatId == null
                                && !danhSachBaiViet.isEmpty()) {

                            tinNhanCuNhatId =
                                    danhSachBaiViet.get(
                                            danhSachBaiViet.size() - 1
                                    ).getId();
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<ApiResponse<DanhSachTinNhanCongDongResponse>> call,
                            @NonNull Throwable throwable) {

                        dangTaiTinNhan = false;
                        dangTaiTinCu = false;

                        if (call.isCanceled() || !dangOCommunity()) {
                            return;
                        }

                        thongBaoLoi(
                                "Lỗi kết nối tin nhắn"
                        );
                    }
                }
        );
    }

    private void taiTinNhanCu() {

        if (dangTaiTinCu
                || !coTheTaiThem
                || tinNhanCuNhatId == null
                || danhSachBaiViet.isEmpty()) {
            return;
        }

        taiTinNhanTheoNgay(
                ngayDangChon,
                true
        );
    }

    // ==================== GỬI TIN NHẮN ====================

    private void guiTinNhan() {

        if (!kiemTraToken()) {
            return;
        }

        String noiDung = edtNoiDungCongDong
                .getText()
                .toString()
                .trim();

        if (noiDung.isEmpty()) {

            edtNoiDungCongDong.setError(
                    "Nhập nội dung tin nhắn"
            );

            return;
        }

        if (noiDung.length() > 1000) {

            edtNoiDungCongDong.setError(
                    "Tin nhắn tối đa 1000 ký tự"
            );

            return;
        }

        btnGuiCongDong.setEnabled(false);

        requestGuiTinNhan = apiService.guiTinNhanCongDong(
                "Bearer " + token,
                new GuiTinNhanCongDongRequest(noiDung)
        );

        requestGuiTinNhan.enqueue(
                new Callback<ApiResponse<BaiVietCongDong>>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<ApiResponse<BaiVietCongDong>> call,
                            @NonNull Response<ApiResponse<BaiVietCongDong>> response) {

                        btnGuiCongDong.setEnabled(true);

                        if (!dangOCommunity()) {
                            return;
                        }

                        if (!response.isSuccessful()
                                || response.body() == null
                                || response.body().getData() == null) {

                            thongBaoLoi(
                                    "Không gửi được tin nhắn"
                            );

                            return;
                        }

                        BaiVietCongDong tinNhanMoi =
                                response.body().getData();

                        // Xóa nội dung chỉ khi máy chủ xác nhận thành công
                        edtNoiDungCongDong.setText("");

                        // Nếu đang xem hôm nay thì thêm tin ngay
                        if (ngayDangChon.equals(layNgayHienTai())) {

                            themTinNhanNeuChuaCo(
                                    tinNhanMoi,
                                    true
                            );

                            congDongAdapter.notifyDataSetChanged();

                            listCongDong.setSelection(0);

                        } else {

                            taiDanhSachNgay();
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<ApiResponse<BaiVietCongDong>> call,
                            @NonNull Throwable throwable) {

                        btnGuiCongDong.setEnabled(true);

                        if (call.isCanceled() || !dangOCommunity()) {
                            return;
                        }

                        thongBaoLoi(
                                "Lỗi kết nối khi gửi tin nhắn"
                        );
                    }
                }
        );
    }

    // ==================== CHỐNG TRÙNG TIN NHẮN ====================

    private void themTinNhanNeuChuaCo(
            BaiVietCongDong tinNhan,
            boolean duaLenDau) {

        if (tinNhan == null) {
            return;
        }

        for (BaiVietCongDong tinCu : danhSachBaiViet) {
            if (tinCu.getId() == tinNhan.getId()) {
                return;
            }
        }

        // Luôn thêm tin nhắn mới xuống cuối danh sách
        danhSachBaiViet.add(tinNhan);
    }

    // ==================== SOCKET.IO ====================

    private void ketNoiSocket() {

        if (!kiemTraToken()
                || socketDangKetNoi
                || !dangOCommunity()) {
            return;
        }

        try {

            ngatSocket();

            IO.Options options = new IO.Options();

            Map<String, String> auth = new HashMap<>();
            auth.put("token", token);

            options.auth = auth;
            options.reconnection = true;
            options.reconnectionAttempts = Integer.MAX_VALUE;
            options.reconnectionDelay = 1000;

            socket = IO.socket(
                    RetrofitClient.getBaseUrl(),
                    options
            );

            socket.on(
                    Socket.EVENT_CONNECT,
                    args -> {

                        socketDangKetNoi = true;

                        android.util.Log.d(
                                "CongDongSocket",
                                "Đã kết nối Socket.IO"
                        );
                    }
            );

            socket.on(
                    Socket.EVENT_CONNECT_ERROR,
                    args -> {

                        socketDangKetNoi = false;

                        android.util.Log.e(
                                "CongDongSocket",
                                "Lỗi kết nối Socket.IO: "
                                        + (args.length > 0 ? args[0] : "")
                        );
                    }
            );

            socket.on(
                    "tin_nhan_moi",
                    args -> {

                        if (args.length == 0 || args[0] == null) {
                            return;
                        }

                        try {

                            BaiVietCongDong tinNhan =
                                    gson.fromJson(
                                            args[0].toString(),
                                            BaiVietCongDong.class
                                    );

                            if (tinNhan == null) {
                                return;
                            }

                            if (!isAdded()) {
                                return;
                            }

                            requireActivity().runOnUiThread(() -> {

                                if (!dangOCommunity()) {
                                    return;
                                }

                                String ngayTinNhan =
                                        layNgayTuThoiGian(
                                                tinNhan.getThoiGian()
                                        );

                                if (ngayDangChon.equals(ngayTinNhan)) {

                                    themTinNhanNeuChuaCo(
                                            tinNhan,
                                            true
                                    );

                                    congDongAdapter.notifyDataSetChanged();

                                    listCongDong.setSelection(0);

                                } else if (ngayTinNhan != null) {

                                    if (!danhSachNgay.contains(ngayTinNhan)) {

                                        danhSachNgay.add(0, ngayTinNhan);

                                        veDanhSachNgay();
                                    }
                                }
                            });

                        } catch (Exception e) {

                            android.util.Log.e(
                                    "CongDongSocket",
                                    "Không đọc được tin nhắn Socket",
                                    e
                            );
                        }
                    }
            );

            socket.on(
                    Socket.EVENT_DISCONNECT,
                    args -> {

                        socketDangKetNoi = false;

                        android.util.Log.d(
                                "CongDongSocket",
                                "Socket.IO đã ngắt kết nối"
                        );
                    }
            );

            socket.connect();

        } catch (Exception e) {

            android.util.Log.e(
                    "CongDongSocket",
                    "Không thể khởi tạo Socket.IO",
                    e
            );
        }
    }

    private void ngatSocket() {

        socketDangKetNoi = false;

        if (socket != null) {

            socket.off();

            socket.disconnect();
            socket.close();

            socket = null;
        }
    }

    // ==================== LÀM MỚI ====================

    private void lamMoiCongDong() {

        if (!kiemTraToken()) {
            return;
        }

        taiDanhSachNgay();

        if (!socketDangKetNoi) {
            ketNoiSocket();
        }
    }

    // ==================== HỦY REQUEST ====================

    private void huyRequestCongDong() {

        if (requestDanhSachNgay != null) {
            requestDanhSachNgay.cancel();
            requestDanhSachNgay = null;
        }

        if (requestTinNhan != null) {
            requestTinNhan.cancel();
            requestTinNhan = null;
        }

        if (requestGuiTinNhan != null) {
            requestGuiTinNhan.cancel();
            requestGuiTinNhan = null;
        }

        dangTaiTinNhan = false;
        dangTaiTinCu = false;
    }

    // ==================== TIỆN ÍCH ====================

    private boolean kiemTraToken() {

        if (token == null || token.trim().isEmpty()) {

            thongBaoLoi(
                    "Vui lòng đăng nhập để sử dụng chức năng này"
            );

            return false;
        }

        return true;
    }

    private boolean dangOCommunity() {

        return isAdded()
                && getView() != null
                && TAB_CONG_DONG.equals(tabDangChon);
    }

    private String layNgayHienTai() {

        SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
        );

        sdf.setTimeZone(
                TimeZone.getTimeZone(MUI_GIO)
        );

        return sdf.format(new Date());
    }

    private String layNgayHomQua() {

        Calendar calendar = Calendar.getInstance(
                TimeZone.getTimeZone(MUI_GIO)
        );

        calendar.add(Calendar.DAY_OF_YEAR, -1);

        SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
        );

        sdf.setTimeZone(
                TimeZone.getTimeZone(MUI_GIO)
        );

        return sdf.format(calendar.getTime());
    }

    private String layNgayTuThoiGian(String thoiGian) {

        if (thoiGian == null || thoiGian.length() < 10) {
            return null;
        }

        return thoiGian.substring(0, 10);
    }

    private int dp(float giaTri) {

        return (int) (
                giaTri * getResources()
                        .getDisplayMetrics()
                        .density + 0.5f
        );
    }

    private void thongBaoLoi(String thongBao) {

        if (isAdded()) {

            Toast.makeText(
                    requireContext(),
                    thongBao,
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // ==================== THANH CHUYỂN TAB ====================

    private void diChuyenThanhChon(int viTri) {

        float chieuRongTab = thanhChonXepHang.getWidth();

        thanhChonXepHang.animate()
                .translationX(chieuRongTab * viTri)
                .setDuration(220)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    private void capNhatKichThuocThanhChon() {

        int chieuRong = khungTabXepHang.getWidth()
                - khungTabXepHang.getPaddingLeft()
                - khungTabXepHang.getPaddingRight();

        int chieuRongMoi = chieuRong / 3;

        FrameLayout.LayoutParams params =
                (FrameLayout.LayoutParams)
                        thanhChonXepHang.getLayoutParams();

        params.width = chieuRongMoi;

        thanhChonXepHang.setLayoutParams(params);

        int viTri = TAB_STREAK.equals(tabDangChon) ? 1
                : TAB_CONG_DONG.equals(tabDangChon) ? 2 : 0;

        thanhChonXepHang.setTranslationX(
                chieuRongMoi * viTri
        );
    }

    private void capNhatMauTab() {

        int mauThuong = Color.parseColor("#7C8492");
        int mauChon = Color.parseColor("#1877F2");

        TextView[] danhSachTab = {
                tabLuotChoi,
                tabStreak,
                tabCongDong
        };

        for (TextView tab : danhSachTab) {

            boolean duocChon =
                    (tab == tabLuotChoi
                            && TAB_LUOT_CHOI.equals(tabDangChon))
                            || (tab == tabStreak
                            && TAB_STREAK.equals(tabDangChon))
                            || (tab == tabCongDong
                            && TAB_CONG_DONG.equals(tabDangChon));

            tab.setSelected(duocChon);

            tab.setTextColor(
                    duocChon ? mauChon : mauThuong
            );

            tab.setTypeface(
                    null,
                    duocChon
                            ? Typeface.BOLD
                            : Typeface.NORMAL
            );
        }
    }

    // ==================== VÒNG ĐỜI FRAGMENT ====================

    @Override
    public void onHiddenChanged(boolean hidden) {

        super.onHiddenChanged(hidden);

        if (hidden) {

            huyRequestBangXepHang();
            huyRequestCongDong();

            ngatSocket();

            return;
        }

        if (!isAdded()
                || getView() == null
                || xepHangAdapter == null) {
            return;
        }

        capNhatMauTab();

        if (TAB_CONG_DONG.equals(tabDangChon)) {

            hienThiCongDong();

            ketNoiSocket();
            taiDanhSachNgay();

        } else {

            hienThiBangXepHang(tabDangChon);

            layBangXepHang(tabDangChon);
        }
    }

    @Override
    public void onDestroyView() {

        huyRequestBangXepHang();
        huyRequestCongDong();

        ngatSocket();

        super.onDestroyView();
    }
}
