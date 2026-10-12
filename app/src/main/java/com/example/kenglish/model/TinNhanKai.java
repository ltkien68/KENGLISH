package com.example.kenglish.model;

public class TinNhanKai {
    private final String noiDung;
    private final boolean nguoiDung;

    public TinNhanKai(String noiDung, boolean nguoiDung) {
        this.noiDung = noiDung;
        this.nguoiDung = nguoiDung;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public boolean isNguoiDung() {
        return nguoiDung;
    }
}
