package com.example.kenglish.model;
import java.io.Serializable;

public class TuVung implements Serializable {

    private int id;

    private int bo_tu_id;

    private String tu_goc;

    private String phien_am;

    private String loai_tu;

    private String nghia_tieng_viet;

    private String cau_vi_du;

    private String tu_dong_nghia;

    private String tu_trai_nghia;

    private int da_thuoc;

    private String ngay_tao;


    public int getId() {
        return id;
    }


    public int getBo_tu_id() {
        return bo_tu_id;
    }


    public String getTu_goc() {
        return tu_goc;
    }


    public String getPhien_am() {
        return phien_am;
    }


    public String getLoai_tu() {
        return loai_tu;
    }


    public String getNghia_tieng_viet() {
        return nghia_tieng_viet;
    }


    public String getCau_vi_du() {
        return cau_vi_du;
    }


    public String getTu_dong_nghia() {
        return tu_dong_nghia;
    }


    public String getTu_trai_nghia() {
        return tu_trai_nghia;
    }


    public int getDa_thuoc() {
        return da_thuoc;
    }


    public boolean isDa_thuoc() {
        return da_thuoc == 1;
    }


    public String getNgay_tao() {
        return ngay_tao;
    }

    public void setDa_thuoc(int da_thuoc) {
        this.da_thuoc = da_thuoc;
    }
}