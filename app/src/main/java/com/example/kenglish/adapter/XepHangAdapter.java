package com.example.kenglish.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kenglish.R;
import com.example.kenglish.model.NguoiDungXepHang;

import java.util.List;


/**
 * Adapter hiển thị người dùng trong bảng xếp hạng.
 *
 * Adapter có thể hiển thị:
 * - Xếp hạng theo lượt chơi.
 * - Xếp hạng theo streak.
 */
public class XepHangAdapter extends BaseAdapter {

    public static final String LOAI_LUOT_CHOI =
            "luot_choi";

    public static final String LOAI_STREAK =
            "streak";


    private final Context context;

    private List<NguoiDungXepHang> danhSach;

    private String loaiXepHang;


    public XepHangAdapter(
            Context context,
            List<NguoiDungXepHang> danhSach,
            String loaiXepHang) {

        this.context = context;
        this.danhSach = danhSach;
        this.loaiXepHang = loaiXepHang;
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
                            R.layout.xephang_itemnguoidung,
                            parent,
                            false
                    );

            holder = new ViewHolder();


            holder.txtHang =
                    convertView.findViewById(
                            R.id.txt_hang
                    );

            holder.imgAnhDaiDien =
                    convertView.findViewById(
                            R.id.img_anh_dai_dien
                    );

            holder.txtTenNguoiDung =
                    convertView.findViewById(
                            R.id.txt_ten_nguoi_dung
                    );

            holder.txtDiemXepHang =
                    convertView.findViewById(
                            R.id.txt_diem_xep_hang
                    );


            convertView.setTag(holder);

        } else {

            holder = (ViewHolder)
                    convertView.getTag();
        }


        NguoiDungXepHang nguoiDung =
                danhSach.get(position);


        // Hạng bắt đầu từ 1.
        int hang = position + 1;


        hienThiHang(
                holder.txtHang,
                hang
        );


        holder.txtTenNguoiDung.setText(
                nguoiDung.getTenNguoiDung()
        );


        /*
         * Hiện tại dùng avatar mặc định.
         *
         * Sau này khi Backend trả URL ảnh,
         * có thể dùng Glide để load nguoiDung.getAnhDaiDien().
         */
        holder.imgAnhDaiDien.setImageResource(
                android.R.drawable.sym_def_app_icon
        );


        // Hiển thị giá trị tùy bảng đang chọn.
        if (LOAI_STREAK.equals(loaiXepHang)) {

            holder.txtDiemXepHang.setText(
                    "🔥 " + nguoiDung.getStreak()
            );

        } else {

            holder.txtDiemXepHang.setText(
                    nguoiDung.getLuotChoi() + " ⚡"
            );
        }


        return convertView;
    }


    /**
     * Hiển thị hạng.
     * Top 3 dùng biểu tượng huy chương để nổi bật.
     */
    private void hienThiHang(
            TextView txtHang,
            int hang) {

        if (hang == 1) {

            txtHang.setText("🥇");

        } else if (hang == 2) {

            txtHang.setText("🥈");

        } else if (hang == 3) {

            txtHang.setText("🥉");

        } else {

            txtHang.setText(
                    String.valueOf(hang)
            );
        }
    }


    /**
     * Chuyển loại bảng xếp hạng.
     */
    public void setLoaiXepHang(
            String loaiXepHang) {

        this.loaiXepHang =
                loaiXepHang;

        notifyDataSetChanged();
    }


    /**
     * Cập nhật dữ liệu mới.
     *
     * Sau này Backend trả danh sách mới
     * chỉ cần gọi method này.
     */
    public void capNhatDanhSach(
            List<NguoiDungXepHang> danhSachMoi) {

        this.danhSach =
                danhSachMoi;

        notifyDataSetChanged();
    }


    private static class ViewHolder {

        TextView txtHang;

        ImageView imgAnhDaiDien;

        TextView txtTenNguoiDung;

        TextView txtDiemXepHang;
    }
}