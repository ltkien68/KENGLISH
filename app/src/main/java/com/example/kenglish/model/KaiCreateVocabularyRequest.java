package com.example.kenglish.model;

import com.google.gson.annotations.SerializedName;

public class KaiCreateVocabularyRequest {
    @SerializedName("chu_de")
    private final String chuDe;

    @SerializedName("so_luong")
    private final int soLuong;

    public KaiCreateVocabularyRequest(String chuDe, int soLuong) {
        this.chuDe = chuDe;
        this.soLuong = soLuong;
    }
}