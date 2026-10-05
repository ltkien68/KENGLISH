package com.example.kenglish.model;

import java.util.List;

public class HoatDongNamResponse {

    private int nam;
    private int current_streak;
    private List<String> ngay_hoat_dong;


    public int getNam() {
        return nam;
    }


    public int getCurrentStreak() {
        return current_streak;
    }


    public List<String> getNgayHoatDong() {
        return ngay_hoat_dong;
    }
}