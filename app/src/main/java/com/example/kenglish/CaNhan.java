package com.example.kenglish;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


/**
 * Fragment hiển thị thông tin cá nhân và cài đặt người dùng.
 */
public class CaNhan extends Fragment {


    /**
     * Khởi tạo giao diện của màn hình Cá nhân.
     */
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.ca_nhan,
                container,
                false
        );

    }

    private void anhXa() {
        // findViewById(...)
    }

    private void xuLySuKien() {
        // Gọi các hàm xử lý
    }

    private void xuLyNhacHoc() {
        // Mở tab nhắc học
    }

    private void xuLyCongDong() {
        // Mở link Zalo
    }

    private void xuLyLienHeHoTro() {
        // Mở trang hỗ trợ
    }

    private void xuLyChinhSach() {
        // Mở chính sách
    }

    private void xuLyDieuKhoan() {
        // Mở điều khoản
    }

    private void xuLyMangXaHoi() {
        // Facebook / Instagram
    }

    private void xuLyDangXuat() {
        // Đăng xuất
    }

    private void xuLyXoaTaiKhoan() {
        // Xác nhận xóa tài khoản
    }


}