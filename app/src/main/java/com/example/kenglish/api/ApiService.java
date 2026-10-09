
package com.example.kenglish.api;

import com.example.kenglish.model.ApiResponse;
import com.example.kenglish.model.BangXepHangResponse;
import com.example.kenglish.model.BoTuModel;
import com.example.kenglish.model.CapNhatTrangThaiTuRequest;
import com.example.kenglish.model.ChiTietFolderResponse;
import com.example.kenglish.model.ChuyenBoTuRequest;
import com.example.kenglish.model.DangKyRequest;
import com.example.kenglish.model.DangNhapRequest;
import com.example.kenglish.model.DangNhapResponse;
import com.example.kenglish.model.DanhSachTuResponse;
import com.example.kenglish.model.Folder;
import com.example.kenglish.model.HoatDongNamResponse;
import com.example.kenglish.model.LichSuLuyenTapResponse;
import com.example.kenglish.model.LuuTienTrinhRequest;
import com.example.kenglish.model.LuuTienTrinhResponse;
import com.example.kenglish.model.TaoBoTuRequest;
import com.example.kenglish.model.TaoFolderRequest;
import com.example.kenglish.model.ThemTuRequest;
import com.example.kenglish.model.ThongKeHocTap;
import com.example.kenglish.model.ThongKeLuyenTapResponse;
import com.example.kenglish.model.TuVung;
import com.example.kenglish.model.XacThucEmailRequest;
import com.example.kenglish.model.DictionaryResponse;
import com.example.kenglish.model.SuggestionResponse;
import com.example.kenglish.model.LichSuTraTuResponse;
import com.example.kenglish.model.ThemLichSuRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // ==================== AUTH ====================

    @POST("auth/dang-ky")
    Call<ApiResponse> dangKy(
            @Body DangKyRequest request
    );

    @POST("auth/xac-thuc-email")
    Call<ApiResponse> xacThucEmail(
            @Body XacThucEmailRequest request
    );

    @POST("auth/dang-nhap")
    Call<DangNhapResponse> dangNhap(
            @Body DangNhapRequest request
    );

    @POST("auth/dang-xuat")
    Call<ApiResponse> dangXuat(
            @Header("Authorization") String token
    );

    @DELETE("user/me")
    Call<ApiResponse> xoaTaiKhoan(
            @Header("Authorization") String token
    );

    // ==================== DICTIONARY ====================

    @GET("dictionary/search")
    Call<SuggestionResponse> timKiemTu(
            @Query("q") String tuKhoa
    );

    @GET("dictionary/lookup")
    Call<DictionaryResponse> traTu(
            @Query("word") String tu
    );

    @POST("dictionary/history")
    Call<ApiResponse> themLichSuTraTu(
            @Header("Authorization") String token,
            @Body ThemLichSuRequest request
    );

    @GET("dictionary/history")
    Call<LichSuTraTuResponse> layLichSuTraTu(
            @Header("Authorization") String token
    );

    @DELETE("dictionary/history")
    Call<ApiResponse> xoaLichSuTraTu(
            @Header("Authorization") String token
    );

    // ==================== FOLDER ====================

    @POST("folder")
    Call<ApiResponse<Folder>> taoFolder(
            @Header("Authorization") String token,
            @Body TaoFolderRequest request
    );

    @GET("folder")
    Call<ApiResponse<List<Folder>>> layDanhSachFolder(
            @Header("Authorization") String token
    );

    @GET("folder/{folderId}/vocabulary")
    Call<ApiResponse<ChiTietFolderResponse>> layBoTuTheoFolder(
            @Header("Authorization") String token,
            @Path("folderId") int folderId
    );

    // ==================== BỘ TỪ ====================

    @POST("vocabulary")
    Call<ApiResponse<BoTuModel>> taoBoTu(
            @Header("Authorization") String token,
            @Body TaoBoTuRequest request
    );

    @GET("vocabulary")
    Call<ApiResponse<List<BoTuModel>>> layDanhSachBoTu(
            @Header("Authorization") String token
    );

    @GET("vocabulary")
    Call<ApiResponse<List<BoTuModel>>> layTatCaBoTuLuyenTap(
            @Header("Authorization") String token,
            @Query("tat_ca") boolean tatCa
    );

    @PATCH("vocabulary/{boTuId}/folder")
    Call<ApiResponse<Object>> chuyenBoTuVaoFolder(
            @Header("Authorization") String token,
            @Path("boTuId") int boTuId,
            @Body ChuyenBoTuRequest request
    );

    @DELETE("vocabulary/{id}")
    Call<ApiResponse<Object>> xoaBoTu(
            @Header("Authorization") String token,
            @Path("id") int boTuId
    );

    // ==================== TỪ VỰNG ====================

    @POST("vocabulary/{boTuId}/words")
    Call<ApiResponse<TuVung>> themTu(
            @Header("Authorization") String token,
            @Path("boTuId") int boTuId,
            @Body ThemTuRequest request
    );

    @GET("vocabulary/{boTuId}/words")
    Call<ApiResponse<DanhSachTuResponse>> layDanhSachTu(
            @Header("Authorization") String token,
            @Path("boTuId") int boTuId
    );

    @DELETE("vocabulary/{boTuId}/words/{tuVungId}")
    Call<ApiResponse<Object>> xoaTu(
            @Header("Authorization") String token,
            @Path("boTuId") int boTuId,
            @Path("tuVungId") int tuVungId
    );

    @PATCH("vocabulary/{boTuId}/words/{tuVungId}/status")
    Call<ApiResponse<Object>> capNhatTrangThaiTu(
            @Header("Authorization") String token,
            @Path("boTuId") int boTuId,
            @Path("tuVungId") int tuVungId,
            @Body CapNhatTrangThaiTuRequest request
    );

    // ==================== LUYỆN TẬP ====================

    @GET("practice/words")
    Call<ApiResponse<DanhSachTuResponse>> layTuLuyenTap(
            @Header("Authorization") String token,
            @Query("bo_tu_id") int boTuId,
            @Query("trang_thai") String trangThai,
            @Query("thu_tu") String thuTu,
            @Query("so_luong") int soLuong
    );

    // ==================== HOẠT ĐỘNG ====================

    @GET("activity/current-year")
    Call<ApiResponse<HoatDongNamResponse>> layHoatDongNamHienTai(
            @Header("Authorization") String token
    );

    // ==================== THỐNG KÊ ====================

    @GET("user/thong-ke-hoc-tap")
    Call<ApiResponse<ThongKeHocTap>> layThongKeHocTap(
            @Header("Authorization") String token
    );


    @POST("practice/save-progress")
    Call<ApiResponse<LuuTienTrinhResponse>> luuTienTrinhLuyenTap(
            @Header("Authorization") String token,
            @Body LuuTienTrinhRequest request
    );

    // ==================== LỊCH SỬ LUYỆN TẬP ====================

    @GET("practice/history")
    Call<ApiResponse<LichSuLuyenTapResponse>> layLichSuLuyenTap(
            @Header("Authorization") String token,
            @Query("page") int page,
            @Query("limit") int limit
    );

    @GET("practice/statistics")
    Call<ApiResponse<ThongKeLuyenTapResponse>> layThongKeLuyenTap(
            @Header("Authorization") String token
    );


    // ==================== BẢNG XẾP HẠNG ====================
    @GET("leaderboard")
    Call<ApiResponse<BangXepHangResponse>> layBangXepHang(
            @Header("Authorization") String token,
            @Query("loai") String loai
    );


}
