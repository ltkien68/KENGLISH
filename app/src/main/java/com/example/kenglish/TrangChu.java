package com.example.kenglish;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;


/**
 * Fragment hiển thị màn hình Trang chủ.
 */
public class TrangChu extends Fragment {

    /*
     * Khu vực từ đến hạn.
     */
    private LinearLayout layoutTuDenHan;


    /*
     * Ảnh đại diện.
     */
    private TextView txtAnhDaiDien;


    /*
     * Số ngày chuỗi học.
     *
     * Icon lửa được đặt cố định bằng ImageView trong XML,
     * vì vậy không cần ánh xạ icon tại đây.
     */
    private TextView txtChuoiHoc;


    /*
     * Các nút lọc lộ trình.
     */
    private MaterialButton btnTatCa;
    private MaterialButton btnThpt;
    private MaterialButton btnIelts;
    private MaterialButton btnToeic;
    private MaterialButton btnNguoiNoiTieng;
    private MaterialButton btnTheoLevel;
    private MaterialButton btnNguoiDiLam;
    private MaterialButton btnThcsTieuHoc;


    /*
     * Các nhóm lộ trình.
     */
    private View nhomThpt;
    private View nhomIelts;
    private View nhomToeic;
    private View nhomNguoiNoiTieng;
    private View nhomLevel;
    private View nhomChuyenNganh;
    private View nhomThcs;


