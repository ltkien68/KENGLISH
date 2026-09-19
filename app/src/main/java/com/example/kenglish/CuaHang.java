package com.example.kenglish;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.kenglish.adapter.SanPhamCuaHangAdapter;
import com.example.kenglish.model.SanPhamCuaHang;

import java.util.ArrayList;
import java.util.List;


/**
 * Fragment hiển thị màn hình Cửa hàng.
 *
 * Cửa hàng gồm 3 loại sản phẩm:
 * - Avatar
 * - Hình nền
 * - Đặc biệt
 *
 * Hiện tại sử dụng dữ liệu mẫu.
 * Sau này có thể thay dữ liệu mẫu bằng dữ liệu Backend/API.
 */
public class CuaHang extends Fragment {

    private static final String LOAI_AVATAR =
            "avatar";

    private static final String LOAI_HINH_NEN =
            "hinh_nen";

    private static final String LOAI_DAC_BIET =
            "dac_biet";


    private TextView txtSoXu;

    private TextView tabAvatar;
    private TextView tabHinhNen;
    private TextView tabDacBiet;

    private View thanhChonTab;

    private FrameLayout khungTabCuaHang;

    private GridView gridSanPham;


    private final List<SanPhamCuaHang>
            danhSachTatCaSanPham = new ArrayList<>();


    private final List<SanPhamCuaHang>
            danhSachDangHienThi = new ArrayList<>();


    private SanPhamCuaHangAdapter adapter;


    // Tab được chọn mặc định.
    private String loaiDangChon = LOAI_AVATAR;


