package com.example.kenglish.api;

import com.example.kenglish.model.ApiResponse;
import com.example.kenglish.model.DangKyRequest;
import com.example.kenglish.model.DangNhapRequest;
import com.example.kenglish.model.DangNhapResponse;
import com.example.kenglish.model.XacThucEmailRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("auth/dang_ky.php")
    Call<ApiResponse> dangKy(
            @Body DangKyRequest request
    );

    @POST("auth/xac_thuc_email.php")
    Call<ApiResponse> xacThucEmail(
            @Body XacThucEmailRequest request
    );

    @POST("auth/dang_nhap.php")
    Call<DangNhapResponse> dangNhap(
            @Body DangNhapRequest request
    );
}