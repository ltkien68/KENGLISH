package com.example.kenglish;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
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

    private TextView btnTaoBoTu;
    private TextView btnTaoFolder;


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

        btnTaoBoTu.setOnClickListener(v -> {
            moPopupTaoBoTu();
        });

        btnTaoFolder.setOnClickListener(v -> {
            moPopupTaoFolder();
        });
    }


    /**
     * Hiển thị popup tạo bộ từ mới.
     */
    private void moPopupTaoBoTu() {

        BottomSheetDialog dialog = new BottomSheetDialog(
                requireContext()
        );

        View popupView = getLayoutInflater().inflate(
                R.layout.botu_popup_taobotu,
                null
        );

        dialog.setContentView(popupView);


        EditText edtTenBoTu = popupView.findViewById(
                R.id.edt_ten_bo_tu
        );

        EditText edtMoTaBoTu = popupView.findViewById(
                R.id.edt_mo_ta_bo_tu
        );

        TextView btnXacNhan = popupView.findViewById(
                R.id.btn_xac_nhan_tao_bo_tu
        );


        btnXacNhan.setOnClickListener(v -> {

            String tenBoTu = edtTenBoTu
                    .getText()
                    .toString()
                    .trim();

            String moTa = edtMoTaBoTu
                    .getText()
                    .toString()
                    .trim();


            // Kiểm tra tên bộ từ
            if (tenBoTu.isEmpty()) {

                edtTenBoTu.setError(
                        "Vui lòng nhập tên bộ từ"
                );

                edtTenBoTu.requestFocus();

                return;
            }


            /*
             * Hiện tại mới xử lý giao diện.
             * Sau này sẽ lưu bộ từ vào database tại đây.
             */

            Toast.makeText(
                    requireContext(),
                    "Đã tạo bộ từ: " + tenBoTu,
                    Toast.LENGTH_SHORT
            ).show();


            dialog.dismiss();
        });


        dialog.show();

        moBanPhim(edtTenBoTu, dialog);
    }


    /**
     * Hiển thị popup tạo folder mới.
     */
    private void moPopupTaoFolder() {

        BottomSheetDialog dialog = new BottomSheetDialog(
                requireContext()
        );

        View popupView = getLayoutInflater().inflate(
                R.layout.botu_popup_taofolder,
                null
        );

        dialog.setContentView(popupView);


        EditText edtTenFolder = popupView.findViewById(
                R.id.edt_ten_folder
        );

        TextView btnXacNhan = popupView.findViewById(
                R.id.btn_xac_nhan_tao_folder
        );


        btnXacNhan.setOnClickListener(v -> {

            String tenFolder = edtTenFolder
                    .getText()
                    .toString()
                    .trim();


            // Kiểm tra tên folder
            if (tenFolder.isEmpty()) {

                edtTenFolder.setError(
                        "Vui lòng nhập tên folder"
                );

                edtTenFolder.requestFocus();

                return;
            }


            /*
             * Hiện tại mới xử lý giao diện.
             * Sau này sẽ lưu folder vào database tại đây.
             */

            Toast.makeText(
                    requireContext(),
                    "Đã tạo folder: " + tenFolder,
                    Toast.LENGTH_SHORT
            ).show();


            dialog.dismiss();
        });


        dialog.show();

        moBanPhim(edtTenFolder, dialog);
    }


    /**
     * Tự động focus vào ô nhập liệu và mở bàn phím
     * khi popup được hiển thị.
     */
    private void moBanPhim(
            EditText editText,
            BottomSheetDialog dialog) {

        editText.requestFocus();

        Window window = dialog.getWindow();

        if (window != null) {

            window.setSoftInputMode(
                    WindowManager.LayoutParams
                            .SOFT_INPUT_STATE_ALWAYS_VISIBLE
            );
        }
    }
}