    // Số xu tạm thời để test.
    private int soXu = 19209;


    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.cua_hang,
                container,
                false
        );


        anhXa(view);

        khoiTaoDuLieuMau();

        khoiTaoAdapter();

        xuLySuKien();

        hienThiSoXu();

        hienThiSanPhamTheoLoai(
                LOAI_AVATAR,
                false
        );


        // Đợi layout hoàn tất rồi mới tính chiều rộng indicator.
        khungTabCuaHang.post(
                this::capNhatKichThuocThanhChon
        );


        return view;
    }


    /**
     * Ánh xạ các thành phần giao diện.
     */
    private void anhXa(View view) {

        txtSoXu = view.findViewById(
                R.id.txt_so_xu
        );


        tabAvatar = view.findViewById(
                R.id.tab_avatar
        );

        tabHinhNen = view.findViewById(
                R.id.tab_hinh_nen
        );

        tabDacBiet = view.findViewById(
                R.id.tab_dac_biet
        );


        thanhChonTab = view.findViewById(
                R.id.thanh_chon_tab
        );

        khungTabCuaHang = view.findViewById(
                R.id.khung_tab_cua_hang
        );


        gridSanPham = view.findViewById(
                R.id.grid_san_pham
        );
    }


    /**
     * Khởi tạo Adapter cho GridView.
     */
    private void khoiTaoAdapter() {

        adapter = new SanPhamCuaHangAdapter(
                requireContext(),
                danhSachDangHienThi,
                new SanPhamCuaHangAdapter.SuKienSanPham() {

                    @Override
                    public void khiBamMua(
                            SanPhamCuaHang sanPham) {

                        muaSanPham(sanPham);
                    }


                    @Override
                    public void khiBamSuDung(
                            SanPhamCuaHang sanPham) {

                        suDungSanPham(sanPham);
                    }


                    @Override
                    public void khiBamGo(
                            SanPhamCuaHang sanPham) {

                        goSanPham(sanPham);
                    }
                }
        );


        gridSanPham.setAdapter(adapter);
    }


    /**
     * Thiết lập sự kiện cho ba tab.
     */
    private void xuLySuKien() {

        tabAvatar.setOnClickListener(v -> {

            chuyenTab(
                    LOAI_AVATAR,
                    0
            );
        });


        tabHinhNen.setOnClickListener(v -> {

            chuyenTab(
                    LOAI_HINH_NEN,
                    1
            );
        });


        tabDacBiet.setOnClickListener(v -> {

            chuyenTab(
                    LOAI_DAC_BIET,
                    2
            );
        });
    }


    /**
     * Chuyển sang một loại sản phẩm khác.
     */
    private void chuyenTab(
            String loaiMoi,
            int viTriTab) {

        if (loaiMoi.equals(loaiDangChon)) {
            return;
        }


        loaiDangChon = loaiMoi;


        // Di chuyển nền tím tới tab mới.
        diChuyenThanhChon(viTriTab);


        // Cập nhật màu chữ.
        capNhatMauTab();


        // Animation nhẹ cho danh sách sản phẩm.
        gridSanPham.animate()
                .alpha(0f)
                .setDuration(100)
                .withEndAction(() -> {

                    hienThiSanPhamTheoLoai(
                            loaiMoi,
                            true
                    );

                    gridSanPham.setAlpha(0f);

                    gridSanPham.animate()
                            .alpha(1f)
                            .setDuration(160)
                            .start();
                })
                .start();
    }


    /**
     * Di chuyển indicator tím sang tab được chọn.
     */
    private void diChuyenThanhChon(
            int viTriTab) {

        float chieuRongTab =
                thanhChonTab.getWidth();


        float viTriMoi =
                chieuRongTab * viTriTab;


        thanhChonTab.animate()
                .translationX(viTriMoi)
                .setDuration(220)
                .setInterpolator(
                        new DecelerateInterpolator()
                )
                .start();
    }


    /**
     * Tính chiều rộng của indicator.
     *
     * Indicator chiếm đúng 1/3 thanh tab.
     */
    private void capNhatKichThuocThanhChon() {

        int chieuRongKhung =
                khungTabCuaHang.getWidth();


        int paddingTrai =
                khungTabCuaHang.getPaddingLeft();

        int paddingPhai =
                khungTabCuaHang.getPaddingRight();


        int chieuRongSuDung =
                chieuRongKhung
                        - paddingTrai
                        - paddingPhai;


        int chieuRongMoi =
                chieuRongSuDung / 3;


        FrameLayout.LayoutParams params =
                (FrameLayout.LayoutParams)
                        thanhChonTab.getLayoutParams();


        params.width = chieuRongMoi;


        thanhChonTab.setLayoutParams(
                params
        );
    }


    /**
     * Cập nhật màu chữ của ba tab.
     */
    private void capNhatMauTab() {

        int mauKhongChon =
                Color.parseColor("#AAB4C5");

        int mauDangChon =
                Color.WHITE;


        tabAvatar.setTextColor(
                mauKhongChon
        );

        tabHinhNen.setTextColor(
                mauKhongChon
        );

        tabDacBiet.setTextColor(
                mauKhongChon
        );


        if (LOAI_AVATAR.equals(loaiDangChon)) {

            tabAvatar.setTextColor(
                    mauDangChon
            );

        } else if (
                LOAI_HINH_NEN.equals(loaiDangChon)) {

            tabHinhNen.setTextColor(
                    mauDangChon
            );

        } else {

            tabDacBiet.setTextColor(
                    mauDangChon
            );
        }
    }


    /**
     * Lọc và hiển thị sản phẩm theo loại.
     */
    private void hienThiSanPhamTheoLoai(
            String loai,
            boolean capNhatAdapter) {

        danhSachDangHienThi.clear();


        for (SanPhamCuaHang sanPham
                : danhSachTatCaSanPham) {

            if (loai.equals(
                    sanPham.getLoaiSanPham())) {

                danhSachDangHienThi.add(
                        sanPham
                );
            }
        }


        if (capNhatAdapter && adapter != null) {

            adapter.notifyDataSetChanged();
        }
    }


    /**
     * Hiển thị số xu hiện tại.
     */
    private void hienThiSoXu() {

        txtSoXu.setText(
                String.format(
                        java.util.Locale.getDefault(),
                        "%,d",
                        soXu
                ).replace(",", ".")
        );
    }


    /**
     * Xử lý mua sản phẩm.
     *
     * HIỆN TẠI:
     * Trừ xu trực tiếp để test giao diện.
     *
     * SAU NÀY:
     * Thay phần này bằng lời gọi Backend/API.
     */
    private void muaSanPham(
            SanPhamCuaHang sanPham) {

        if (soXu < sanPham.getGia()) {

            Toast.makeText(
                    requireContext(),
                    "Bạn không đủ xu",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        soXu -= sanPham.getGia();

        sanPham.setDaSoHuu(true);


        hienThiSoXu();

        adapter.notifyDataSetChanged();


        Toast.makeText(
                requireContext(),
                "Đã mua "
                        + sanPham.getTenSanPham(),
                Toast.LENGTH_SHORT
        ).show();
    }


    /**
     * Sử dụng một sản phẩm đã sở hữu.
     */
    private void suDungSanPham(
            SanPhamCuaHang sanPham) {

        // Chỉ cho phép một sản phẩm cùng loại
        // được sử dụng tại một thời điểm.
        for (SanPhamCuaHang item
                : danhSachTatCaSanPham) {

            if (item.getLoaiSanPham()
                    .equals(
                            sanPham.getLoaiSanPham()
                    )) {

                item.setDangSuDung(false);
            }
        }


        sanPham.setDangSuDung(true);

        adapter.notifyDataSetChanged();


        Toast.makeText(
                requireContext(),
                "Đang sử dụng "
                        + sanPham.getTenSanPham(),
                Toast.LENGTH_SHORT
        ).show();
    }


    /**
     * Gỡ sản phẩm đang được sử dụng.
     */
    private void goSanPham(
            SanPhamCuaHang sanPham) {

        sanPham.setDangSuDung(false);

        adapter.notifyDataSetChanged();


        Toast.makeText(
                requireContext(),
                "Đã gỡ "
                        + sanPham.getTenSanPham(),
                Toast.LENGTH_SHORT
        ).show();
    }


    /**
     * Tạo dữ liệu mẫu để kiểm tra giao diện.
     *
     * Khi có Backend chỉ cần thay method này
     * bằng dữ liệu lấy từ API.
     */
    private void khoiTaoDuLieuMau() {

        danhSachTatCaSanPham.clear();


        // ================= AVATAR =================

        danhSachTatCaSanPham.add(
                new SanPhamCuaHang(
                        1,
                        "Ảnh đại diện riêng",
                        "",
                        android.R.drawable.ic_menu_camera,
                        5000,
                        LOAI_AVATAR,
                        true,
                        true
                )
        );


        danhSachTatCaSanPham.add(
                new SanPhamCuaHang(
                        2,
                        "Hai chú mèo",
                        "",
                        android.R.drawable.ic_menu_gallery,
                        0,
                        LOAI_AVATAR,
                        false,
                        false
                )
        );


        danhSachTatCaSanPham.add(
                new SanPhamCuaHang(
                        3,
                        "HNUE chờ tôi nhé",
                        "",
                        android.R.drawable.ic_menu_gallery,
                        3000,
                        LOAI_AVATAR,
                        false,
                        false
                )
        );


        danhSachTatCaSanPham.add(
                new SanPhamCuaHang(
                        4,
                        "TMU chờ tôi nhé",
                        "",
                        android.R.drawable.ic_menu_gallery,
                        3000,
                        LOAI_AVATAR,
                        false,
                        false
                )
        );


        danhSachTatCaSanPham.add(
                new SanPhamCuaHang(
                        5,
                        "Học viện CSND",
                        "",
                        android.R.drawable.ic_menu_gallery,
                        3000,
                        LOAI_AVATAR,
                        false,
                        false
                )
        );


        danhSachTatCaSanPham.add(
                new SanPhamCuaHang(
                        6,
                        "HUST chờ tôi nợ môn nhé",
                        "",
                        android.R.drawable.ic_menu_gallery,
                        3000,
                        LOAI_AVATAR,
                        false,
                        false
                )
        );


        // ================= HÌNH NỀN =================

        danhSachTatCaSanPham.add(
                new SanPhamCuaHang(
                        7,
                        "Bầu trời đêm",
                        "",
                        android.R.drawable.ic_menu_gallery,
                        2500,
                        LOAI_HINH_NEN,
                        false,
                        false
                )
        );


        danhSachTatCaSanPham.add(
                new SanPhamCuaHang(
                        8,
                        "Thành phố",
                        "",
                        android.R.drawable.ic_menu_gallery,
                        3500,
                        LOAI_HINH_NEN,
                        false,
                        false
                )
        );


        danhSachTatCaSanPham.add(
                new SanPhamCuaHang(
                        9,
                        "Không gian",
                        "",
                        android.R.drawable.ic_menu_gallery,
                        4000,
                        LOAI_HINH_NEN,
                        false,
                        false
                )
        );


        // ================= ĐẶC BIỆT =================

        danhSachTatCaSanPham.add(
                new SanPhamCuaHang(
                        10,
                        "Khung hồ sơ đặc biệt",
                        "",
                        android.R.drawable.star_big_on,
                        8000,
                        LOAI_DAC_BIET,
                        false,
                        false
                )
        );


        danhSachTatCaSanPham.add(
                new SanPhamCuaHang(
                        11,
                        "Hiệu ứng đặc biệt",
                        "",
                        android.R.drawable.star_big_on,
                        10000,
                        LOAI_DAC_BIET,
                        false,
                        false
                )
        );
    }
}