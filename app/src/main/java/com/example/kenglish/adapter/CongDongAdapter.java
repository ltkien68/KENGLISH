package com.example.kenglish.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.kenglish.R;
import com.example.kenglish.model.BaiVietCongDong;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class CongDongAdapter extends BaseAdapter {

    private final Context context;
    private final List<BaiVietCongDong> danhSachBaiViet;
    private long nguoiDungHienTaiId = -1;

    public CongDongAdapter(
            Context context,
            List<BaiVietCongDong> danhSachBaiViet) {

        this.context = context;
        this.danhSachBaiViet = danhSachBaiViet;
    }

    // Cập nhật ID người dùng đang đăng nhập
    public void setNguoiDungHienTaiId(long id) {
        nguoiDungHienTaiId = id;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return danhSachBaiViet.size();
    }

    @Override
    public BaiVietCongDong getItem(int position) {
        return danhSachBaiViet.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(
                            R.layout.xephang_itembaiviet,
                            parent,
                            false
                    );

            holder = new ViewHolder();

            holder.khungNguoiKhac = convertView.findViewById(
                    R.id.khung_tin_nhan_nguoi_khac
            );

            holder.khungCuaMinh = convertView.findViewById(
                    R.id.khung_tin_nhan_cua_minh
            );

            holder.imgAnhDaiDien = convertView.findViewById(
                    R.id.img_anh_dai_dien_bai_viet
            );

            holder.txtTenNguoiDang = convertView.findViewById(
                    R.id.txt_ten_nguoi_dang
            );

            holder.txtNoiDungNguoiKhac = convertView.findViewById(
                    R.id.txt_noi_dung_bai_viet
            );

            holder.txtThoiGianNguoiKhac = convertView.findViewById(
                    R.id.txt_thoi_gian_bai_viet
            );

            holder.txtNoiDungCuaMinh = convertView.findViewById(
                    R.id.txt_noi_dung_tin_nhan_cua_minh
            );

            holder.txtThoiGianCuaMinh = convertView.findViewById(
                    R.id.txt_thoi_gian_tin_nhan_cua_minh
            );

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        BaiVietCongDong baiViet = getItem(position);

        boolean laTinNhanCuaMinh =
                baiViet.getNguoiDungId() == nguoiDungHienTaiId;

        if (laTinNhanCuaMinh) {

            holder.khungNguoiKhac.setVisibility(View.GONE);
            holder.khungCuaMinh.setVisibility(View.VISIBLE);

            holder.txtNoiDungCuaMinh.setText(
                    baiViet.getNoiDung()
            );

            holder.txtThoiGianCuaMinh.setText(
                    dinhDangThoiGian(baiViet.getThoiGian())
            );

        } else {

            holder.khungNguoiKhac.setVisibility(View.VISIBLE);
            holder.khungCuaMinh.setVisibility(View.GONE);

            holder.txtTenNguoiDang.setText(
                    baiViet.getTenNguoiDung() == null
                            || baiViet.getTenNguoiDung().trim().isEmpty()
                            ? "Người dùng"
                            : baiViet.getTenNguoiDung()
            );

            holder.txtNoiDungNguoiKhac.setText(
                    baiViet.getNoiDung()
            );

            holder.txtThoiGianNguoiKhac.setText(
                    dinhDangThoiGian(baiViet.getThoiGian())
            );

            // Tải ảnh đại diện
            Glide.with(context)
                    .load(baiViet.getAnhDaiDien())
                    .placeholder(R.drawable.nen_congdong_avatar)
                    .error(R.drawable.nen_congdong_avatar)
                    .centerCrop()
                    .into(holder.imgAnhDaiDien);
        }

        return convertView;
    }

    // Chuyển thời gian từ MySQL thành giờ:phút
    private String dinhDangThoiGian(String thoiGian) {

        if (thoiGian == null || thoiGian.trim().isEmpty()) {
            return "";
        }

        try {
            SimpleDateFormat dinhDangGoc =
                    new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss",
                            Locale.getDefault()
                    );

            dinhDangGoc.setTimeZone(
                    TimeZone.getTimeZone("Asia/Ho_Chi_Minh")
            );

            Date ngay = dinhDangGoc.parse(thoiGian);

            if (ngay == null) {
                return "";
            }

            SimpleDateFormat dinhDangMoi =
                    new SimpleDateFormat(
                            "HH:mm",
                            Locale.getDefault()
                    );

            dinhDangMoi.setTimeZone(
                    TimeZone.getTimeZone("Asia/Ho_Chi_Minh")
            );

            return dinhDangMoi.format(ngay);

        } catch (ParseException e) {
            return thoiGian;
        }
    }

    private static class ViewHolder {

        LinearLayout khungNguoiKhac;
        LinearLayout khungCuaMinh;

        ImageView imgAnhDaiDien;

        TextView txtTenNguoiDang;
        TextView txtNoiDungNguoiKhac;
        TextView txtThoiGianNguoiKhac;

        TextView txtNoiDungCuaMinh;
        TextView txtThoiGianCuaMinh;
    }
}
