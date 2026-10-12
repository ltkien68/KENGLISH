package com.example.kenglish.model;

import com.google.gson.annotations.SerializedName;

public class KaiCreateVocabularyResponse {
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
        @SerializedName("bo_tu_id")
        private int boTuId;

        @SerializedName("ten_bo_tu")
        private String tenBoTu;

        @SerializedName("so_tu")
        private int soTu;

        @SerializedName("hanh_dong_tiep_theo")
        private String hanhDongTiepTheo;

        public int getBoTuId() {
            return boTuId;
        }

        public String getTenBoTu() {
            return tenBoTu;
        }

        public int getSoTu() {
            return soTu;
        }

        public String getHanhDongTiepTheo() {
            return hanhDongTiepTheo;
        }
    }
}