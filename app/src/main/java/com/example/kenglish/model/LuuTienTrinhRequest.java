package com.example.kenglish.model;

import java.util.List;

public class LuuTienTrinhRequest {

    private String ma_phien_choi;
    private String che_do;
    private List<KetQuaTuVung> ket_qua;

    public LuuTienTrinhRequest(
            String maPhienChoi,
            String cheDo,
            List<KetQuaTuVung> ketQua) {

        this.ma_phien_choi = maPhienChoi;
        this.che_do = cheDo;
        this.ket_qua = ketQua;
    }

    public String getMa_phien_choi() {
        return ma_phien_choi;
    }

    public String getChe_do() {
        return che_do;
    }

    public List<KetQuaTuVung> getKet_qua() {
        return ket_qua;
    }
}
