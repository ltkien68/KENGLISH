package com.example.kenglish.model;
import com.google.gson.annotations.SerializedName;

public class NguoiDungXepHang {
    @SerializedName("id")
    private int id;
    @SerializedName("ten_hien_thi")
    private String tenNguoiDung;
    @SerializedName("anh_dai_dien")
    private String anhDaiDien;
    @SerializedName("so_luot_choi")
    private int luotChoi;
    @SerializedName("current_streak")
    private int streak;

    public NguoiDungXepHang(int id, String tenNguoiDung, String anhDaiDien, int luotChoi, int streak) {
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
