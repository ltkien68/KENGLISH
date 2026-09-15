package com.example.kenglish;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


/**
 * Fragment hiển thị màn hình quản lý các bộ từ vựng.
 */
public class BoTu extends Fragment {


    /**
     * Khởi tạo giao diện của màn hình Bộ từ.
     */
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.bo_tu,
                container,
                false
        );

    }


}