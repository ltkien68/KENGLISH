package com.example.kenglish.model;

public class DangKyRequest {

    private String ten_hien_thi;
    private String email;
    private String mat_khau;

    public DangKyRequest(
            String ten_hien_thi,
            String email,
            String mat_khau
    ) {
        this.ten_hien_thi = ten_hien_thi;
        this.email = email;
        this.mat_khau = mat_khau;
    }
}