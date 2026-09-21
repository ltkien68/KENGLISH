package com.example.kenglish;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;


/**
 * Fragment hiển thị màn hình quản lý các bộ từ vựng.
 */
public class BoTu extends Fragment {

    /*
     * Nút tạo bộ từ hiện tại vẫn là TextView.
     */
    private TextView btnTaoBoTu;

    /*
     * Component tạo folder đã được tách thành
     * LinearLayout gồm icon + TextView.
     */
    private LinearLayout btnTaoFolder;


    /**
     * Khởi tạo giao diện của màn hình Bộ từ.
     */
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.bo_tu,
                container,
                false
        );

        anhXa(view);
        xuLySuKien();

        return view;
    }


    /**
     * Ánh xạ các thành phần giao diện.
     */
    private void anhXa(View view) {

        btnTaoBoTu = view.findViewById(
                R.id.component_tao_bo_tu
        );

        btnTaoFolder = view.findViewById(
                R.id.component_tao_folder
        );
    }


    /**
     * Xử lý các sự kiện của màn hình Bộ từ.
     */
    private void xuLySuKien() {

        /*
         * Mở popup tạo bộ từ.
         */
        btnTaoBoTu.setOnClickListener(v -> {
            moPopupTaoBoTu();
        });


        /*
         * Mở popup tạo folder.
         */
        btnTaoFolder.setOnClickListener(v -> {
            moPopupTaoFolder();
        });
    }


    /**
     * Hiển thị popup tạo bộ từ mới.
     */
    private void moPopupTaoBoTu() {

        BottomSheetDialog dialog =
                new BottomSheetDialog(
                        requireContext()
                );


        View popupView =
                getLayoutInflater().inflate(
                        R.layout.botu_popup_taobotu,
                        null
                );


        dialog.setContentView(
                popupView
        );


        /*
         * Ánh xạ các View trong popup.
         */
        EditText edtTenBoTu =
                popupView.findViewById(
                        R.id.edt_ten_bo_tu
                );

        EditText edtMoTaBoTu =
                popupView.findViewById(
                        R.id.edt_mo_ta_bo_tu
                );

        TextView btnXacNhan =
                popupView.findViewById(
                        R.id.btn_xac_nhan_tao_bo_tu
                );


        /*
         * Xử lý khi nhấn nút xác nhận.
         */
        btnXacNhan.setOnClickListener(v -> {

            String tenBoTu =
                    edtTenBoTu
                            .getText()
                            .toString()
                            .trim();

            String moTa =
                    edtMoTaBoTu
                            .getText()
                            .toString()
                            .trim();


            /*
             * Kiểm tra tên bộ từ.
             */
            if (tenBoTu.isEmpty()) {

                edtTenBoTu.setError(
                        "Vui lòng nhập tên bộ từ"
                );

                edtTenBoTu.requestFocus();

                return;
            }


            /*
             * Hiện tại mới xử lý giao diện.
             *
             * Sau này sẽ lưu:
             * - tenBoTu
             * - moTa
             *
             * vào database tại đây.
             */


            Toast.makeText(
                    requireContext(),
                    "Đã tạo bộ từ: " + tenBoTu,
                    Toast.LENGTH_SHORT
            ).show();


            /*
             * Đóng popup sau khi tạo thành công.
             */
            dialog.dismiss();
        });


        /*
         * Hiển thị BottomSheet.
         */
        dialog.show();


        /*
         * Tự động focus vào ô tên bộ từ
         * và mở bàn phím.
         */
        moBanPhim(
                edtTenBoTu,
                dialog
        );
    }


    /**
     * Hiển thị popup tạo folder mới.
     */
    private void moPopupTaoFolder() {

        BottomSheetDialog dialog =
                new BottomSheetDialog(
                        requireContext()
                );


        View popupView =
                getLayoutInflater().inflate(
                        R.layout.botu_popup_taofolder,
                        null
                );


        dialog.setContentView(
                popupView
        );


        /*
         * Ánh xạ các View trong popup.
         */
        EditText edtTenFolder =
                popupView.findViewById(
                        R.id.edt_ten_folder
                );

        TextView btnXacNhan =
                popupView.findViewById(
                        R.id.btn_xac_nhan_tao_folder
                );


        /*
         * Xử lý khi nhấn nút xác nhận.
         */
        btnXacNhan.setOnClickListener(v -> {

            String tenFolder =
                    edtTenFolder
                            .getText()
                            .toString()
                            .trim();


            /*
             * Kiểm tra tên folder.
             */
            if (tenFolder.isEmpty()) {

                edtTenFolder.setError(
                        "Vui lòng nhập tên folder"
                );

                edtTenFolder.requestFocus();

                return;
            }


            /*
             * Hiện tại mới xử lý giao diện.
             *
             * Sau này sẽ lưu folder
             * vào database tại đây.
             */


            Toast.makeText(
                    requireContext(),
                    "Đã tạo folder: " + tenFolder,
                    Toast.LENGTH_SHORT
            ).show();


            /*
             * Đóng popup sau khi tạo thành công.
             */
            dialog.dismiss();
        });


        /*
         * Hiển thị BottomSheet.
         */
        dialog.show();


        /*
         * Tự động focus vào ô tên folder
         * và mở bàn phím.
         */
        moBanPhim(
                edtTenFolder,
                dialog
        );
    }


    /**
     * Tự động focus vào ô nhập liệu
     * và mở bàn phím khi popup được hiển thị.
     */
    private void moBanPhim(
            EditText editText,
            BottomSheetDialog dialog) {

        editText.requestFocus();


        Window window =
                dialog.getWindow();


        if (window != null) {

            window.setSoftInputMode(
                    WindowManager.LayoutParams
                            .SOFT_INPUT_STATE_ALWAYS_VISIBLE
            );
        }
    }
}