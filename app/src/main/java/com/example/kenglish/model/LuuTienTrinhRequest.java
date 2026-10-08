package com.example.kenglish.model;

import java.util.List;

public class LuuTienTrinhRequest {

    private List<KetQuaTuVung> ket_qua;

    public LuuTienTrinhRequest(
            List<KetQuaTuVung> ketQua) {

        this.ket_qua = ketQua;
    }

    public List<KetQuaTuVung> getKet_qua() {
        return ket_qua;
    }
}
