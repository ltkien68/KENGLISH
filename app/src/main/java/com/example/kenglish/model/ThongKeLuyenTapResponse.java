package com.example.kenglish.model;

import java.util.List;

public class ThongKeLuyenTapResponse {
    private int so_lan_luyen_tap;
    private int tong_so_cau;
    private int tong_so_dung;
    private int tong_so_sai;
    private int do_chinh_xac_tb;
    private int so_luot_choi;
    private List<ThongKeTheoCheDo> theo_che_do;

    public int getSoLanLuyenTap() {
        return so_lan_luyen_tap;
    }

    public int getTongSoCau() {
        return tong_so_cau;
    }

    public int getTongSoDung() {
        return tong_so_dung;
    }

    public int getTongSoSai() {
        return tong_so_sai;
    }

    public int getDoChinhXacTb() {
        return do_chinh_xac_tb;
    }

    public int getSoLuotChoi() {
        return so_luot_choi;
    }

    public List<ThongKeTheoCheDo> getTheoCheDo() {
        return theo_che_do;
    }

    public static class ThongKeTheoCheDo {
        private String che_do;
        private int so_lan;

        public String getCheDo() {
            return che_do;
        }

        public int getSoLan() {
            return so_lan;
        }
    }
}
