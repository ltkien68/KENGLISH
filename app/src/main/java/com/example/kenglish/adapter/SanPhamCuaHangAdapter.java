package com.example.kenglish.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kenglish.R;
import com.example.kenglish.model.SanPhamCuaHang;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;


/**
 * Adapter hiển thị danh sách sản phẩm của cửa hàng.
 *
 * Adapter chỉ chịu trách nhiệm hiển thị dữ liệu
 * và gửi sự kiện thao tác về Fragment.
 *
 * Việc gọi Backend/API sau này sẽ được xử lý
 * ở CuaHang thay vì nhét trực tiếp vào Adapter.
 */
public class SanPhamCuaHangAdapter extends BaseAdapter {

    private final Context context;

    private List<SanPhamCuaHang> danhSachSanPham;

    private final SuKienSanPham suKienSanPham;


    /**
     * Interface gửi sự kiện từ Adapter về Fragment.
     */
    public interface SuKienSanPham {

        void khiBamMua(SanPhamCuaHang sanPham);

        void khiBamSuDung(SanPhamCuaHang sanPham);

        void khiBamGo(SanPhamCuaHang sanPham);
    }


    public SanPhamCuaHangAdapter(
            Context context,
            List<SanPhamCuaHang> danhSachSanPham,
            SuKienSanPham suKienSanPham) {

        this.context = context;
        this.danhSachSanPham = danhSachSanPham;
        this.suKienSanPham = suKienSanPham;
    }


    @Override
    public int getCount() {

        return danhSachSanPham.size();
    }


    @Override
    public Object getItem(int position) {

        return danhSachSanPham.get(position);
    }


    @Override
    public long getItemId(int position) {

        return danhSachSanPham.get(position).getId();
    }


    @Override
    public View getView(
            int position,
            View convertView,
            ViewGroup parent) {

        ViewHolder holder;


        // Tạo View mới khi chưa có View để tái sử dụng.
        if (convertView == null) {

            convertView = LayoutInflater
                    .from(context)
                    .inflate(
                            R.layout.cuahang_itemsanpham,
                            parent,
                            false
                    );

            holder = new ViewHolder();

            holder.imgSanPham =
                    convertView.findViewById(
                            R.id.img_san_pham
                    );

            holder.txtTenSanPham =
                    convertView.findViewById(
                            R.id.txt_ten_san_pham
                    );

            holder.txtGiaSanPham =
                    convertView.findViewById(
                            R.id.txt_gia_san_pham
                    );

            holder.btnMua =
                    convertView.findViewById(
                            R.id.btn_mua_san_pham
                    );

            holder.btnSuDung =
                    convertView.findViewById(
                            R.id.btn_su_dung_san_pham
                    );

            holder.btnGo =
                    convertView.findViewById(
                            R.id.btn_go_san_pham
                    );

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }


        SanPhamCuaHang sanPham =
                danhSachSanPham.get(position);


        // Hiển thị thông tin cơ bản.
        holder.txtTenSanPham.setText(
                sanPham.getTenSanPham()
        );

        holder.txtGiaSanPham.setText(
                dinhDangGia(sanPham.getGia())
        );


        // Hiện tại sử dụng drawable local để test.
        if (sanPham.getAnhDrawable() != 0) {

            holder.imgSanPham.setImageResource(
                    sanPham.getAnhDrawable()
            );

        } else {

            holder.imgSanPham.setImageResource(
                    android.R.drawable.ic_menu_gallery
            );
        }


        capNhatTrangThaiNut(
                holder,
                sanPham
        );


        // Sự kiện mua sản phẩm.
        holder.btnMua.setOnClickListener(v -> {

            if (suKienSanPham != null) {

                suKienSanPham.khiBamMua(
                        sanPham
                );
            }
        });


        // Sự kiện sử dụng sản phẩm.
        holder.btnSuDung.setOnClickListener(v -> {

            if (suKienSanPham != null) {

                suKienSanPham.khiBamSuDung(
                        sanPham
                );
            }
        });


        // Sự kiện gỡ sản phẩm đang sử dụng.
        holder.btnGo.setOnClickListener(v -> {

            if (suKienSanPham != null) {

                suKienSanPham.khiBamGo(
                        sanPham
                );
            }
        });


        return convertView;
    }


    /**
     * Hiển thị nút phù hợp với trạng thái sản phẩm.
     */
    private void capNhatTrangThaiNut(
            ViewHolder holder,
            SanPhamCuaHang sanPham) {

        // Mặc định ẩn tất cả nút.
        holder.btnMua.setVisibility(View.GONE);
        holder.btnSuDung.setVisibility(View.GONE);
        holder.btnGo.setVisibility(View.GONE);


        // Chưa sở hữu -> hiện nút Mua.
        if (!sanPham.isDaSoHuu()) {

            holder.btnMua.setVisibility(
                    View.VISIBLE
            );

            return;
        }


        // Đang sử dụng -> hiện nút Gỡ.
        if (sanPham.isDangSuDung()) {

            holder.btnGo.setVisibility(
                    View.VISIBLE
            );

            return;
        }


        // Đã sở hữu nhưng chưa sử dụng.
        holder.btnSuDung.setVisibility(
                View.VISIBLE
        );
    }


    /**
     * Đổi danh sách sản phẩm đang hiển thị.
     */
    public void capNhatDanhSach(
            List<SanPhamCuaHang> danhSachMoi) {

        this.danhSachSanPham = danhSachMoi;

        notifyDataSetChanged();
    }


    /**
     * Định dạng giá theo dạng 5.000, 10.000...
     */
    private String dinhDangGia(int gia) {

        NumberFormat numberFormat =
                NumberFormat.getNumberInstance(
                        new Locale("vi", "VN")
                );

        return numberFormat.format(gia);
    }


    /**
     * Lưu các View của một card để tránh findViewById nhiều lần.
     */
    private static class ViewHolder {

        ImageView imgSanPham;

        TextView txtTenSanPham;
        TextView txtGiaSanPham;

        TextView btnMua;
        TextView btnSuDung;
        TextView btnGo;
    }
}