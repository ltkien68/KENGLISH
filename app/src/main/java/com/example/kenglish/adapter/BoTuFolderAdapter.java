package com.example.kenglish.adapter;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kenglish.R;
import com.example.kenglish.model.BoTuModel;

import java.util.List;


public class BoTuFolderAdapter
        extends RecyclerView.Adapter<BoTuFolderAdapter.BoTuViewHolder> {


    /*
     * =========================================
     * BIẾN
     * =========================================
     */

    private final List<BoTuModel> danhSachBoTu;

    private final OnBoTuClickListener listener;


    /*
     * =========================================
     * CONSTRUCTOR
     * =========================================
     */

    public BoTuFolderAdapter(
            List<BoTuModel> danhSachBoTu,
            OnBoTuClickListener listener) {

        this.danhSachBoTu =
                danhSachBoTu;

        this.listener =
                listener;
    }


    /*
     * =========================================
     * TẠO VIEW HOLDER
     * =========================================
     */

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


    /*
     * =========================================
     * HIỂN THỊ DỮ LIỆU
     * =========================================
     */

    @Override
    public void onBindViewHolder(
            @NonNull BoTuViewHolder holder,
            int position) {

        BoTuModel boTu =
                danhSachBoTu.get(
                        position
                );


        /*
         * =====================================
         * TIẾN ĐỘ
         * =====================================
         */

        int tongSoTu =
                boTu.getSoLuongTu();


        int soTuDaThuoc =
                boTu.getSoTuDaThuoc();


        /*
         * Tên Bộ từ.
         */
        holder.txtTenBoTu.setText(
                boTu.getTenBoTu()
        );


        /*
         * Tổng số từ.
         */
        holder.txtSoLuongTu.setText(
                tongSoTu
                        + " từ"
        );


        /*
         * Số từ đã thuộc / tổng số từ.
         */
        holder.txtTienDo.setText(
                soTuDaThuoc
                        + "/"
                        + tongSoTu
        );


        /*
         * Tính phần trăm.
         */
        int phanTram =
                0;


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
         * =====================================
         * MENU BỘ TỪ
         * =====================================
         */

        holder.btnMenuBoTu.setOnClickListener(
                view -> {

                    /*
                     * Inflate giao diện menu custom.
                     */
                    View popupView =
                            LayoutInflater
                                    .from(view.getContext())
                                    .inflate(
                                            R.layout.botu_popup_menu,
                                            null
                                    );


                    /*
                     * Chiều rộng menu: 150dp.
                     */
                    int popupWidth =
                            (int) (
                                    150
                                            * view
                                            .getResources()
                                            .getDisplayMetrics()
                                            .density
                            );


                    /*
                     * Tạo PopupWindow.
                     */
                    PopupWindow popupWindow =
                            new PopupWindow(
                                    popupView,
                                    popupWidth,
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    true
                            );


                    /*
                     * Cho phép bấm ra ngoài
                     * để đóng popup.
                     */
                    popupWindow.setOutsideTouchable(
                            true
                    );


                    /*
                     * Background trong suốt
                     * để sử dụng background
                     * của layout custom.
                     */
                    popupWindow.setBackgroundDrawable(
                            new ColorDrawable(
                                    Color.TRANSPARENT
                            )
                    );


                    /*
                     * Không dùng shadow mặc định.
                     */
                    popupWindow.setElevation(
                            0f
                    );


                    /*
                     * =================================
                     * NÚT XÓA BỘ TỪ
                     * =================================
                     */

                    LinearLayout btnXoaBoTu =
                            popupView.findViewById(
                                    R.id.btn_xoa_bo_tu
                            );


                    btnXoaBoTu.setOnClickListener(
                            v -> {

                                /*
                                 * Đóng menu trước.
                                 */
                                popupWindow.dismiss();


                                /*
                                 * Adapter không gọi API.
                                 *
                                 * Chỉ gửi Bộ từ cần xóa
                                 * về ChiTietFolder.
                                 */
                                if (listener != null) {

                                    listener.onXoaBoTu(
                                            boTu
                                    );
                                }
                            }
                    );


                    /*
                     * =================================
                     * HIỂN THỊ MENU
                     * =================================
                     *
                     * Menu xuất hiện phía dưới dấu ⋮
                     * và căn về bên trái.
                     */

                    int offsetX =
                            -popupWidth
                                    + holder
                                    .btnMenuBoTu
                                    .getWidth();


                    popupWindow.showAsDropDown(
                            holder.btnMenuBoTu,
                            offsetX,
                            -5
                    );
                }
        );


        /*
         * =====================================
         * XEM BỘ TỪ
         * =====================================
         */

        holder.btnXemBoTu.setOnClickListener(
                view -> {

                    if (listener != null) {

                        listener.onXemBoTu(
                                boTu
                        );
                    }
                }
        );


        /*
         * =====================================
         * LUYỆN TẬP BỘ TỪ TRONG FOLDER
         * =====================================
         */
        holder.btnLuyenTapBoTu.setOnClickListener(view -> {

            if (listener != null) {
                listener.onLuyenTapBoTu(boTu);
            }
        });

    }


    /*
     * =========================================
     * SỐ LƯỢNG ITEM
     * =========================================
     */

    @Override
    public int getItemCount() {

        return danhSachBoTu.size();
    }


    /*
     * =========================================
     * VIEW HOLDER
     * =========================================
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

            super(
                    itemView
            );


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


    /*
     * =========================================
     * CALLBACK VỀ CHI TIẾT FOLDER
     * =========================================
     */


    public interface OnBoTuClickListener {

        void onXemBoTu(BoTuModel boTu);

        void onXoaBoTu(BoTuModel boTu);

        void onLuyenTapBoTu(BoTuModel boTu);
    }

}