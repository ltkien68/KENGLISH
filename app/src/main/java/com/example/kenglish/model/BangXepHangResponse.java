package com.example.kenglish.model;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class BangXepHangResponse {
    @SerializedName("loai")
    private String loai;
    @SerializedName("danh_sach")
    private List<NguoiDungXepHang> danhSach;

    public String getLoai() {
        return loai;
    }

    public List<NguoiDungXepHang> getDanhSach() {
        return danhSach;
    }
}
