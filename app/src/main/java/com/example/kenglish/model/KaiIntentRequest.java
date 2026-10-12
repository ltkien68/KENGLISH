package com.example.kenglish.model;

import com.google.gson.annotations.SerializedName;

public class KaiIntentRequest {
    @SerializedName("cau_hoi")
    private final String cauHoi;

    @SerializedName("model")
    private final String model;

    public KaiIntentRequest(String cauHoi) {
        this(cauHoi, "auto");
    }

    public KaiIntentRequest(String cauHoi, String model) {
        this.cauHoi = cauHoi;
        this.model = model;
    }
}