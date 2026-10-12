package com.example.kenglish.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kenglish.R;
import com.example.kenglish.model.TinNhanKai;

import java.util.List;

public class TinNhanKaiAdapter extends RecyclerView.Adapter<TinNhanKaiAdapter.TinNhanViewHolder> {

    private static final int LOAI_NGUOI_DUNG = 1;
    private static final int LOAI_KAI = 2;

    private final List<TinNhanKai> danhSach;

    public TinNhanKaiAdapter(List<TinNhanKai> danhSach) {
        this.danhSach = danhSach;
    }

    @Override
    public int getItemViewType(int position) {
        return danhSach.get(position).isNguoiDung()
                ? LOAI_NGUOI_DUNG : LOAI_KAI;
    }

    @NonNull
    @Override
    public TinNhanViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        int layout = viewType == LOAI_NGUOI_DUNG
                ? R.layout.item_tin_nhan_nguoi_dung
                : R.layout.item_tin_nhan_ai;

        View view = LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);

        return new TinNhanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull TinNhanViewHolder holder, int position) {

        TinNhanKai tinNhan = danhSach.get(position);
        holder.txtNoiDung.setText(tinNhan.getNoiDung());
    }

    @Override
    public int getItemCount() {
        return danhSach.size();
    }

    static class TinNhanViewHolder extends RecyclerView.ViewHolder {
        TextView txtNoiDung;

        TinNhanViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNoiDung = itemView.findViewById(
                    R.id.txt_noi_dung_tin_nhan
            );
        }
    }
}
