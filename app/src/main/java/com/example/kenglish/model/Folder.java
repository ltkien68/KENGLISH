package com.example.kenglish.model;

public class Folder {

    private int id;

    private String ten_folder;

    private String ngay_tao;

    private int so_bo_tu;


    public int getId() {
        return id;
    }


    public String getTenFolder() {
        return ten_folder;
    }


    public String getNgayTao() {
        return ngay_tao;
    }


    public int getSoBoTu() {
        return so_bo_tu;
    }

    public void tangSoBoTu() {
        so_bo_tu++;
    }
}