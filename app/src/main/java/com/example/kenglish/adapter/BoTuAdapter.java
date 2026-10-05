package com.example.kenglish.adapter;

import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kenglish.R;
import com.example.kenglish.model.BoTuModel;
import com.example.kenglish.model.Folder;
import com.example.kenglish.model.MucBoTu;

import java.util.List;


public class BoTuAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    /*
     * =========================================
     * BIẾN
     * =========================================
     */

    private final List<MucBoTu> danhSach;

    private final OnBoTuDragListener dragListener;


    /*
     * =========================================
     * LISTENER
     * =========================================
     */

    public interface OnBoTuDragListener {

        // Khi nhấn giữ Bộ từ
        void onBatDauKeo(
                View view,
                BoTuModel boTu
        );


        // Khi thả Bộ từ vào Folder
        void onThaVaoFolder(
                BoTuModel boTu,
                Folder folder
        );


        // Khi bấm vào Folder
        void onMoFolder(
                Folder folder
        );


        // Khi bấm XEM BỘ TỪ
        void onXemBoTu(
                BoTuModel boTu
        );
    }


    /*
     * =========================================
     * CONSTRUCTOR
     * =========================================
     */

    public BoTuAdapter(
            List<MucBoTu> danhSach,
            OnBoTuDragListener dragListener) {

        this.danhSach =
                danhSach;

        this.dragListener =
                dragListener;
    }


    /*
     * =========================================
     * XÁC ĐỊNH LOẠI ITEM
     * =========================================
     */

    @Override
    public int getItemViewType(
            int position) {

        return danhSach
                .get(position)
                .getLoai();
    }


    /*
     * =========================================
     * TẠO VIEW HOLDER
     * =========================================
     */

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {


        /*
         * FOLDER
         */
        if (viewType
                == MucBoTu.LOAI_FOLDER) {

            View view =
                    LayoutInflater
                            .from(parent.getContext())
                            .inflate(
                                    R.layout.botu_itemfolder,
                                    parent,
                                    false
                            );

            return new FolderViewHolder(
                    view
            );
        }


        /*
         * BỘ TỪ
         */
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
     * ĐỔ DỮ LIỆU
     * =========================================
     */

    @Override
    public void onBindViewHolder(
            @NonNull RecyclerView.ViewHolder holder,
            int position) {

        MucBoTu muc =
                danhSach.get(position);


        /*
         * =====================================
         * FOLDER
         * =====================================
         */

        if (holder instanceof FolderViewHolder) {

            FolderViewHolder folderHolder =
                    (FolderViewHolder) holder;

            Folder folder =
                    muc.getFolder();


            /*
             * Tên Folder.
             */
            folderHolder.txtTenFolder.setText(
                    folder.getTenFolder()
            );


            /*
             * Số Bộ từ trong Folder.
             */
            folderHolder.txtSoBoTu.setText(
                    folder.getSoBoTu()
                            + " bộ từ"
            );


            /*
             * Bấm Folder để mở ChiTietFolder.
             */
            folderHolder.itemView.setOnClickListener(
                    view -> {

                        if (dragListener != null) {

                            dragListener.onMoFolder(
                                    folder
                            );
                        }
                    }
            );


            /*
             * Folder không kéo được.
             *
             * Folder chỉ nhận Bộ từ
             * đang được kéo vào.
             */
            folderHolder.itemView.setOnDragListener(
                    (view, event) -> {

                        switch (event.getAction()) {


                            /*
                             * Có một Drag bắt đầu.
                             *
                             * Chỉ chấp nhận nếu dữ liệu
                             * đang kéo là BoTuModel.
                             */
                            case DragEvent.ACTION_DRAG_STARTED:

                                return event.getLocalState()
                                        instanceof BoTuModel;


                            /*
                             * Bộ từ đi vào Folder.
                             */
                            case DragEvent.ACTION_DRAG_ENTERED:

                                view.animate()
                                        .scaleX(1.05f)
                                        .scaleY(1.05f)
                                        .setDuration(120)
                                        .start();

                                return true;


                            /*
                             * Bộ từ rời khỏi Folder.
                             */
                            case DragEvent.ACTION_DRAG_EXITED:

                                view.animate()
                                        .scaleX(1f)
                                        .scaleY(1f)
                                        .setDuration(120)
                                        .start();

                                return true;


                            /*
                             * Thả Bộ từ vào Folder.
                             */
                            case DragEvent.ACTION_DROP:

                                /*
                                 * Trả Folder về kích thước cũ.
                                 */
                                view.animate()
                                        .scaleX(1f)
                                        .scaleY(1f)
                                        .setDuration(120)
                                        .start();


                                Object duLieu =
                                        event.getLocalState();


                                if (duLieu
                                        instanceof BoTuModel) {

                                    BoTuModel boTu =
                                            (BoTuModel) duLieu;


                                    if (dragListener != null) {

                                        dragListener.onThaVaoFolder(
                                                boTu,
                                                folder
                                        );
                                    }
                                }

                                return true;


                            /*
                             * Drag kết thúc.
                             */
                            case DragEvent.ACTION_DRAG_ENDED:

                                view.animate()
                                        .scaleX(1f)
                                        .scaleY(1f)
                                        .setDuration(120)
                                        .start();

                                return true;
                        }


                        return false;
                    }
            );
        }


        /*
         * =====================================
         * BỘ TỪ
         * =====================================
         */

        else if (holder instanceof BoTuViewHolder) {

            BoTuViewHolder boTuHolder =
                    (BoTuViewHolder) holder;

            BoTuModel boTu =
                    muc.getBoTu();


            /*
             * -----------------------------
             * HIỂN THỊ DỮ LIỆU
             * -----------------------------
             */

            int tongSoTu =
                    boTu.getSoLuongTu();

            int soTuDaThuoc =
                    boTu.getSoTuDaThuoc();


            /*
             * Tên Bộ từ.
             */
            boTuHolder.txtTenBoTu.setText(
                    boTu.getTenBoTu()
            );


            /*
             * Tổng số từ.
             */
            boTuHolder.txtSoLuongTu.setText(
                    tongSoTu
                            + " từ"
            );


            /*
             * Tiến độ.
             */
            boTuHolder.txtTienDo.setText(
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


            boTuHolder.progressBoTu.setProgress(
                    phanTram
            );


            /*
             * -----------------------------
             * XEM CHI TIẾT BỘ TỪ
             * -----------------------------
             *
             * Adapter chỉ gửi BoTuModel
             * về Fragment BoTu.
             *
             * Việc mở ChiTietBoTu
             * sẽ được xử lý ở BoTu.java.
             */
            boTuHolder.btnXemBoTu.setOnClickListener(
                    view -> {

                        if (dragListener != null) {

                            dragListener.onXemBoTu(
                                    boTu
                            );
                        }
                    }
            );


            /*
             * -----------------------------
             * NHẤN GIỮ ĐỂ KÉO
             * -----------------------------
             */
            boTuHolder.itemView.setOnLongClickListener(
                    view -> {

                        if (dragListener != null) {

                            dragListener.onBatDauKeo(
                                    view,
                                    boTu
                            );
                        }


                        return true;
                    }
            );


            /*
             * Bộ từ không phải Drop Target.
             */
            boTuHolder.itemView.setOnDragListener(
                    null
            );
        }
    }


    /*
     * =========================================
     * SỐ LƯỢNG ITEM
     * =========================================
     */

    @Override
    public int getItemCount() {

        return danhSach.size();
    }


    /*
     * =========================================
     * VIEW HOLDER FOLDER
     * =========================================
     */

    public static class FolderViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtTenFolder;

        TextView txtSoBoTu;


        public FolderViewHolder(
                @NonNull View itemView) {

            super(itemView);


            txtTenFolder =
                    itemView.findViewById(
                            R.id.txt_ten_folder
                    );


            txtSoBoTu =
                    itemView.findViewById(
                            R.id.txt_so_bo_tu
                    );
        }
    }


    /*
     * =========================================
     * VIEW HOLDER BỘ TỪ
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
}