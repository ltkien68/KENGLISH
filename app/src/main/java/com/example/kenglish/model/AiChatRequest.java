package com.example.kenglish.model;

import com.google.gson.annotations.SerializedName;

public class AiChatRequest {

    @SerializedName("cau_hoi")
    private final String cauHoi;

    public AiChatRequest(String cauHoi) {
        this.cauHoi = cauHoi;
    }

    public String getCauHoi() {
        return cauHoi;
    }
}