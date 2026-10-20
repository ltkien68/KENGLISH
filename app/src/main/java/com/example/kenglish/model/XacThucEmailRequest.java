package com.example.kenglish.model;

public class XacThucEmailRequest {

    private String email;
    private String ma_xac_thuc;
    private String loai;

    public XacThucEmailRequest(
            String email,
            String maXacThuc,
            String loai
    ) {
        this.email = email;
        this.ma_xac_thuc = maXacThuc;
        this.loai = loai;
    }

    public String getEmail() {
        return email;
    }

    public String getMa_xac_thuc() {
        return ma_xac_thuc;
    }

    public String getLoai() {
        return loai;
    }
}