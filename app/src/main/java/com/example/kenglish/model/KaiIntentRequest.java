package com.example.kenglish.model;

import com.google.gson.annotations.SerializedName;

public class KaiIntentRequest {
    @SerializedName("cau_hoi")
    private final String cauHoi;

    public KaiIntentRequest(String cauHoi) {
        this.cauHoi = cauHoi;
    }
}