package com.example.kenglish.model;

import com.google.gson.annotations.SerializedName;

public class KaiIntentResponse {
    @SerializedName("thanh_cong")
    private boolean thanhCong;

    @SerializedName("thong_bao")
    private String thongBao;

    @SerializedName("data")
    private DuLieu data;

    public boolean isThanhCong() {
        return thanhCong;
    }

    public String getThongBao() {
        return thongBao;
    }

    public DuLieu getData() {
        return data;
    }

    public static class DuLieu {
        @SerializedName("hanh_dong")
        private String hanhDong;

        @SerializedName("chu_de")
        private String chuDe;

        @SerializedName("so_luong")
        private int soLuong;

        @SerializedName("mo_luyen_tap")
        private boolean moLuyenTap;

        public String getHanhDong() {
            return hanhDong;
        }

        public String getChuDe() {
            return chuDe;
        }

        public int getSoLuong() {
            return soLuong;
        }

        public boolean isMoLuyenTap() {
            return moLuyenTap;
        }
    }
}