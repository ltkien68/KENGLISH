package com.example.kenglish.model;

import java.util.List;

public class LichSuLuyenTapResponse {
    private List<PhienLuyenTap> danh_sach;
    private int trang_hien_tai;
    private int tong_so;
    private int tong_trang;

    public List<PhienLuyenTap> getDanhSach() {
        return danh_sach;
    }

    public int getTrangHienTai() {
        return trang_hien_tai;
    }

    public int getTongSo() {
        return tong_so;
    }

    public int getTongTrang() {
        return tong_trang;
    }

    public static class PhienLuyenTap {
        private long id;
        private String che_do;
        private int tong_so_cau;
        private int so_dung;
        private int so_sai;
        private int phan_tram;
        private String ngay_luyen_tap;

        public long getId() {
            return id;
        }

        public String getCheDo() {
            return che_do;
        }

        public int getTongSoCau() {
            return tong_so_cau;
        }

        public int getSoDung() {
            return so_dung;
        }

        public int getSoSai() {
            return so_sai;
        }

        public int getPhanTram() {
            return phan_tram;
        }

        public String getNgayLuyenTap() {
            return ngay_luyen_tap;
        }
    }
}
