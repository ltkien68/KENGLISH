package com.example.kenglish;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;


/**
 * Activity chính của ứng dụng Kenglish.
 *
 * Chức năng:
 * - Quản lý thanh điều hướng phía dưới.
 * - Hiển thị các Fragment chính của ứng dụng.
 * - Giữ lại trạng thái Fragment khi chuyển tab.
 */
public class MainActivity extends AppCompatActivity {


    // Thanh điều hướng chính của ứng dụng.
    private BottomNavigationView thanhDieuHuong;


    // Các Fragment chính của ứng dụng.
    private Fragment trangChu;
    private Fragment boTu;
    private Fragment luyenTap;
    private Fragment xepHang;
    private Fragment cuaHang;
    private Fragment caNhan;


    // Fragment đang được hiển thị.
    private Fragment manHinhHienTai;


    /**
     * Khởi tạo Activity và các thành phần giao diện.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        // Ánh xạ các thành phần giao diện.
        anhXa();


        // Khởi tạo hoặc lấy lại các Fragment.
        khoiTaoFragment(savedInstanceState);


        // Thiết lập sự kiện chuyển màn hình.
        thietLapDieuHuong();


        // Thiết lập màu cho tab đang được chọn.
        thietLapMauThanhDieuHuong();
    }


    /**
     * Ánh xạ các View trong giao diện với biến Java.
     */
    private void anhXa() {

        thanhDieuHuong = findViewById(
                R.id.thanh_dieu_huong
        );

        thanhDieuHuong.setItemActiveIndicatorColor(
                ColorStateList.valueOf(
                        Color.parseColor("#37659C")
                )
        );
    }


    /**
     * Khởi tạo 6 Fragment chính.
     *
     * Fragment chỉ được tạo một lần.
     * Khi chuyển tab sẽ sử dụng hide/show thay vì tạo Fragment mới.
     */
    private void khoiTaoFragment(Bundle savedInstanceState) {

        // Activity được tạo lần đầu.
        if (savedInstanceState == null) {

            trangChu = new TrangChu();
            boTu = new BoTu();
            luyenTap = new LuyenTap();
            xepHang = new XepHang();
            cuaHang = new CuaHang();
            caNhan = new CaNhan();


            // Thêm tất cả Fragment vào Activity.
            // Chỉ Trang chủ được hiển thị ban đầu.
            getSupportFragmentManager()
                    .beginTransaction()

                    .add(
                            R.id.khung_noi_dung,
                            caNhan,
                            "CA_NHAN"
                    )
                    .hide(caNhan)

                    .add(
                            R.id.khung_noi_dung,
                            cuaHang,
                            "CUA_HANG"
                    )
                    .hide(cuaHang)

                    .add(
                            R.id.khung_noi_dung,
                            xepHang,
                            "XEP_HANG"
                    )
                    .hide(xepHang)

                    .add(
                            R.id.khung_noi_dung,
                            luyenTap,
                            "LUYEN_TAP"
                    )
                    .hide(luyenTap)

                    .add(
                            R.id.khung_noi_dung,
                            boTu,
                            "BO_TU"
                    )
                    .hide(boTu)

                    .add(
                            R.id.khung_noi_dung,
                            trangChu,
                            "TRANG_CHU"
                    )

                    .commit();


            // Trang chủ là màn hình mặc định.
            manHinhHienTai = trangChu;

            thanhDieuHuong.setSelectedItemId(
                    R.id.menu_trang_chu
            );


            return;
        }


        // Activity được Android tạo lại, ví dụ khi xoay màn hình.
        // Lấy lại các Fragment cũ thay vì tạo Fragment mới.
        trangChu = getSupportFragmentManager()
                .findFragmentByTag("TRANG_CHU");

        boTu = getSupportFragmentManager()
                .findFragmentByTag("BO_TU");

        luyenTap = getSupportFragmentManager()
                .findFragmentByTag("LUYEN_TAP");

        xepHang = getSupportFragmentManager()
                .findFragmentByTag("XEP_HANG");

        cuaHang = getSupportFragmentManager()
                .findFragmentByTag("CUA_HANG");

        caNhan = getSupportFragmentManager()
                .findFragmentByTag("CA_NHAN");


        // Xác định Fragment hiện đang được hiển thị.
        if (trangChu != null && !trangChu.isHidden()) {

            manHinhHienTai = trangChu;

        } else if (boTu != null && !boTu.isHidden()) {

            manHinhHienTai = boTu;

        } else if (luyenTap != null && !luyenTap.isHidden()) {

            manHinhHienTai = luyenTap;

        } else if (xepHang != null && !xepHang.isHidden()) {

            manHinhHienTai = xepHang;

        } else if (cuaHang != null && !cuaHang.isHidden()) {

            manHinhHienTai = cuaHang;

        } else if (caNhan != null && !caNhan.isHidden()) {

            manHinhHienTai = caNhan;
        }
    }


