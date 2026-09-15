package com.example.kenglish;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


/**
 * Fragment hiển thị cửa hàng của ứng dụng.
 */
public class CuaHang extends Fragment {


    /**
     * Khởi tạo giao diện của màn hình Cửa hàng.
     */
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.cua_hang,
                container,
                false
        );

    }


}