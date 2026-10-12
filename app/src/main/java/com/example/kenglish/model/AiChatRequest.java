package com.example.kenglish.model;

import com.google.gson.annotations.SerializedName;

public class AiChatRequest {
    @SerializedName("cau_hoi")
    private final String cauHoi;

    @SerializedName("model")
    private final String model;

    public AiChatRequest(String cauHoi) {
        this(cauHoi, "auto");
    }

    public AiChatRequest(String cauHoi, String model) {
        this.cauHoi = cauHoi;
        this.model = model;
    }
}