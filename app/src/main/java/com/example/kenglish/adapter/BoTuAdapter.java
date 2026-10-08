
package com.example.kenglish.adapter;

import android.view.DragEvent;
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
import com.example.kenglish.model.Folder;
import com.example.kenglish.model.MucBoTu;

import java.util.List;

public class BoTuAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<MucBoTu> danhSach;
    private final OnBoTuDragListener dragListener;
    private final OnXoaBoTuListener onXoaBoTuListener;

    // =========================================
    // LISTENER
    // =========================================

    public interface OnXoaBoTuListener {
        void onXoaBoTu(BoTuModel boTu);
    }

    public interface OnBoTuDragListener {

        void onBatDauKeo(View view, BoTuModel boTu);

        void onThaVaoFolder(BoTuModel boTu, Folder folder);

        void onMoFolder(Folder folder);

        void onXemBoTu(BoTuModel boTu);

        // Nhấn nút LUYỆN TẬP trên card bộ từ.
        void onLuyenTapBoTu(BoTuModel boTu);
    }

    // =========================================
    // CONSTRUCTOR
    // =========================================

    public BoTuAdapter(
            List<MucBoTu> danhSach,
            OnBoTuDragListener dragListener,
            OnXoaBoTuListener onXoaBoTuListener) {

        this.danhSach = danhSach;
        this.dragListener = dragListener;
        this.onXoaBoTuListener = onXoaBoTuListener;
    }

    // =========================================
    // LOẠI ITEM
    // =========================================

    @Override
    public int getItemViewType(int position) {
        return danhSach.get(position).getLoai();
    }

    // =========================================
    // TẠO VIEW HOLDER
    // =========================================

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        if (viewType == MucBoTu.LOAI_FOLDER) {

            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(
                            R.layout.botu_itemfolder,
                            parent,
                            false
                    );

            return new FolderViewHolder(view);
        }

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.botu_itembotu,
                        parent,
                        false
                );

        return new BoTuViewHolder(view);
    }

    // =========================================
    // HIỂN THỊ DỮ LIỆU
    // =========================================

    @Override
    public void onBindViewHolder(
            @NonNull RecyclerView.ViewHolder holder,
            int position) {

        MucBoTu muc = danhSach.get(position);

        // =====================================
        // FOLDER
        // =====================================

        if (holder instanceof FolderViewHolder) {

            FolderViewHolder folderHolder =
                    (FolderViewHolder) holder;

            Folder folder = muc.getFolder();

            folderHolder.txtTenFolder.setText(
                    folder.getTenFolder()
            );

            folderHolder.txtSoBoTu.setText(
                    folder.getSoBoTu() + " bộ từ"
            );

            // Mở folder.
            folderHolder.itemView.setOnClickListener(view -> {

                if (dragListener != null) {
                    dragListener.onMoFolder(folder);
                }
            });

            // Nhận bộ từ được kéo vào folder.
            folderHolder.itemView.setOnDragListener(
                    (view, event) -> {

                        switch (event.getAction()) {

                            case DragEvent.ACTION_DRAG_STARTED:

                                return event.getLocalState()
                                        instanceof BoTuModel;

                            case DragEvent.ACTION_DRAG_ENTERED:

                                view.animate()
                                        .scaleX(1.05f)
                                        .scaleY(1.05f)
                                        .setDuration(120)
                                        .start();

                                return true;

                            case DragEvent.ACTION_DRAG_EXITED:

                                view.animate()
                                        .scaleX(1f)
                                        .scaleY(1f)
                                        .setDuration(120)
                                        .start();

                                return true;

                            case DragEvent.ACTION_DROP:

                                view.animate()
                                        .scaleX(1f)
                                        .scaleY(1f)
                                        .setDuration(120)
                                        .start();

                                Object duLieu = event.getLocalState();

                                if (duLieu instanceof BoTuModel) {

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

        // =====================================
        // BỘ TỪ
        // =====================================

        else if (holder instanceof BoTuViewHolder) {

            BoTuViewHolder boTuHolder =
                    (BoTuViewHolder) holder;

            BoTuModel boTu = muc.getBoTu();

            // =================================
            // THÔNG TIN BỘ TỪ
            // =================================

            int tongSoTu = boTu.getSoLuongTu();
            int soTuDaThuoc = boTu.getSoTuDaThuoc();

            boTuHolder.txtTenBoTu.setText(
                    boTu.getTenBoTu()
            );

            boTuHolder.txtSoLuongTu.setText(
                    tongSoTu + " từ"
            );

            boTuHolder.txtTienDo.setText(
                    soTuDaThuoc + "/" + tongSoTu
            );

            int phanTram = 0;

            if (tongSoTu > 0) {
                phanTram = soTuDaThuoc * 100 / tongSoTu;
            }

            boTuHolder.progressBoTu.setProgress(phanTram);

            // =================================
            // MENU XÓA BỘ TỪ
            // =================================

            boTuHolder.btnMenuBoTu.setOnClickListener(view -> {

                View popupView = LayoutInflater
                        .from(view.getContext())
                        .inflate(
                                R.layout.botu_popup_menu,
                                null
                        );

                PopupWindow popupWindow = new PopupWindow(
                        popupView,
                        (int) (
                                150 * view.getResources()
                                        .getDisplayMetrics()
                                        .density
                        ),
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        true
                );

                popupWindow.setOutsideTouchable(true);

                LinearLayout btnXoaBoTu =
                        popupView.findViewById(
                                R.id.btn_xoa_bo_tu
                        );

                btnXoaBoTu.setOnClickListener(v -> {

                    popupWindow.dismiss();

                    if (onXoaBoTuListener != null) {
                        onXoaBoTuListener.onXoaBoTu(boTu);
                    }
                });

                popupWindow.showAsDropDown(
                        boTuHolder.btnMenuBoTu,
                        -120,
                        -5
                );
            });

            // =================================
            // NÚT XEM
            // =================================

            boTuHolder.btnXemBoTu.setOnClickListener(view -> {

                if (dragListener != null) {
                    dragListener.onXemBoTu(boTu);
                }
            });

            // =================================
            // NÚT LUYỆN TẬP
            // =================================

            boTuHolder.btnLuyenTapBoTu.setOnClickListener(view -> {

                if (dragListener != null) {
                    dragListener.onLuyenTapBoTu(boTu);
                }
            });

            // =================================
            // NHẤN GIỮ ĐỂ KÉO BỘ TỪ
            // =================================

            boTuHolder.itemView.setOnLongClickListener(view -> {

                if (dragListener != null) {
                    dragListener.onBatDauKeo(
                            view,
                            boTu
                    );
                }

                return true;
            });

            // Bộ từ không nhận sự kiện thả.
            boTuHolder.itemView.setOnDragListener(null);
        }
    }

    // =========================================
    // SỐ LƯỢNG ITEM
    // =========================================

    @Override
    public int getItemCount() {
        return danhSach.size();
    }

    // =========================================
    // VIEW HOLDER FOLDER
    // =========================================

    public static class FolderViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtTenFolder;
        TextView txtSoBoTu;

        public FolderViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTenFolder = itemView.findViewById(
                    R.id.txt_ten_folder
            );

            txtSoBoTu = itemView.findViewById(
                    R.id.txt_so_bo_tu
            );
        }
    }

    // =========================================
    // VIEW HOLDER BỘ TỪ
    // =========================================

    public static class BoTuViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtTenBoTu;
        TextView txtSoLuongTu;
        TextView txtTienDo;

        TextView btnMenuBoTu;
        TextView btnXemBoTu;
        TextView btnLuyenTapBoTu;

        ProgressBar progressBoTu;

        public BoTuViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTenBoTu = itemView.findViewById(
                    R.id.txt_ten_bo_tu
            );

            txtSoLuongTu = itemView.findViewById(
                    R.id.txt_so_luong_tu
            );

            txtTienDo = itemView.findViewById(
                    R.id.txt_tien_do
            );

            progressBoTu = itemView.findViewById(
                    R.id.progress_bo_tu
            );

            btnMenuBoTu = itemView.findViewById(
                    R.id.btn_menu_bo_tu
            );

            btnXemBoTu = itemView.findViewById(
                    R.id.btn_xem_bo_tu
            );

            btnLuyenTapBoTu = itemView.findViewById(
                    R.id.btn_luyen_tap_bo_tu
            );
        }
    }
}
