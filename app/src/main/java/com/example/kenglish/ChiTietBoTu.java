package com.example.kenglish;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.kenglish.adapter.TuVungAdapter;
import com.example.kenglish.api.ApiService;
import com.example.kenglish.api.RetrofitClient;
import com.example.kenglish.model.ApiResponse;
import com.example.kenglish.model.DanhSachTuResponse;
import com.example.kenglish.model.TuVung;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChiTietBoTu extends Fragment {


    /*
     * =========================================
     * VIEW
     * =========================================
     */

    private View btnQuayLai;

    private TextView txtTenBoTu;
    private TextView txtMoTaBoTu;
    private TextView txtTongTu;
    private TextView btnTatCa;
    private TextView btnDaThuoc;
    private TextView btnChuaThuoc;
    private TextView txtDaThuoc;
    private TextView txtChuaThuoc;

    private ListView listTuVung;

    private TuVungAdapter tuVungAdapter;


    /*
     * =========================================
     * DỮ LIỆU BỘ TỪ
     * =========================================
     */

    private int boTuId = -1;

    private String tenBoTu = "";

    private String moTa = "";


    /*
     * Danh sách từ lấy từ database.
     */
    private final List<TuVung> danhSachTu =
            new ArrayList<>();

    private final List<TuVung> danhSachHienThi =
            new ArrayList<>();

    /*
     * =========================================
     * TẠO GIAO DIỆN
     * =========================================
     */

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view =
                inflater.inflate(
                        R.layout.botu_chitietbotu,
                        container,
                        false
                );


        anhXa(view);


        /*
         * Lấy boTuId, tên, mô tả
         * được màn hình trước truyền sang.
         */
        layDuLieuTruyenSang();


        /*
         * Hiển thị trước tên + mô tả.
         */
        hienThiThongTinBoTu();


        /*
         * Lấy dữ liệu thật từ database.
         */
        layDanhSachTu();

        khoiTaoDanhSachTu();


        xuLySuKien();


        return view;
    }


    /*
     * =========================================
     * ÁNH XẠ VIEW
     * =========================================
     */

    private void anhXa(
            View view) {

        btnQuayLai =
                view.findViewById(
                        R.id.btn_quay_lai
                );


        txtTenBoTu =
                view.findViewById(
                        R.id.txt_ten_bo_tu
                );


        txtMoTaBoTu =
                view.findViewById(
                        R.id.txt_mo_ta_bo_tu
                );


        txtTongTu =
                view.findViewById(
                        R.id.txt_tong_tu
                );


        txtDaThuoc =
                view.findViewById(
                        R.id.txt_da_thuoc
                );


        txtChuaThuoc =
                view.findViewById(
                        R.id.txt_chua_thuoc
                );

        listTuVung =
                view.findViewById(
                        R.id.list_tu_vung
                );

        btnTatCa = view.findViewById(R.id.btn_tat_ca);

        btnChuaThuoc = view.findViewById(R.id.btn_chua_thuoc);

        btnDaThuoc = view.findViewById(R.id.btn_da_thuoc);
    }


    /*
     * =========================================
     * NHẬN DỮ LIỆU TỪ MÀN HÌNH TRƯỚC
     * =========================================
     */

    private void layDuLieuTruyenSang() {

        Bundle arguments =
                getArguments();


        if (arguments == null) {
            return;
        }


        /*
         * ID này là quan trọng nhất.
         *
         * Ví dụ:
         * boTuId = 3
         *
         * thì API sẽ gọi:
         *
         * GET /vocabulary/3/words
         */
        boTuId =
                arguments.getInt(
                        "bo_tu_id",
                        -1
                );


        tenBoTu =
                arguments.getString(
                        "ten_bo_tu",
                        "Bộ từ"
                );


        moTa =
                arguments.getString(
                        "mo_ta",
                        ""
                );
    }


    /*
     * =========================================
     * HIỂN THỊ THÔNG TIN BỘ TỪ
     * =========================================
     */

    private void hienThiThongTinBoTu() {

        txtTenBoTu.setText(
                tenBoTu
        );


        if (moTa == null
                || moTa.trim().isEmpty()) {

            txtMoTaBoTu.setText(
                    "Theo dõi tiến độ học tập của bộ từ"
            );

        } else {

            txtMoTaBoTu.setText(
                    moTa
            );
        }
    }


    /*
     * =========================================
     * LẤY DANH SÁCH TỪ TỪ DATABASE
     * =========================================
     */

    private void layDanhSachTu() {


        /*
         * Không có ID Bộ từ
         * thì không thể gọi API.
         */
        if (boTuId == -1) {

            Toast.makeText(
                    requireContext(),
                    "Không xác định được bộ từ",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        /*
         * =====================================
         * LẤY TOKEN
         * =====================================
         */

        SharedPreferences sharedPreferences =
                requireContext()
                        .getSharedPreferences(
                                "Kenglish",
                                Context.MODE_PRIVATE
                        );


        String token =
                sharedPreferences.getString(
                        "token",
                        null
                );


        if (token == null) {

            Toast.makeText(
                    requireContext(),
                    "Phiên đăng nhập không hợp lệ",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        /*
         * =====================================
         * TẠO API SERVICE
         * =====================================
         */

        ApiService apiService =
                RetrofitClient
                        .getClient()
                        .create(
                                ApiService.class
                        );


        /*
         * =====================================
         * GỌI API
         * =====================================
         *
         * Ví dụ boTuId = 3:
         *
         * GET /vocabulary/3/words
         */
        apiService
                .layDanhSachTu(
                        "Bearer " + token,
                        boTuId
                )
                .enqueue(
                        new Callback<ApiResponse<DanhSachTuResponse>>() {

                            @Override
                            public void onResponse(
                                    @NonNull Call<ApiResponse<DanhSachTuResponse>> call,
                                    @NonNull Response<ApiResponse<DanhSachTuResponse>> response) {


                                /*
                                 * Response HTTP không thành công.
                                 */
                                if (!response.isSuccessful()) {

                                    Toast.makeText(
                                            requireContext(),
                                            "Không thể tải danh sách từ",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    return;
                                }


                                /*
                                 * Lấy body JSON
                                 * đã được Gson chuyển thành Java Object.
                                 */
                                ApiResponse<DanhSachTuResponse> apiResponse =
                                        response.body();


                                if (apiResponse == null
                                        || !apiResponse.isThanhCong()
                                        || apiResponse.getData() == null) {

                                    Toast.makeText(
                                            requireContext(),
                                            "Không thể tải danh sách từ",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    return;
                                }


                                /*
                                 * =================================
                                 * LẤY DATA
                                 * =================================
                                 */

                                DanhSachTuResponse data =
                                        apiResponse.getData();


                                danhSachTu.clear();

                                if (data.getTuVung() != null) {

                                    danhSachTu.addAll(
                                            data.getTuVung()
                                    );
                                }


                                /*
                                 * Mặc định khi vừa mở màn hình:
                                 * hiển thị tất cả.
                                 */
                                hienThiTatCa();


                                /*
                                 * Thống kê vẫn tính trên
                                 * danh sách gốc.
                                 */
                                capNhatThongKe();
                            }


                            @Override
                            public void onFailure(
                                    @NonNull Call<ApiResponse<DanhSachTuResponse>> call,
                                    @NonNull Throwable t) {

                                Toast.makeText(
                                        requireContext(),
                                        "Không thể kết nối đến server",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                );
    }


    /*
     * =========================================
     * CẬP NHẬT THỐNG KÊ
     * =========================================
     */

    private void capNhatThongKe() {


        /*
         * Tổng số từ chính là số phần tử
         * trong danh sách lấy từ database.
         */
        int tongSoTu =
                danhSachTu.size();


        int soTuDaThuoc = 0;


        /*
         * Duyệt từng từ.
         */
        for (TuVung tuVung : danhSachTu) {


            /*
             * Nếu da_thuoc = true
             * thì tăng số từ đã thuộc.
             */
            if (tuVung.isDa_thuoc()) {

                soTuDaThuoc++;
            }
        }


        int soTuChuaThuoc =
                tongSoTu - soTuDaThuoc;


        /*
         * =====================================
         * ĐƯA SỐ LIỆU LÊN GIAO DIỆN
         * =====================================
         */

        txtTongTu.setText(
                String.valueOf(
                        tongSoTu
                )
        );


        txtDaThuoc.setText(
                String.valueOf(
                        soTuDaThuoc
                )
        );


        txtChuaThuoc.setText(
                String.valueOf(
                        soTuChuaThuoc
                )
        );
    }

    private void capNhatGiaoDienFilter(TextView filterDangChon) {

        // Reset tất cả về trạng thái mặc định
        btnTatCa.setBackgroundResource(
                R.drawable.nen_filter
        );

        btnTatCa.setTextColor(
                android.graphics.Color.parseColor("#7C8492")
        );


        btnDaThuoc.setBackgroundResource(
                R.drawable.nen_filter
        );

        btnDaThuoc.setTextColor(
                android.graphics.Color.parseColor("#7C8492")
        );


        btnChuaThuoc.setBackgroundResource(
                R.drawable.nen_filter
        );

        btnChuaThuoc.setTextColor(
                android.graphics.Color.parseColor("#7C8492")
        );


        // Filter đang được chọn
        filterDangChon.setBackgroundResource(
                R.drawable.nen_filter_active
        );

        filterDangChon.setTextColor(
                android.graphics.Color.parseColor("#37659C")
        );
    }

    private void khoiTaoDanhSachTu() {

        tuVungAdapter =
                new TuVungAdapter(
                        requireContext(),
                        danhSachHienThi
                );

        listTuVung.setAdapter(
                tuVungAdapter
        );
    }

    /*
     * =========================================
     * FILTER - TẤT CẢ
     * =========================================
     */

    private void hienThiTatCa() {

        danhSachHienThi.clear();

        danhSachHienThi.addAll(
                danhSachTu
        );

        tuVungAdapter.notifyDataSetChanged();
    }


    /*
     * =========================================
     * FILTER - ĐÃ THUỘC
     * =========================================
     */

    private void hienThiDaThuoc() {

        danhSachHienThi.clear();


        for (TuVung tuVung : danhSachTu) {

            if (tuVung.isDa_thuoc()) {

                danhSachHienThi.add(
                        tuVung
                );
            }
        }


        tuVungAdapter.notifyDataSetChanged();
    }


    /*
     * =========================================
     * FILTER - CHƯA THUỘC
     * =========================================
     */

    private void hienThiChuaThuoc() {

        danhSachHienThi.clear();


        for (TuVung tuVung : danhSachTu) {

            if (!tuVung.isDa_thuoc()) {

                danhSachHienThi.add(
                        tuVung
                );
            }
        }


        tuVungAdapter.notifyDataSetChanged();
    }


    /*
     * =========================================
     * SỰ KIỆN
     * =========================================
     */

    private void xuLySuKien() {

        btnQuayLai.setOnClickListener(
                v -> requireActivity()
                        .getSupportFragmentManager()
                        .popBackStack()
        );


        // TẤT CẢ
        btnTatCa.setOnClickListener(v -> {

            hienThiTatCa();

            capNhatGiaoDienFilter(
                    btnTatCa
            );
        });


        // ĐÃ THUỘC
        btnDaThuoc.setOnClickListener(v -> {

            hienThiDaThuoc();

            capNhatGiaoDienFilter(
                    btnDaThuoc
            );
        });


        // CHƯA THUỘC
        btnChuaThuoc.setOnClickListener(v -> {

            hienThiChuaThuoc();

            capNhatGiaoDienFilter(
                    btnChuaThuoc
            );
        });
    }


}