package com.example.kenglish;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

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
        capNhatMauTabHienTai();
    }


    /**
     * Ánh xạ các View trong giao diện với biến Java.
     */
    private void anhXa() {

        thanhDieuHuong = findViewById(
                R.id.thanh_dieu_huong
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


            // Chuyển sang màn hình Trang chủ.
            if (id == R.id.menu_trang_chu) {

                chuyenManHinh(trangChu);

                doiMauThanhDieuHuong(
                        Color.parseColor("#29B6F6")
                );

                return true;
            }


            // Chuyển sang màn hình Bộ từ.
            if (id == R.id.menu_bo_tu) {

                chuyenManHinh(boTu);

                doiMauThanhDieuHuong(
                        Color.parseColor("#35C759")
                );

                return true;
            }


            // Chuyển sang màn hình Luyện tập.
            if (id == R.id.menu_luyen_tap) {

                chuyenManHinh(luyenTap);

                doiMauThanhDieuHuong(
                        Color.parseColor("#FF9F0A")
                );

                return true;
            }


            // Chuyển sang màn hình Xếp hạng.
            if (id == R.id.menu_xep_hang) {

                chuyenManHinh(xepHang);

                doiMauThanhDieuHuong(
                        Color.parseColor("#FFD43B")
                );

                return true;
            }


            // Chuyển sang màn hình Cửa hàng.
            if (id == R.id.menu_cua_hang) {

                chuyenManHinh(cuaHang);

                doiMauThanhDieuHuong(
                        Color.parseColor("#AF52DE")
                );

                return true;
            }


            // Chuyển sang màn hình Cá nhân.
            if (id == R.id.menu_ca_nhan) {

                chuyenManHinh(caNhan);

                doiMauThanhDieuHuong(
                        Color.parseColor("#FF5C8A")
                );

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
    private void chuyenManHinh(Fragment manHinhMoi) {

        // Không làm gì nếu đang đứng tại chính màn hình đó.
        if (manHinhMoi == null ||
                manHinhMoi == manHinhHienTai) {

            return;
        }


        getSupportFragmentManager()
                .beginTransaction()
                .hide(manHinhHienTai)
                .show(manHinhMoi)
                .commit();


        manHinhHienTai = manHinhMoi;
    }


    /**
     * Đổi màu icon và chữ của tab đang được chọn.
     *
     * @param mauDuocChon màu của tab đang chọn.
     */
    private void doiMauThanhDieuHuong(int mauDuocChon) {

        int[][] trangThai = new int[][]{
                new int[]{android.R.attr.state_checked},
                new int[]{-android.R.attr.state_checked}
        };


        int[] mauSac = new int[]{
                mauDuocChon,
                Color.parseColor("#AAB4C5")
        };


        ColorStateList danhSachMau = new ColorStateList(
                trangThai,
                mauSac
        );


        thanhDieuHuong.setItemIconTintList(
                danhSachMau
        );

        thanhDieuHuong.setItemTextColor(
                danhSachMau
        );
    }


    /**
     * Thiết lập lại màu tab hiện tại khi Activity được tạo.
     */
    private void capNhatMauTabHienTai() {

        int id = thanhDieuHuong.getSelectedItemId();


        if (id == R.id.menu_bo_tu) {

            doiMauThanhDieuHuong(
                    Color.parseColor("#35C759")
            );

        } else if (id == R.id.menu_luyen_tap) {

            doiMauThanhDieuHuong(
                    Color.parseColor("#FF9F0A")
            );

        } else if (id == R.id.menu_xep_hang) {

            doiMauThanhDieuHuong(
                    Color.parseColor("#FFD43B")
            );

        } else if (id == R.id.menu_cua_hang) {

            doiMauThanhDieuHuong(
                    Color.parseColor("#AF52DE")
            );

        } else if (id == R.id.menu_ca_nhan) {

            doiMauThanhDieuHuong(
                    Color.parseColor("#FF5C8A")
            );

        } else {

            doiMauThanhDieuHuong(
                    Color.parseColor("#29B6F6")
            );
        }
    }
}