package com.example.kenglish.model;

import com.google.gson.annotations.SerializedName;

public class AiChatResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private DuLieuAI data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public DuLieuAI getData() {
        return data;
    }

    public static class DuLieuAI {

        @SerializedName("cau_hoi")
        private String cauHoi;

        @SerializedName("tra_loi")
        private String traLoi;

        public String getCauHoi() {
            return cauHoi;
        }

        public String getTraLoi() {
            return traLoi;
        }
    }
}