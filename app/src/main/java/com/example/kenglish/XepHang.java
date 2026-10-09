package com.example.kenglish;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import com.example.kenglish.model.NguoiDungXepHang;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class XepHang extends Fragment {
    private static final String TAB_LUOT_CHOI = "luot_choi";
    private static final String TAB_STREAK = "streak";
    private static final String TAB_CONG_DONG = "cong_dong";
    private TextView tabLuotChoi;
    private TextView tabStreak;
    private TextView tabCongDong;
    private TextView txtTieuDeDiem;
    private View thanhChonXepHang;
    private FrameLayout khungTabXepHang;
    private LinearLayout khungBangXepHang;
    private FrameLayout khungCongDong;
    private ListView listXepHang;
    private ListView listCongDong;
    private EditText edtNoiDungCongDong;
    private TextView btnGuiCongDong;
    private TextView btnLamMoiCongDong;
    private final List<NguoiDungXepHang> danhSachNguoiDung = new ArrayList<>();
    private final List<BaiVietCongDong> danhSachBaiViet = new ArrayList<>();
    private XepHangAdapter xepHangAdapter;
    private CongDongAdapter congDongAdapter;
    private String tabDangChon = TAB_LUOT_CHOI;
    private Call<ApiResponse<BangXepHangResponse>> requestBangXepHang;
    private int phienTaiDuLieu = 0;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.xep_hang, container, false);
        tabDangChon = TAB_LUOT_CHOI;
        anhXa(view);
        khoiTaoDuLieuMauCongDong();
        khoiTaoAdapter();
        xuLySuKien();
        capNhatMauTab();
        khungTabXepHang.post(this::capNhatKichThuocThanhChon);
        layBangXepHang(TAB_LUOT_CHOI);
        return view;
    }

    private void anhXa(View view) {
        tabLuotChoi = view.findViewById(R.id.tab_luot_choi);
        tabStreak = view.findViewById(R.id.tab_streak);
        tabCongDong = view.findViewById(R.id.tab_cong_dong);
        txtTieuDeDiem = view.findViewById(R.id.txt_tieu_de_diem);
        thanhChonXepHang = view.findViewById(R.id.thanh_chon_xep_hang);
        khungTabXepHang = view.findViewById(R.id.khung_tab_xep_hang);
        khungBangXepHang = view.findViewById(R.id.khung_bang_xep_hang);
        khungCongDong = view.findViewById(R.id.khung_cong_dong);
        listXepHang = view.findViewById(R.id.list_xep_hang);
        listCongDong = view.findViewById(R.id.list_cong_dong);
        edtNoiDungCongDong = view.findViewById(R.id.edt_noi_dung_cong_dong);
        btnGuiCongDong = view.findViewById(R.id.btn_gui_cong_dong);
        btnLamMoiCongDong = view.findViewById(R.id.btn_lam_moi_cong_dong);
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
        listCongDong.setAdapter(congDongAdapter);
    }

    private void xuLySuKien() {
        tabLuotChoi.setOnClickListener(v ->
                chuyenTab(TAB_LUOT_CHOI, 0));
        tabStreak.setOnClickListener(v ->
                chuyenTab(TAB_STREAK, 1));
        tabCongDong.setOnClickListener(v ->
                chuyenTab(TAB_CONG_DONG, 2));
        btnGuiCongDong.setOnClickListener(v -> guiBaiViet());
        btnLamMoiCongDong.setOnClickListener(v -> {
            Toast.makeText(
                    requireContext(),
                    "Đã làm mới cộng đồng",
                    Toast.LENGTH_SHORT
            ).show();
        });
    }

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
        } else {
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

    private void layBangXepHang(String loai) {
        huyRequestBangXepHang();
        danhSachNguoiDung.clear();
        xepHangAdapter.notifyDataSetChanged();
        final int phienYeuCau = phienTaiDuLieu;
        SharedPreferences preferences = requireContext()
                .getSharedPreferences("Kenglish", Context.MODE_PRIVATE);
        String token = preferences.getString("token", "");
        if (token == null || token.trim().isEmpty()) {
            Toast.makeText(
                    requireContext(),
                    "Vui lòng đăng nhập để xem bảng xếp hạng",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }
        ApiService apiService = RetrofitClient.getClient()
                .create(
                        ApiService.class
                );
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
                            thongBaoLoi("Không thể tải bảng xếp hạng");
                            return;
                        }
                        BangXepHangResponse duLieu =
                                response.body().getData();
                        if (duLieu.getDanhSach() == null) {
                            thongBaoLoi("Dữ liệu bảng xếp hạng không hợp lệ");
                            return;
                        }
                        danhSachNguoiDung.clear();
                        danhSachNguoiDung.addAll(duLieu.getDanhSach());
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
                        thongBaoLoi("Lỗi kết nối bảng xếp hạng");
                    }
                }
        );
    }

    private boolean duocPhepCapNhat(int phienYeuCau, String loai) {
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

    private void thongBaoLoi(String thongBao) {
        if (isAdded()) {
            Toast.makeText(
                    requireContext(),
                    thongBao,
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

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
                (FrameLayout.LayoutParams) thanhChonXepHang.getLayoutParams();
        params.width = chieuRongMoi;
        thanhChonXepHang.setLayoutParams(params);
        int viTri = TAB_STREAK.equals(tabDangChon) ? 1
                : TAB_CONG_DONG.equals(tabDangChon) ? 2 : 0;
        thanhChonXepHang.setTranslationX(chieuRongMoi * viTri);
    }

    private void capNhatMauTab() {
        int mauThuong = Color.parseColor("#AAB4C5");
        int mauChon = Color.parseColor("#37659C");
        tabLuotChoi.setTextColor(mauThuong);
        tabStreak.setTextColor(mauThuong);
        tabCongDong.setTextColor(mauThuong);
        if (TAB_LUOT_CHOI.equals(tabDangChon)) {
            tabLuotChoi.setTextColor(mauChon);
        } else if (TAB_STREAK.equals(tabDangChon)) {
            tabStreak.setTextColor(mauChon);
        } else {
            tabCongDong.setTextColor(mauChon);
        }
    }

    private void guiBaiViet() {
        String noiDung = edtNoiDungCongDong
                .getText().toString().trim();
        if (noiDung.isEmpty()) {
            edtNoiDungCongDong.setError("Nhập nội dung");
            return;
        }
        BaiVietCongDong baiVietMoi = new BaiVietCongDong(
                danhSachBaiViet.size() + 1,
                "Kiên",
                "",
                noiDung,
                "Vừa xong"
        );
        danhSachBaiViet.add(0, baiVietMoi);
        congDongAdapter.notifyDataSetChanged();
        edtNoiDungCongDong.setText("");
        listCongDong.setSelection(0);
    }

    private void khoiTaoDuLieuMauCongDong() {
        danhSachBaiViet.clear();
        danhSachBaiViet.add(new BaiVietCongDong(
                1,
                "Quynh Nnd",
                "",
                "Ai chuỗi đối tui k",
                "12:10"
        ));
        danhSachBaiViet.add(new BaiVietCongDong(
                2,
                "Chi Phương",
                "",
                "Làm sao nghe hiểu listening đây",
                "12:11"
        ));
        danhSachBaiViet.add(new BaiVietCongDong(
                3,
                "yuriholic",
                "",
                "Ê nhớ là mới xem bảng xếp hạng trước đây có chục phút mà giờ top 1 rồi kìa",
                "12:51"
        ));
    }

    @Override
    public void onDestroyView() {
        huyRequestBangXepHang();
        super.onDestroyView();
    }
}
