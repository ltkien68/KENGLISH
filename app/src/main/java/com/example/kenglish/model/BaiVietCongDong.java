package com.example.kenglish.model;

import com.google.gson.annotations.SerializedName;

public class BaiVietCongDong {

    private long id;

    @SerializedName("nguoi_dung_id")
    private int nguoiDungId;

    @SerializedName("ten_hien_thi")
    private String tenNguoiDung;

    @SerializedName("avatar_url")
    private String anhDaiDien;

    @SerializedName("noi_dung")
    private String noiDung;

    @SerializedName("tao_luc")
    private String thoiGian;

    public long getId() {
        return id;
    }

    public int getNguoiDungId() {
        return nguoiDungId;
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
