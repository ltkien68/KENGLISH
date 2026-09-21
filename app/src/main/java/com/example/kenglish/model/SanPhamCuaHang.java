package com.example.kenglish.model;


/**
 * Model lưu thông tin một sản phẩm trong cửa hàng.
 *
 * Hiện tại có thể sử dụng ảnh drawable để test giao diện.
 * Sau này anhSanPham có thể nhận URL ảnh từ Backend.
 */
public class SanPhamCuaHang {

    private int id;
    private String tenSanPham;

    // URL ảnh lấy từ Backend sau này.
    private String anhSanPham;

    // Drawable local dùng để test trong giai đoạn hiện tại.
    private int anhDrawable;

    private int gia;
    private String loaiSanPham;

    private boolean daSoHuu;
    private boolean dangSuDung;


    public SanPhamCuaHang(
            int id,
            String tenSanPham,
            String anhSanPham,
            int anhDrawable,
            int gia,
            String loaiSanPham,
            boolean daSoHuu,
            boolean dangSuDung) {

        this.id = id;
        this.tenSanPham = tenSanPham;
        this.anhSanPham = anhSanPham;
        this.anhDrawable = anhDrawable;
        this.gia = gia;
        this.loaiSanPham = loaiSanPham;
        this.daSoHuu = daSoHuu;
        this.dangSuDung = dangSuDung;
    }


    public int getId() {
        return id;
    }


    public String getTenSanPham() {
        return tenSanPham;
    }


    public String getAnhSanPham() {
        return anhSanPham;
    }


    public int getAnhDrawable() {
        return anhDrawable;
    }


    public int getGia() {
        return gia;
    }


    public String getLoaiSanPham() {
        return loaiSanPham;
    }


    public boolean isDaSoHuu() {
        return daSoHuu;
    }


    public boolean isDangSuDung() {
        return dangSuDung;
    }


    public void setDaSoHuu(boolean daSoHuu) {
        this.daSoHuu = daSoHuu;
    }


    public void setDangSuDung(boolean dangSuDung) {
        this.dangSuDung = dangSuDung;
    }
}