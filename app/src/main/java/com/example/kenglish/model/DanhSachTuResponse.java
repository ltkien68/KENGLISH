package com.example.kenglish.model;

import java.util.List;

public class DanhSachTuResponse {

    private BoTuModel bo_tu;

    private int tong_tu;

    private List<TuVung> tu_vung;


    public BoTuModel getBoTu() {
        return bo_tu;
    }


    public int getTongTu() {
        return tong_tu;
    }


    public List<TuVung> getTuVung() {
        return tu_vung;
    }
}