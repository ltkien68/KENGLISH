package com.example.kenglish.model;

public class DatLaiMatKhauRequest {
    private String email;
    private String ma_xac_thuc;
    private String mat_khau_moi;

    public DatLaiMatKhauRequest(
            String email,
            String maXacThuc,
            String matKhauMoi
    ) {
        this.email = email;
        this.ma_xac_thuc = maXacThuc;
        this.mat_khau_moi = matKhauMoi;
    }

    public String getEmail() {
        return email;
    }

    public String getMa_xac_thuc() {
        return ma_xac_thuc;
    }

    public String getMat_khau_moi() {
        return mat_khau_moi;
    }
}