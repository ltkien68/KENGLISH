package com.example.kenglish.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.kenglish.R;
import com.example.kenglish.model.TuVung;

import java.util.List;


public class TuVungAdapter
        extends ArrayAdapter<TuVung> {


    /*
     * =========================================
     * BIẾN
     * =========================================
     */

    private final Context context;

    private final List<TuVung> danhSachTu;


    /*
     * =========================================
     * CONSTRUCTOR
     * =========================================
     */

    public TuVungAdapter(
            @NonNull Context context,
            @NonNull List<TuVung> danhSachTu) {

        super(
                context,
                R.layout.botu_itemtuvung,
                danhSachTu
        );

        this.context =
                context;

        this.danhSachTu =
                danhSachTu;
    }


    /*
     * =========================================
     * TẠO / CẬP NHẬT ITEM
     * =========================================
     */

    @NonNull
    @Override
    public View getView(
            int position,
            @Nullable View convertView,
            @NonNull ViewGroup parent) {


        /*
         * Nếu chưa có View để tái sử dụng
         * thì inflate layout item.
         */
        if (convertView == null) {

            convertView =
                    LayoutInflater
                            .from(context)
                            .inflate(
                                    R.layout.botu_itemtuvung,
                                    parent,
                                    false
                            );
        }


        /*
         * Lấy từ vựng tại vị trí hiện tại.
         */
        TuVung tuVung =
                danhSachTu.get(position);


        /*
         * =====================================
         * ÁNH XẠ
         * =====================================
         */

        TextView txtTuGoc =
                convertView.findViewById(
                        R.id.txt_tu_goc
                );


        TextView txtPhienAmLoaiTu =
                convertView.findViewById(
                        R.id.txt_phien_am_loai_tu
                );


        TextView txtNghiaTiengViet =
                convertView.findViewById(
                        R.id.txt_nghia_tieng_viet
                );


        TextView txtCauViDu =
                convertView.findViewById(
                        R.id.txt_cau_vi_du
                );


        TextView txtTrangThai =
                convertView.findViewById(
                        R.id.txt_trang_thai
                );


        /*
         * =====================================
         * TỪ GỐC
         * =====================================
         */

        txtTuGoc.setText(
                tuVung.getTu_goc()
        );


        /*
         * =====================================
         * PHIÊN ÂM + LOẠI TỪ
         * =====================================
         */

        String phienAm =
                tuVung.getPhien_am();

        String loaiTu =
                tuVung.getLoai_tu();


        if (phienAm == null) {
            phienAm = "";
        }

        if (loaiTu == null) {
            loaiTu = "";
        }


        String thongTinPhu;


        if (!phienAm.isEmpty()
                && !loaiTu.isEmpty()) {

            thongTinPhu =
                    phienAm
                            + "  •  "
                            + loaiTu;

        } else if (!phienAm.isEmpty()) {

            thongTinPhu =
                    phienAm;

        } else {

            thongTinPhu =
                    loaiTu;
        }


        txtPhienAmLoaiTu.setText(
                thongTinPhu
        );


        /*
         * =====================================
         * NGHĨA TIẾNG VIỆT
         * =====================================
         */

        txtNghiaTiengViet.setText(
                tuVung.getNghia_tieng_viet()
        );


        /*
         * =====================================
         * CÂU VÍ DỤ
         * =====================================
         */

        String cauViDu =
                tuVung.getCau_vi_du();


        if (cauViDu == null
                || cauViDu.trim().isEmpty()) {

            /*
             * Không có ví dụ thì ẩn luôn,
             * không để khoảng trắng thừa.
             */
            txtCauViDu.setVisibility(
                    View.GONE
            );

        } else {

            txtCauViDu.setVisibility(
                    View.VISIBLE
            );

            txtCauViDu.setText(
                    cauViDu
            );
        }


        /*
         * =====================================
         * TRẠNG THÁI
         * =====================================
         */

        if (tuVung.isDa_thuoc()) {

            txtTrangThai.setText(
                    "Đã thuộc"
            );

        } else {

            txtTrangThai.setText(
                    "Chưa thuộc"
            );
        }


        return convertView;
    }
}