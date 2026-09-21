package com.example.kenglish;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.kenglish.adapter.GameLuyenTapAdapter;
import com.example.kenglish.model.GameLuyenTap;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Fragment hiển thị màn hình luyện tập.
 */
public class LuyenTap extends Fragment {

    /*
     * Khung click của 4 bộ lọc.
     */
    private LinearLayout btnChonBoTu;
    private LinearLayout btnChonTrangThai;
    private LinearLayout btnChonThuTu;
    private LinearLayout btnChonSoLuong;

    /*
     * Text hiển thị giá trị đang chọn.
     */
    private TextView txtBoTu;
    private TextView txtTrangThai;
    private TextView txtThuTu;
    private TextView txtSoLuong;

    /*
     * Icon mũi tên của từng bộ lọc.
     */
    private ImageView imgMuiTenBoTu;
    private ImageView imgMuiTenTrangThai;
    private ImageView imgMuiTenThuTu;
    private ImageView imgMuiTenSoLuong;

    private GridView gridGame;

    private final List<GameLuyenTap> danhSachGame =
            new ArrayList<>();

    private GameLuyenTapAdapter gameAdapter;


    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.luyen_tap,
                container,
                false
        );

        anhXa(view);
        khoiTaoDuLieuGame();
        khoiTaoAdapter();
        xuLySuKien();

        return view;
    }


    /**
     * Ánh xạ các View.
     */
    private void anhXa(View view) {

        /*
         * Bộ từ.
         */
        btnChonBoTu =
                view.findViewById(
                        R.id.btn_chon_bo_tu
                );

        txtBoTu =
                view.findViewById(
                        R.id.txt_bo_tu
                );

        imgMuiTenBoTu =
                view.findViewById(
                        R.id.img_mui_ten_bo_tu
                );


        /*
         * Trạng thái.
         */
        btnChonTrangThai =
                view.findViewById(
                        R.id.btn_chon_trang_thai
                );

        txtTrangThai =
                view.findViewById(
                        R.id.txt_trang_thai
                );

        imgMuiTenTrangThai =
                view.findViewById(
                        R.id.img_mui_ten_trang_thai
                );


        /*
         * Thứ tự.
         */
        btnChonThuTu =
                view.findViewById(
                        R.id.btn_chon_thu_tu
                );

        txtThuTu =
                view.findViewById(
                        R.id.txt_thu_tu
                );

        imgMuiTenThuTu =
                view.findViewById(
                        R.id.img_mui_ten_thu_tu
                );


        /*
         * Số lượng.
         */
        btnChonSoLuong =
                view.findViewById(
                        R.id.btn_chon_so_luong
                );

        txtSoLuong =
                view.findViewById(
                        R.id.txt_so_luong
                );

        imgMuiTenSoLuong =
                view.findViewById(
                        R.id.img_mui_ten_so_luong
                );


        /*
         * Danh sách game.
         */
        gridGame =
                view.findViewById(
                        R.id.grid_game
                );
    }


    /**
     * Khởi tạo dữ liệu game mẫu.
     */
    private void khoiTaoDuLieuGame() {

        danhSachGame.clear();

        danhSachGame.add(
                new GameLuyenTap(
                        "Flashcard",
                        "Lật thẻ và ghi nhớ",
                        "◩",
                        5,
                        R.drawable.nen_luyentap_game_flashcard
                )
        );

        danhSachGame.add(
                new GameLuyenTap(
                        "Trắc nghiệm",
                        "Chọn đáp án đúng",
                        "▣",
                        10,
                        R.drawable.nen_luyentap_game_tracnghiem
                )
        );

        danhSachGame.add(
                new GameLuyenTap(
                        "Nối từ với nghĩa",
                        "Ghép các cặp thật nhanh",
                        "↔",
                        10,
                        R.drawable.nen_luyentap_game_noitu
                )
        );

        danhSachGame.add(
                new GameLuyenTap(
                        "Gõ từ vựng",
                        "Nhớ nghĩa và gõ từ",
                        "⌨",
                        10,
                        R.drawable.nen_luyentap_game_gotu
                )
        );

        danhSachGame.add(
                new GameLuyenTap(
                        "Nghe viết",
                        "Nghe và nhập từ đúng",
                        "♬",
                        15,
                        R.drawable.nen_luyentap_game_ngheviet
                )
        );

        danhSachGame.add(
                new GameLuyenTap(
                        "Đặc biệt",
                        "Thử các chế độ mới",
                        "✦",
                        20,
                        R.drawable.nen_luyentap_game_dacbiet
                )
        );
    }


    /**
     * Khởi tạo Adapter cho danh sách game.
     */
    private void khoiTaoAdapter() {

        gameAdapter = new GameLuyenTapAdapter(
                requireContext(),
                danhSachGame
        );

        gridGame.setAdapter(gameAdapter);
    }


    /**
     * Xử lý sự kiện click.
     */
    private void xuLySuKien() {

        /*
         * Chọn bộ từ.
         * Hiện tại dùng dữ liệu mẫu.
         * Sau này thay bằng danh sách bộ từ thật.
         */
        btnChonBoTu.setOnClickListener(v -> {

            List<String> danhSach = Arrays.asList(
                    "Tất cả bộ từ",
                    "Trang từ liên kết",
                    "Từ vựng TOEIC"
            );

            moPopupLuaChon(
                    "Chọn bộ từ",
                    danhSach,
                    txtBoTu.getText().toString(),
                    txtBoTu
            );
        });


        /*
         * Chọn trạng thái từ.
         */
        btnChonTrangThai.setOnClickListener(v -> {

            List<String> danhSach = Arrays.asList(
                    "Chưa thuộc",
                    "Đã thuộc",
                    "Tất cả"
            );

            moPopupLuaChon(
                    "Chọn trạng thái từ",
                    danhSach,
                    txtTrangThai.getText().toString(),
                    txtTrangThai
            );
        });


        /*
         * Chọn thứ tự.
         */
        btnChonThuTu.setOnClickListener(v -> {

            List<String> danhSach = Arrays.asList(
                    "Ngẫu nhiên",
                    "Theo thứ tự"
            );

            moPopupLuaChon(
                    "Chọn thứ tự",
                    danhSach,
                    txtThuTu.getText().toString(),
                    txtThuTu
            );
        });


        /*
         * Chọn số lượng từ.
         */
        btnChonSoLuong.setOnClickListener(v -> {

            List<String> danhSach = Arrays.asList(
                    "10 từ",
                    "20 từ",
                    "50 từ",
                    "100 từ",
                    "200 từ"
            );

            moPopupLuaChon(
                    "Chọn số lượng từ",
                    danhSach,
                    txtSoLuong.getText().toString(),
                    txtSoLuong
            );
        });


        /*
         * Click vào một game.
         */
        gridGame.setOnItemClickListener(
                (parent, view, position, id) -> {

                    GameLuyenTap game =
                            danhSachGame.get(position);

                    // Logic mở game sẽ làm sau.
                }
        );
    }


    /**
     * Mở BottomSheet dùng chung cho
     * Bộ từ, Trạng thái, Thứ tự và Số lượng.
     */
    private void moPopupLuaChon(
            String tieuDe,
            List<String> danhSach,
            String giaTriDangChon,
            TextView textViewCanCapNhat) {

        BottomSheetDialog dialog =
                new BottomSheetDialog(requireContext());

        View popupView = LayoutInflater
                .from(requireContext())
                .inflate(
                        R.layout.luyentap_popup_luachon,
                        null
                );

        dialog.setContentView(popupView);


        /*
         * Ánh xạ View của popup.
         */
        TextView txtTieuDe =
                popupView.findViewById(
                        R.id.txt_tieu_de_popup
                );

        LinearLayout khungDanhSach =
                popupView.findViewById(
                        R.id.khung_danh_sach_lua_chon
                );


        txtTieuDe.setText(tieuDe);


        /*
         * Render từng lựa chọn.
         */
        for (String noiDung : danhSach) {

            View itemView = LayoutInflater
                    .from(requireContext())
                    .inflate(
                            R.layout.luyentap_item_luachon,
                            khungDanhSach,
                            false
                    );


            LinearLayout khungLuaChon =
                    itemView.findViewById(
                            R.id.khung_lua_chon
                    );

            ImageView imgDauChon =
                    itemView.findViewById(
                            R.id.txt_dau_chon
                    );

            TextView txtNoiDung =
                    itemView.findViewById(
                            R.id.txt_noi_dung_lua_chon
                    );


            txtNoiDung.setText(noiDung);


            /*
             * Kiểm tra item hiện tại có đang được chọn không.
             */
            boolean dangChon =
                    noiDung.equals(giaTriDangChon);


            if (dangChon) {

                /*
                 * Item đang chọn.
                 */
                khungLuaChon.setBackgroundResource(
                        R.drawable.nen_luyentap_luachon_chon
                );

                imgDauChon.setImageResource(
                        R.drawable.ic_check
                );

                imgDauChon.setColorFilter(
                        0xFF20C45A
                );

                txtNoiDung.setTextColor(
                        0xFF20C45A
                );

            } else {

                /*
                 * Item chưa chọn.
                 */
                khungLuaChon.setBackgroundResource(
                        R.drawable.nen_luyentap_luachon
                );

                imgDauChon.setImageResource(
                        R.drawable.ic_bullet
                );

                imgDauChon.setColorFilter(
                        0xFF8794AA
                );

                txtNoiDung.setTextColor(
                        0xFFFFFFFF
                );
            }


            /*
             * Khi người dùng chọn item.
             */
            itemView.setOnClickListener(v -> {

                /*
                 * Chỉ cập nhật phần chữ.
                 * Mũi tên là ImageView riêng nên không cần
                 * nối ký tự "⌄" vào String nữa.
                 */
                textViewCanCapNhat.setText(noiDung);

                dialog.dismiss();
            });


            khungDanhSach.addView(itemView);
        }


        dialog.show();
    }
}