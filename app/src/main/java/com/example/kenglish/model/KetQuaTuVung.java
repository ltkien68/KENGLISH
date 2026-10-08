package com.example.kenglish.model;

import java.io.Serializable;

public class KetQuaTuVung implements Serializable {

    private int tu_vung_id;
    private String tu_goc;
    private String nghia_tieng_viet;
    private int da_thuoc;

    public KetQuaTuVung(
            int tuVungId,
            String tuGoc,
            String nghiaTiengViet,
            int daThuoc) {

        this.tu_vung_id = tuVungId;
        this.tu_goc = tuGoc;
        this.nghia_tieng_viet = nghiaTiengViet;
        this.da_thuoc = daThuoc;
    }

    public int getTu_vung_id() {
        return tu_vung_id;
    }

    public String getTu_goc() {
        return tu_goc;
    }

    public String getNghia_tieng_viet() {
        return nghia_tieng_viet;
    }

    public int getDa_thuoc() {
        return da_thuoc;
    }
}
