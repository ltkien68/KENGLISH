package com.example.kenglish.model;


/**
 * Model lưu dữ liệu một người dùng trong bảng xếp hạng.
 *
 * Hiện tại dữ liệu được tạo mẫu trong ứng dụng.
 * Sau này có thể map trực tiếp dữ liệu JSON từ Backend vào model này.
 */
public class NguoiDungXepHang {

    private int id;
    private String tenNguoiDung;
    private String anhDaiDien;

    private int luotChoi;
    private int streak;


    public NguoiDungXepHang(
            int id,
            String tenNguoiDung,
            String anhDaiDien,
            int luotChoi,
            int streak) {

        this.id = id;
        this.tenNguoiDung = tenNguoiDung;
        this.anhDaiDien = anhDaiDien;
        this.luotChoi = luotChoi;
        this.streak = streak;
    }


    public int getId() {
        return id;
    }


    public String getTenNguoiDung() {
        return tenNguoiDung;
    }


    public String getAnhDaiDien() {
        return anhDaiDien;
    }


    public int getLuotChoi() {
        return luotChoi;
    }


    public int getStreak() {
        return streak;
    }
}