package com.example.kenglish.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kenglish.R;
import com.example.kenglish.model.NguoiDungXepHang;

import java.util.List;

public class XepHangAdapter extends BaseAdapter {

    public static final String LOAI_LUOT_CHOI = "luot_choi";
    public static final String LOAI_STREAK = "streak";

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
        return danhSach == null ? 0 : danhSach.size();
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

            convertView = LayoutInflater.from(context)
                    .inflate(
                            R.layout.xephang_itemnguoidung,
                            parent,
                            false
                    );

            holder = new ViewHolder();

            holder.cardXepHang =
                    convertView.findViewById(
                            R.id.card_xep_hang
                    );

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

            holder.imgIconDiem =
                    convertView.findViewById(
                            R.id.img_icon_diem
                    );

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        NguoiDungXepHang nguoiDung =
                danhSach.get(position);

        // Danh sách đã được sắp xếp theo bảng tương ứng.
        int hang = position + 1;

        // Hiển thị huy chương hoặc số thứ hạng.
        hienThiHang(holder.txtHang, hang);

        // Đổi nền theo Top 1, 2, 3.
        capNhatMauXepHang(holder.cardXepHang, hang);

        // Tên người dùng.
        holder.txtTenNguoiDung.setText(
                nguoiDung.getTenNguoiDung()
        );

        // Avatar mặc định.
        holder.imgAnhDaiDien.setImageResource(
                android.R.drawable.ic_menu_myplaces
        );

        holder.imgAnhDaiDien.setColorFilter(
                Color.parseColor("#087CFF")
        );

        // Đổi điểm và icon theo loại bảng.
        if (LOAI_STREAK.equals(loaiXepHang)) {

            holder.txtDiemXepHang.setText(
                    String.valueOf(nguoiDung.getStreak())
            );

            holder.imgIconDiem.setImageResource(
                    R.drawable.ic_streak
            );

            holder.imgIconDiem.setColorFilter(
                    Color.parseColor("#FF7043")
            );

        } else {

            holder.txtDiemXepHang.setText(
                    String.valueOf(nguoiDung.getLuotChoi())
            );

            holder.imgIconDiem.setImageResource(
                    R.drawable.ic_tiaset
            );

            holder.imgIconDiem.setColorFilter(
                    Color.parseColor("#FFD43B")
            );
        }

        holder.txtDiemXepHang.setTextColor(
                Color.parseColor("#087CFF")
        );

        return convertView;
    }

    /**
     * Hiển thị huy chương cho Top 3.
     * Các hạng còn lại hiển thị số.
     */
    private void hienThiHang(TextView txtHang, int hang) {

        if (hang == 1) {

            txtHang.setText("🥇");

        } else if (hang == 2) {

            txtHang.setText("🥈");

        } else if (hang == 3) {

            txtHang.setText("🥉");

        } else {

            txtHang.setText(String.valueOf(hang));
        }
    }

    /**
     * Đổi màu nền và viền card theo thứ hạng.
     *
     * Luôn được gọi trong getView() để tránh
     * ListView tái sử dụng màu của item trước.
     */
    private void capNhatMauXepHang(
            LinearLayout card,
            int hang) {

        if (card == null) {
            return;
        }

        String mauNen;
        String mauVien;

        switch (hang) {

            case 1:
                mauNen = "#FFF5E5";
                mauVien = "#F4D9A6";
                break;

            case 2:
                mauNen = "#F0F5FA";
                mauVien = "#DCE5EF";
                break;

            case 3:
                mauNen = "#FFF0E9";
                mauVien = "#F3D9CC";
                break;

            default:
                mauNen = "#FFFFFF";
                mauVien = "#E5EDF5";
                break;
        }

        GradientDrawable background =
                new GradientDrawable();

        background.setShape(
                GradientDrawable.RECTANGLE
        );

        background.setColor(
                Color.parseColor(mauNen)
        );

        background.setCornerRadius(
                dp(14)
        );

        background.setStroke(
                dp(1),
                Color.parseColor(mauVien)
        );

        card.setBackground(background);
    }

    /**
     * Chuyển dp sang px.
     */
    private int dp(float giaTri) {

        return Math.round(
                giaTri *
                        context.getResources()
                                .getDisplayMetrics()
                                .density
        );
    }

    /**
     * Chuyển giữa bảng lượt chơi và streak.
     */
    public void setLoaiXepHang(String loaiXepHang) {

        this.loaiXepHang = loaiXepHang;

        notifyDataSetChanged();
    }

    /**
     * Cập nhật danh sách từ API.
     */
    public void capNhatDanhSach(
            List<NguoiDungXepHang> danhSachMoi) {

        this.danhSach = danhSachMoi;

        notifyDataSetChanged();
    }

    /**
     * Lưu các View để tối ưu ListView.
     */
    private static class ViewHolder {

        LinearLayout cardXepHang;

        TextView txtHang;

        ImageView imgAnhDaiDien;

        TextView txtTenNguoiDung;

        TextView txtDiemXepHang;

        ImageView imgIconDiem;
    }
}
