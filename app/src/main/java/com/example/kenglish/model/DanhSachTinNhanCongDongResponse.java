package com.example.kenglish.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DanhSachTinNhanCongDongResponse {

    @SerializedName("danh_sach")
    private List<BaiVietCongDong> danhSach;

    @SerializedName("co_the_tai_them")
    private boolean coTheTaiThem;

    @SerializedName("tin_nhan_cu_nhat_id")
    private Long tinNhanCuNhatId;

    public List<BaiVietCongDong> getDanhSach() {
        return danhSach;
    }

    public boolean isCoTheTaiThem() {
        return coTheTaiThem;
    }

    public Long getTinNhanCuNhatId() {
        return tinNhanCuNhatId;
    }
}