    /**
     * Thiết lập sự kiện cho BottomNavigationView.
     */
    private void thietLapDieuHuong() {

        thanhDieuHuong.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.menu_trang_chu) {
                chuyenManHinh(trangChu);
                return true;
            }

            if (id == R.id.menu_bo_tu) {
                chuyenManHinh(boTu);
                return true;
            }

            if (id == R.id.menu_luyen_tap) {
                chuyenManHinh(luyenTap);
                return true;
            }

            if (id == R.id.menu_xep_hang) {
                chuyenManHinh(xepHang);
                return true;
            }

            if (id == R.id.menu_cua_hang) {
                chuyenManHinh(cuaHang);
                return true;
            }

            if (id == R.id.menu_ca_nhan) {
                chuyenManHinh(caNhan);
                return true;
            }

            return false;
        });
    }


    /**
     * Chuyển màn hình bằng cách ẩn Fragment hiện tại
     * và hiển thị Fragment được chọn.
     *
     * Fragment không bị tạo lại khi chuyển tab.
     *
     * @param manHinhMoi Fragment cần hiển thị.
     */
    private void chuyenManHinh(
            Fragment manHinhMoi) {

        if (manHinhMoi == null) {
            return;
        }


        /*
         * Nếu đang có màn con như ChiTietFolder,
         * đóng toàn bộ màn con trước khi chuyển tab.
         */
        if (getSupportFragmentManager()
                .getBackStackEntryCount() > 0) {

            getSupportFragmentManager()
                    .popBackStackImmediate(
                            null,
                            FragmentManager.POP_BACK_STACK_INCLUSIVE
                    );
        }


        /*
         * Sau khi đóng màn con,
         * nếu người dùng chọn đúng tab hiện tại
         * thì tab đó đã được hiện lại rồi.
         */
        if (manHinhMoi == manHinhHienTai) {
            return;
        }


        getSupportFragmentManager()
                .beginTransaction()
                .hide(manHinhHienTai)
                .show(manHinhMoi)
                .commit();


        manHinhHienTai =
                manHinhMoi;
    }

    private void thietLapMauThanhDieuHuong() {

        int[][] trangThai = new int[][]{
                new int[]{android.R.attr.state_checked},
                new int[]{-android.R.attr.state_checked}
        };


        // Màu icon
        // Active   -> trắng
        // Inactive -> xám
        int[] mauIcon = new int[]{
                Color.WHITE,
                Color.parseColor("#A9A9A9")
        };


        // Màu chữ
        // Active   -> heading
        // Inactive -> chữ phụ
        int[] mauChu = new int[]{
                Color.parseColor("#37659C"),
                Color.parseColor("#A9A9A9")
        };


        thanhDieuHuong.setItemIconTintList(
                new ColorStateList(trangThai, mauIcon)
        );

        thanhDieuHuong.setItemTextColor(
                new ColorStateList(trangThai, mauChu)
        );
    }

}