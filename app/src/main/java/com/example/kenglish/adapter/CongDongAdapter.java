package com.example.kenglish.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kenglish.R;
import com.example.kenglish.model.BaiVietCongDong;

import java.util.List;


/**
 * Adapter hiển thị các bài viết trong Cộng đồng.
 */
public class CongDongAdapter extends BaseAdapter {

    private final Context context;

    private List<BaiVietCongDong> danhSach;


    public CongDongAdapter(
            Context context,
            List<BaiVietCongDong> danhSach) {

        this.context = context;
        this.danhSach = danhSach;
    }


    @Override
    public int getCount() {
        return danhSach.size();
    }


    @Override
    public Object getItem(int position) {
        return danhSach.get(position);
    }


    @Override
    public long getItemId(int position) {
        return danhSach.get(position).getId();
    }


    @Override
    public View getView(
            int position,
            View convertView,
            ViewGroup parent) {

        ViewHolder holder;


        if (convertView == null) {

            convertView = LayoutInflater
                    .from(context)
                    .inflate(
                            R.layout.xephang_itembaiviet,
                            parent,
                            false
                    );

            holder = new ViewHolder();


            holder.imgAnhDaiDien =
                    convertView.findViewById(
                            R.id.img_anh_dai_dien_bai_viet
                    );

            holder.txtTen =
                    convertView.findViewById(
                            R.id.txt_ten_nguoi_dang
                    );

            holder.txtThoiGian =
                    convertView.findViewById(
                            R.id.txt_thoi_gian_bai_viet
                    );

            holder.txtNoiDung =
                    convertView.findViewById(
                            R.id.txt_noi_dung_bai_viet
                    );


            convertView.setTag(holder);

        } else {

            holder = (ViewHolder)
                    convertView.getTag();
        }


        BaiVietCongDong baiViet =
                danhSach.get(position);


        holder.txtTen.setText(
                baiViet.getTenNguoiDung()
        );

        holder.txtThoiGian.setText(
                baiViet.getThoiGian()
        );

        holder.txtNoiDung.setText(
                baiViet.getNoiDung()
        );


        // Avatar mặc định trong giai đoạn test.
        holder.imgAnhDaiDien.setImageResource(
                android.R.drawable.sym_def_app_icon
        );


        return convertView;
    }


    /**
     * Cập nhật dữ liệu khi Backend trả danh sách mới.
     */
    public void capNhatDanhSach(
            List<BaiVietCongDong> danhSachMoi) {

        this.danhSach =
                danhSachMoi;

        notifyDataSetChanged();
    }


    private static class ViewHolder {

        ImageView imgAnhDaiDien;

        TextView txtTen;
        TextView txtThoiGian;
        TextView txtNoiDung;
    }
}