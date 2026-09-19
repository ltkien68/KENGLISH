package com.example.kenglish;

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
import com.example.kenglish.model.BaiVietCongDong;
import com.example.kenglish.model.NguoiDungXepHang;

import java.util.ArrayList;
import java.util.List;


/**
 * Fragment hiển thị màn hình Xếp hạng.
 *
 * Gồm 3 tab:
 * - Lượt chơi game.
 * - Streak.
 * - Cộng đồng.
 *
 * Hiện tại sử dụng dữ liệu mẫu.
 * Sau này chỉ cần thay dữ liệu mẫu bằng dữ liệu Backend.
 */
public class XepHang extends Fragment {

    private static final String TAB_LUOT_CHOI =
            "luot_choi";

    private static final String TAB_STREAK =
            "streak";

    private static final String TAB_CONG_DONG =
            "cong_dong";


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


    private final List<NguoiDungXepHang>
            danhSachNguoiDung = new ArrayList<>();

    private final List<BaiVietCongDong>
            danhSachBaiViet = new ArrayList<>();


    private XepHangAdapter xepHangAdapter;

    private CongDongAdapter congDongAdapter;


    private String tabDangChon =
            TAB_LUOT_CHOI;


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


        anhXa(view);

        khoiTaoDuLieuMau();

        khoiTaoAdapter();

        xuLySuKien();


        // Tính kích thước indicator sau khi layout hoàn thành.
        khungTabXepHang.post(
                this::capNhatKichThuocThanhChon
        );