    /**
     * Khởi tạo giao diện Trang chủ.
     */
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.trang_chu,
                container,
                false
        );


        anhXa(view);

        thietLapBoLoc();

        thietLapSuKienAnhDaiDien();

        thietLapSuKienTuDenHan();

        hienThiChuoiHoc();


        return view;
    }


    /**
     * Ánh xạ các thành phần giao diện.
     */
    private void anhXa(View view) {

        /*
         * Thông tin người dùng.
         */
        txtAnhDaiDien =
                view.findViewById(
                        R.id.txt_anh_dai_dien
                );

        txtChuoiHoc =
                view.findViewById(
                        R.id.txt_chuoi_hoc
                );


        /*
         * Khu vực từ đến hạn.
         */
        layoutTuDenHan =
                view.findViewById(
                        R.id.layout_tudenhan
                );


        /*
         * Các nút bộ lọc.
         */
        btnTatCa =
                view.findViewById(
                        R.id.btn_tat_ca
                );

        btnThpt =
                view.findViewById(
                        R.id.btn_thpt
                );

        btnIelts =
                view.findViewById(
                        R.id.btn_ielts
                );

        btnToeic =
                view.findViewById(
                        R.id.btn_toeic
                );

        btnNguoiNoiTieng =
                view.findViewById(
                        R.id.btn_nguoi_noi_tieng
                );

        btnTheoLevel =
                view.findViewById(
                        R.id.btn_theo_level
                );

        btnNguoiDiLam =
                view.findViewById(
                        R.id.btn_nguoi_di_lam
                );

        btnThcsTieuHoc =
                view.findViewById(
                        R.id.btn_thcs_tieu_hoc
                );


        /*
         * Các nhóm lộ trình.
         */
        nhomThpt =
                view.findViewById(
                        R.id.lotrinh_thpt
                );

        nhomIelts =
                view.findViewById(
                        R.id.lotrinh_ielts
                );

        nhomToeic =
                view.findViewById(
                        R.id.lotrinh_toeic
                );

        nhomNguoiNoiTieng =
                view.findViewById(
                        R.id.lotrinh_nguoinoitieng
                );

        nhomLevel =
                view.findViewById(
                        R.id.lotrinh_level
                );

        nhomChuyenNganh =
                view.findViewById(
                        R.id.lotrinh_nguoidilam
                );

        nhomThcs =
                view.findViewById(
                        R.id.lotrinh_thcs
                );
    }


    /**
     * Hiển thị chuỗi học hiện tại.
     *
     * Hiện tại đang dùng dữ liệu mẫu.
     * Sau này có Backend chỉ cần thay giá trị này
     * bằng dữ liệu của người dùng.
     */
    private void hienThiChuoiHoc() {

        int chuoiHoc = 32;

        txtChuoiHoc.setText(
                String.valueOf(chuoiHoc)
        );
    }


    /**
     * Thiết lập sự kiện cho các nút lọc.
     */
    private void thietLapBoLoc() {

        btnTatCa.setOnClickListener(v -> {

            hienThiTatCaNhom();

            capNhatNutDangChon(
                    btnTatCa
            );
        });


        btnThpt.setOnClickListener(v -> {

            locTheoNhom(
                    nhomThpt
            );

            capNhatNutDangChon(
                    btnThpt
            );
        });


        btnIelts.setOnClickListener(v -> {

            locTheoNhom(
                    nhomIelts
            );

            capNhatNutDangChon(
                    btnIelts
            );
        });


        btnToeic.setOnClickListener(v -> {

            locTheoNhom(
                    nhomToeic
            );

            capNhatNutDangChon(
                    btnToeic
            );
        });


        btnNguoiNoiTieng.setOnClickListener(v -> {

            locTheoNhom(
                    nhomNguoiNoiTieng
            );

            capNhatNutDangChon(
                    btnNguoiNoiTieng
            );
        });


        btnTheoLevel.setOnClickListener(v -> {

            locTheoNhom(
                    nhomLevel
            );

            capNhatNutDangChon(
                    btnTheoLevel
            );
        });


        btnNguoiDiLam.setOnClickListener(v -> {

            locTheoNhom(
                    nhomChuyenNganh
            );

            capNhatNutDangChon(
                    btnNguoiDiLam
            );
        });


        btnThcsTieuHoc.setOnClickListener(v -> {

            locTheoNhom(
                    nhomThcs
            );

            capNhatNutDangChon(
                    btnThcsTieuHoc
            );
        });
    }


    /**
     * Khi nhấn ảnh đại diện
     * sẽ chuyển sang tab Cá nhân.
     */
    private void thietLapSuKienAnhDaiDien() {

        txtAnhDaiDien.setOnClickListener(v -> {

            BottomNavigationView thanhDieuHuong =
                    requireActivity().findViewById(
                            R.id.thanh_dieu_huong
                    );

            thanhDieuHuong.setSelectedItemId(
                    R.id.menu_ca_nhan
            );
        });
    }


    /**
     * Khi nhấn vào khu vực từ đến hạn
     * sẽ chuyển sang tab Luyện tập.
     */
    private void thietLapSuKienTuDenHan() {

        layoutTuDenHan.setOnClickListener(v -> {

            BottomNavigationView thanhDieuHuong =
                    requireActivity().findViewById(
                            R.id.thanh_dieu_huong
                    );

            thanhDieuHuong.setSelectedItemId(
                    R.id.menu_luyen_tap
            );
        });
    }


    /**
     * Chỉ hiển thị nhóm lộ trình được chọn.
     */
    private void locTheoNhom(
            View nhomCanHien) {

        anTatCaNhom();

        nhomCanHien.setVisibility(
                View.VISIBLE
        );
    }


    /**
     * Ẩn toàn bộ các nhóm lộ trình.
     */
    private void anTatCaNhom() {

        nhomThpt.setVisibility(
                View.GONE
        );

        nhomIelts.setVisibility(
                View.GONE
        );

        nhomToeic.setVisibility(
                View.GONE
        );

        nhomNguoiNoiTieng.setVisibility(
                View.GONE
        );

        nhomLevel.setVisibility(
                View.GONE
        );

        nhomChuyenNganh.setVisibility(
                View.GONE
        );

        nhomThcs.setVisibility(
                View.GONE
        );
    }


    /**
     * Hiển thị toàn bộ các nhóm lộ trình.
     */
    private void hienThiTatCaNhom() {

        nhomThpt.setVisibility(
                View.VISIBLE
        );

        nhomIelts.setVisibility(
                View.VISIBLE
        );

        nhomToeic.setVisibility(
                View.VISIBLE
        );

        nhomNguoiNoiTieng.setVisibility(
                View.VISIBLE
        );

        nhomLevel.setVisibility(
                View.VISIBLE
        );

        nhomChuyenNganh.setVisibility(
                View.VISIBLE
        );

        nhomThcs.setVisibility(
                View.VISIBLE
        );
    }


    /**
     * Cập nhật màu của nút lọc đang được chọn.
     */
    private void capNhatNutDangChon(
            MaterialButton nutDangChon) {

        /*
         * Đưa tất cả nút về trạng thái mặc định.
         */
        datMauNutMacDinh(
                btnTatCa
        );

        datMauNutMacDinh(
                btnThpt
        );

        datMauNutMacDinh(
                btnIelts
        );

        datMauNutMacDinh(
                btnToeic
        );

        datMauNutMacDinh(
                btnNguoiNoiTieng
        );

        datMauNutMacDinh(
                btnTheoLevel
        );

        datMauNutMacDinh(
                btnNguoiDiLam
        );

        datMauNutMacDinh(
                btnThcsTieuHoc
        );


        /*
         * Nút đang chọn chuyển sang màu xanh.
         */
        nutDangChon.setBackgroundTintList(
                ColorStateList.valueOf(
                        Color.parseColor(
                                "#23B747"
                        )
                )
        );

        nutDangChon.setStrokeWidth(0);
    }


    /**
     * Đưa nút về trạng thái chưa chọn.
     */
    private void datMauNutMacDinh(
            MaterialButton button) {

        button.setBackgroundTintList(
                ColorStateList.valueOf(
                        Color.parseColor(
                                "#243044"
                        )
                )
        );

        button.setStrokeColor(
                ColorStateList.valueOf(
                        Color.parseColor(
                                "#344155"
                        )
                )
        );

        button.setStrokeWidth(
                dpToPx(1)
        );
    }


    /**
     * Chuyển dp sang pixel
     * để sử dụng cho strokeWidth.
     */
    private int dpToPx(int dp) {

        float density =
                getResources()
                        .getDisplayMetrics()
                        .density;

        return Math.round(
                dp * density
        );
    }
}