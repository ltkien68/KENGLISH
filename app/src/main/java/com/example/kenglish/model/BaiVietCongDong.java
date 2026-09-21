package com.example.kenglish.model;


/**
 * Model lưu thông tin một bài viết trong Cộng đồng.
 */
public class BaiVietCongDong {

    private int id;
    private String tenNguoiDung;
    private String anhDaiDien;
    private String noiDung;
    private String thoiGian;


    public BaiVietCongDong(
            int id,
            String tenNguoiDung,
            String anhDaiDien,
            String noiDung,
            String thoiGian) {

        this.id = id;
        this.tenNguoiDung = tenNguoiDung;
        this.anhDaiDien = anhDaiDien;
        this.noiDung = noiDung;
        this.thoiGian = thoiGian;
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


    public String getNoiDung() {
        return noiDung;
    }


    public String getThoiGian() {
        return thoiGian;
    }
}