        return view;
    }


    /**
     * Ánh xạ View.
     */
    private void anhXa(View view) {

        tabLuotChoi =
                view.findViewById(
                        R.id.tab_luot_choi
                );

        tabStreak =
                view.findViewById(
                        R.id.tab_streak
                );

        tabCongDong =
                view.findViewById(
                        R.id.tab_cong_dong
                );


        txtTieuDeDiem =
                view.findViewById(
                        R.id.txt_tieu_de_diem
                );


        thanhChonXepHang =
                view.findViewById(
                        R.id.thanh_chon_xep_hang
                );

        khungTabXepHang =
                view.findViewById(
                        R.id.khung_tab_xep_hang
                );


        khungBangXepHang =
                view.findViewById(
                        R.id.khung_bang_xep_hang
                );

        khungCongDong =
                view.findViewById(
                        R.id.khung_cong_dong
                );


        listXepHang =
                view.findViewById(
                        R.id.list_xep_hang
                );

        listCongDong =
                view.findViewById(
                        R.id.list_cong_dong
                );


        edtNoiDungCongDong =
                view.findViewById(
                        R.id.edt_noi_dung_cong_dong
                );

        btnGuiCongDong =
                view.findViewById(
                        R.id.btn_gui_cong_dong
                );

        btnLamMoiCongDong =
                view.findViewById(
                        R.id.btn_lam_moi_cong_dong
                );
    }


    /**
     * Khởi tạo Adapter.
     */
    private void khoiTaoAdapter() {

        xepHangAdapter =
                new XepHangAdapter(
                        requireContext(),
                        danhSachNguoiDung,
                        XepHangAdapter.LOAI_LUOT_CHOI
                );


        listXepHang.setAdapter(
                xepHangAdapter
        );


        congDongAdapter =
                new CongDongAdapter(
                        requireContext(),
                        danhSachBaiViet
                );


        listCongDong.setAdapter(
                congDongAdapter
        );
    }


    /**
     * Thiết lập các sự kiện.
     */
    private void xuLySuKien() {

        tabLuotChoi.setOnClickListener(v ->
                chuyenTab(
                        TAB_LUOT_CHOI,
                        0
                )
        );


        tabStreak.setOnClickListener(v ->
                chuyenTab(
                        TAB_STREAK,
                        1
                )
        );


        tabCongDong.setOnClickListener(v ->
                chuyenTab(
                        TAB_CONG_DONG,
                        2
                )
        );


        btnGuiCongDong.setOnClickListener(v ->
                guiBaiViet()
        );


        btnLamMoiCongDong.setOnClickListener(v -> {

            Toast.makeText(
                    requireContext(),
                    "Đã làm mới cộng đồng",
                    Toast.LENGTH_SHORT
            ).show();

            /*
             * Sau này gọi API tải lại bài viết tại đây.
             */
        });
    }


    /**
     * Chuyển giữa ba tab.
     */
    private void chuyenTab(
            String tabMoi,
            int viTri) {

        if (tabMoi.equals(tabDangChon)) {
            return;
        }


        tabDangChon = tabMoi;


        diChuyenThanhChon(
                viTri
        );

        capNhatMauTab();


        if (TAB_CONG_DONG.equals(tabMoi)) {

            hienThiCongDong();

        } else {

            hienThiBangXepHang(
                    tabMoi
            );
        }
    }


    /**
     * Hiển thị bảng lượt chơi hoặc streak.
     */
    private void hienThiBangXepHang(
            String loai) {

        khungBangXepHang.setVisibility(
                View.VISIBLE
        );

        khungCongDong.setVisibility(
                View.GONE
        );


        if (TAB_STREAK.equals(loai)) {

            txtTieuDeDiem.setText(
                    "STREAK"
            );

            xepHangAdapter.setLoaiXepHang(
                    XepHangAdapter.LOAI_STREAK
            );

        } else {

            txtTieuDeDiem.setText(
                    "LƯỢT"
            );

            xepHangAdapter.setLoaiXepHang(
                    XepHangAdapter.LOAI_LUOT_CHOI
            );
        }
    }


    /**
     * Hiển thị phần Cộng đồng.
     */
    private void hienThiCongDong() {

        khungBangXepHang.setVisibility(
                View.GONE
        );

        khungCongDong.setVisibility(
                View.VISIBLE
        );
    }


    /**
     * Di chuyển nền tím tới tab được chọn.
     */
    private void diChuyenThanhChon(
            int viTri) {

        float chieuRongTab =
                thanhChonXepHang.getWidth();


        thanhChonXepHang
                .animate()
                .translationX(
                        chieuRongTab * viTri
                )
                .setDuration(220)
                .setInterpolator(
                        new DecelerateInterpolator()
                )
                .start();
    }


    /**
     * Tính chiều rộng indicator bằng 1/3 thanh tab.
     */
    private void capNhatKichThuocThanhChon() {

        int chieuRong =
                khungTabXepHang.getWidth()
                        - khungTabXepHang.getPaddingLeft()
                        - khungTabXepHang.getPaddingRight();


        int chieuRongMoi =
                chieuRong / 3;


        FrameLayout.LayoutParams params =
                (FrameLayout.LayoutParams)
                        thanhChonXepHang.getLayoutParams();


        params.width =
                chieuRongMoi;


        thanhChonXepHang.setLayoutParams(
                params
        );
    }


    /**
     * Cập nhật màu chữ của tab.
     */
    private void capNhatMauTab() {

        int mauThuong =
                Color.parseColor("#AAB4C5");

        int mauChon =
                Color.WHITE;


        tabLuotChoi.setTextColor(
                mauThuong
        );

        tabStreak.setTextColor(
                mauThuong
        );

        tabCongDong.setTextColor(
                mauThuong
        );


        if (TAB_LUOT_CHOI.equals(tabDangChon)) {

            tabLuotChoi.setTextColor(
                    mauChon
            );

        } else if (
                TAB_STREAK.equals(tabDangChon)) {

            tabStreak.setTextColor(
                    mauChon
            );

        } else {

            tabCongDong.setTextColor(
                    mauChon
            );
        }
    }


    /**
     * Gửi bài viết mới.
     *
     * Hiện tại chỉ thêm vào List.
     * Sau này thay bằng API POST.
     */
    private void guiBaiViet() {

        String noiDung =
                edtNoiDungCongDong
                        .getText()
                        .toString()
                        .trim();


        if (noiDung.isEmpty()) {

            edtNoiDungCongDong.setError(
                    "Nhập nội dung"
            );

            return;
        }


        BaiVietCongDong baiVietMoi =
                new BaiVietCongDong(
                        danhSachBaiViet.size() + 1,
                        "Kiên",
                        "",
                        noiDung,
                        "Vừa xong"
                );


        danhSachBaiViet.add(
                0,
                baiVietMoi
        );


        congDongAdapter.notifyDataSetChanged();


        edtNoiDungCongDong.setText("");


        listCongDong.setSelection(0);
    }


    /**
     * Dữ liệu mẫu để kiểm tra giao diện.
     *
     * Khi có Backend sẽ bỏ method này
     * và lấy danh sách từ API.
     */
    private void khoiTaoDuLieuMau() {

        danhSachNguoiDung.clear();


        danhSachNguoiDung.add(
                new NguoiDungXepHang(
                        1,
                        "Trần Thu Nhung",
                        "",
                        121,
                        265
                )
        );


        danhSachNguoiDung.add(
                new NguoiDungXepHang(
                        2,
                        "Hùng đẹp zai >:]",
                        "",
                        93,
                        260
                )
        );


        danhSachNguoiDung.add(
                new NguoiDungXepHang(
                        3,
                        "Bảo Thy",
                        "",
                        89,
                        260
                )
        );


        danhSachNguoiDung.add(
                new NguoiDungXepHang(
                        4,
                        "Vy Tường",
                        "",
                        76,
                        253
                )
        );


        danhSachNguoiDung.add(
                new NguoiDungXepHang(
                        5,
                        "Bằng",
                        "",
                        56,
                        249
                )
        );


        danhSachNguoiDung.add(
                new NguoiDungXepHang(
                        6,
                        "Tu Nguyen",
                        "",
                        48,
                        238
                )
        );


        danhSachBaiViet.clear();


        danhSachBaiViet.add(
                new BaiVietCongDong(
                        1,
                        "Quynh Nnd",
                        "",
                        "Ai chuỗi đối tui k",
                        "12:10"
                )
        );


        danhSachBaiViet.add(
                new BaiVietCongDong(
                        2,
                        "Chi Phương",
                        "",
                        "Làm sao nghe hiểu listening đây",
                        "12:11"
                )
        );


        danhSachBaiViet.add(
                new BaiVietCongDong(
                        3,
                        "yuriholic",
                        "",
                        "Ê nhớ là mới xem bảng xếp hạng trước đây có chục phút mà giờ top 1 rồi kìa",
                        "12:51"
                )
        );
    }
}