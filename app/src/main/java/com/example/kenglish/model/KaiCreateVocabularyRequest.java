package com.example.kenglish.model;

import com.google.gson.annotations.SerializedName;

public class KaiCreateVocabularyRequest {
    @SerializedName("chu_de")
    private final String chuDe;

    @SerializedName("so_luong")
    private final int soLuong;

    @SerializedName("model")
    private final String model;

    public KaiCreateVocabularyRequest(String chuDe, int soLuong) {
        this(chuDe, soLuong, "auto");
    }

    public KaiCreateVocabularyRequest(String chuDe, int soLuong, String model) {
        this.chuDe = chuDe;
        this.soLuong = soLuong;
        this.model = model;
    }
}