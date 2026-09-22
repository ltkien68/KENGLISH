package com.example.kenglish.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kenglish.R;
import com.example.kenglish.model.GameLuyenTap;

import java.util.List;

/**
 * Adapter hiển thị danh sách các chế độ luyện tập.
 */
public class GameLuyenTapAdapter extends BaseAdapter {

    private final Context context;
    private final List<GameLuyenTap> danhSachGame;


    /**
     * Khởi tạo Adapter.
     */
    public GameLuyenTapAdapter(
            Context context,
            List<GameLuyenTap> danhSachGame) {

        this.context = context;
        this.danhSachGame = danhSachGame;
    }


    /**
     * Trả về số lượng game.
     */
    @Override
    public int getCount() {
        return danhSachGame.size();
    }


    /**
     * Trả về game tại vị trí được chọn.
     */
    @Override
    public Object getItem(int position) {
        return danhSachGame.get(position);
    }


    /**
     * Trả về ID của item.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }


    /**
     * Tạo và hiển thị giao diện cho từng game.
     */
    @Override
    public View getView(
            int position,
            View convertView,
            ViewGroup parent) {

        ViewHolder holder;


        /*
         * Nếu chưa có View thì tạo mới.
         */
        if (convertView == null) {

            convertView = LayoutInflater
                    .from(context)
                    .inflate(
                            R.layout.luyentap_item_game,
                            parent,
                            false
                    );

            holder = new ViewHolder();


            /*
             * Ánh xạ các View.
             */
            holder.khungGame =
                    convertView.findViewById(
                            R.id.khung_game
                    );

            holder.txtTen =
                    convertView.findViewById(
                            R.id.txt_ten_game
                    );

            holder.txtMoTa =
                    convertView.findViewById(
                            R.id.txt_mo_ta_game
                    );

            holder.txtThuong =
                    convertView.findViewById(
                            R.id.txt_thuong_game
                    );


            /*
             * Lưu ViewHolder để tái sử dụng.
             */
            convertView.setTag(holder);

        } else {

            /*
             * Lấy lại ViewHolder cũ.
             */
            holder = (ViewHolder) convertView.getTag();
        }


        /*
         * Lấy dữ liệu game tại vị trí hiện tại.
         */
        GameLuyenTap game =
                danhSachGame.get(position);


        /*
         * Hiển thị tên game.
         */
        holder.txtTen.setText(
                game.getTenGame()
        );


        /*
         * Hiển thị mô tả game.
         */
        holder.txtMoTa.setText(
                game.getMoTa()
        );


        /*
         * Hiển thị số xu thưởng.
         *
         * Icon xu đã được hiển thị riêng bằng ImageView
         * trong file luyentap_item_game.xml.
         */
        holder.txtThuong.setText(
                "+" + game.getCoinThuong()
        );


        /*
         * Đặt background riêng cho từng loại game.
         */
        holder.khungGame.setBackgroundResource(
                game.getBackground()
        );


        return convertView;
    }


    /**
     * ViewHolder giúp tái sử dụng View,
     * tránh findViewById nhiều lần.
     */
    private static class ViewHolder {

        LinearLayout khungGame;

        TextView txtTen;
        TextView txtMoTa;
        TextView txtThuong;
    }
}