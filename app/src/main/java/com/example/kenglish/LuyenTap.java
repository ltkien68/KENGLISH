package com.example.kenglish;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


/**
 * Fragment hiển thị các chế độ luyện tập từ vựng.
 */
public class LuyenTap extends Fragment {


    /**
     * Khởi tạo giao diện của màn hình Luyện tập.
     */
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.luyen_tap,
                container,
                false
        );

    }


}