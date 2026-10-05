package com.example.kenglish.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kenglish.R;
import com.example.kenglish.model.BoTuModel;

import java.util.List;


public class BoTuFolderAdapter
        extends RecyclerView.Adapter<BoTuFolderAdapter.BoTuViewHolder> {


    private final List<BoTuModel> danhSachBoTu;

    private final OnBoTuClickListener listener;


    /**
     * Constructor Adapter.
     *
     * @param danhSachBoTu danh sách bộ từ trong folder
     * @param listener xử lý sự kiện khi người dùng thao tác với bộ từ
     */
    public BoTuFolderAdapter(
            List<BoTuModel> danhSachBoTu,
            OnBoTuClickListener listener) {

        this.danhSachBoTu =
                danhSachBoTu;

        this.listener =
                listener;
    }


    @NonNull
    @Override
    public BoTuViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view =
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(
                                R.layout.botu_itembotu,
                                parent,
                                false
                        );

        return new BoTuViewHolder(
                view
        );
    }


    @Override
    public void onBindViewHolder(
            @NonNull BoTuViewHolder holder,
            int position) {

        BoTuModel boTu =
                danhSachBoTu.get(position);


        /*
         * Lấy tiến độ của bộ từ.
         */
        int tongSoTu =
                boTu.getSoLuongTu();

        int soTuDaThuoc =
                boTu.getSoTuDaThuoc();


        /*
         * Hiển thị tên bộ từ.
         */
        holder.txtTenBoTu.setText(
                boTu.getTenBoTu()
        );


        /*
         * Hiển thị tổng số từ.
         */
        holder.txtSoLuongTu.setText(
                tongSoTu + " từ"
        );


        /*
         * Hiển thị số từ đã thuộc / tổng số từ.
         */
        holder.txtTienDo.setText(
                soTuDaThuoc
                        + "/"
                        + tongSoTu
        );


        /*
         * Tính phần trăm tiến độ.
         */
        int phanTram = 0;

        if (tongSoTu > 0) {

            phanTram =
                    soTuDaThuoc
                            * 100
                            / tongSoTu;
        }


        holder.progressBoTu.setProgress(
                phanTram
        );


        /*
         * Nút XEM BỘ TỪ.
         *
         * Adapter không tự mở Fragment.
         * Nó gửi bộ từ được chọn về ChiTietFolder.
         */
        holder.btnXemBoTu.setOnClickListener(
                v -> {

                    if (listener != null) {

                        listener.onXemBoTu(
                                boTu
                        );
                    }
                }
        );
    }


    @Override
    public int getItemCount() {

        return danhSachBoTu.size();
    }


    /**
     * ViewHolder của card bộ từ.
     */
    public static class BoTuViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtTenBoTu;
        TextView txtSoLuongTu;
        TextView txtTienDo;

        TextView btnMenuBoTu;
        TextView btnXemBoTu;
        TextView btnLuyenTapBoTu;

        ProgressBar progressBoTu;


        public BoTuViewHolder(
                @NonNull View itemView) {

            super(itemView);


            txtTenBoTu =
                    itemView.findViewById(
                            R.id.txt_ten_bo_tu
                    );

            txtSoLuongTu =
                    itemView.findViewById(
                            R.id.txt_so_luong_tu
                    );

            txtTienDo =
                    itemView.findViewById(
                            R.id.txt_tien_do
                    );

            progressBoTu =
                    itemView.findViewById(
                            R.id.progress_bo_tu
                    );

            btnMenuBoTu =
                    itemView.findViewById(
                            R.id.btn_menu_bo_tu
                    );

            btnXemBoTu =
                    itemView.findViewById(
                            R.id.btn_xem_bo_tu
                    );

            btnLuyenTapBoTu =
                    itemView.findViewById(
                            R.id.btn_luyen_tap_bo_tu
                    );
        }
    }


    /**
     * Gửi sự kiện từ Adapter về Fragment.
     */
    public interface OnBoTuClickListener {

        void onXemBoTu(
                BoTuModel boTu
        );
    }
}