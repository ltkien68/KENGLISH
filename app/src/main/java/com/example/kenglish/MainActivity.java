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
 */
public class MainActivity extends AppCompatActivity {


    // Thanh điều hướng chính của ứng dụng.
    private BottomNavigationView thanhDieuHuong;


    /**
     * Khởi tạo Activity và các thành phần giao diện.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Ánh xạ thanh điều hướng từ XML.
        anhXa();


        // Thiết lập sự kiện chuyển màn hình.
        thietLapDieuHuong();


        // Chỉ hiển thị Trang chủ khi Activity được tạo lần đầu.
        if (savedInstanceState == null) {
            chuyenManHinh(new TrangChu());
        }
    }


    /**
     * Ánh xạ các View trong giao diện với biến Java.
     */
    private void anhXa() {

        thanhDieuHuong = findViewById(R.id.thanh_dieu_huong);

    }

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


        thanhDieuHuong.setItemIconTintList(danhSachMau);
        thanhDieuHuong.setItemTextColor(danhSachMau);
    }


    /**
     * Thiết lập sự kiện cho BottomNavigationView.
     */
    private void thietLapDieuHuong() {

        thanhDieuHuong.setOnItemSelectedListener(item -> {

            int id = item.getItemId();


            // Chuyển sang màn hình Trang chủ.
            if (id == R.id.menu_trang_chu) {

                chuyenManHinh(new TrangChu());

                doiMauThanhDieuHuong(
                        Color.parseColor("#29B6F6")
                );

                return true;

            }


            // Chuyển sang màn hình Bộ từ.
            if (id == R.id.menu_bo_tu) {

                chuyenManHinh(new BoTu());

                doiMauThanhDieuHuong(
                        Color.parseColor("#35C759")
                );

                return true;

            }


            // Chuyển sang màn hình Luyện tập.
            if (id == R.id.menu_luyen_tap) {

                chuyenManHinh(new LuyenTap());

                doiMauThanhDieuHuong(
                        Color.parseColor("#FF9F0A")
                );

                return true;

            }


            // Chuyển sang màn hình Xếp hạng.
            if (id == R.id.menu_xep_hang) {

                chuyenManHinh(new XepHang());

                doiMauThanhDieuHuong(
                        Color.parseColor("#FFD43B")
                );

                return true;

            }


            // Chuyển sang màn hình Cửa hàng.
            if (id == R.id.menu_cua_hang) {

                chuyenManHinh(new CuaHang());

                doiMauThanhDieuHuong(
                        Color.parseColor("#AF52DE")
                );

                return true;

            }


            // Chuyển sang màn hình Cá nhân.
            if (id == R.id.menu_ca_nhan) {

                chuyenManHinh(new CaNhan());

                doiMauThanhDieuHuong(
                        Color.parseColor("#FF5C8A")
                );

                return true;

            }


            return false;

        });

    }


    /**
     * Thay Fragment hiện tại bằng Fragment được truyền vào.
     *
     * @param manHinh Fragment cần hiển thị.
     */
    private void chuyenManHinh(Fragment manHinh) {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.khung_noi_dung, manHinh)
                .commit();

    }


}