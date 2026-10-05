package com.example.kenglish;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.kenglish.model.CapNhatTrangThaiTuRequest;
import com.example.kenglish.model.DanhSachTuResponse;
import com.example.kenglish.model.DictionaryResponse;
import com.example.kenglish.model.Meaning;
import com.example.kenglish.model.ThemTuRequest;
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

    private ImageView btnThemTu;

    private EditText edtTimTu;


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

        btnThemTu =
                view.findViewById(
                        R.id.btn_them_tu
                );

        edtTimTu =
                view.findViewById(
                        R.id.edt_tim_tu
                );
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
                        danhSachHienThi,

                        // Swipe / bấm X
                        tuVung -> xoaTu(
                                tuVung
                        ),

                        // Bấm Đã thuộc / Chưa thuộc
                        tuVung -> capNhatTrangThaiTu(
                                tuVung
                        )
                );


        listTuVung.setAdapter(
                tuVungAdapter
        );
    }

    private void xoaTu(
            TuVung tuVung) {


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


        ApiService apiService =
                RetrofitClient
                        .getClient()
                        .create(
                                ApiService.class
                        );


        apiService
                .xoaTu(
                        "Bearer " + token,
                        boTuId,
                        tuVung.getId()
                )
                .enqueue(
                        new Callback<ApiResponse<Object>>() {


                            @Override
                            public void onResponse(
                                    @NonNull Call<ApiResponse<Object>> call,
                                    @NonNull Response<ApiResponse<Object>> response) {


                                /*
                                 * HTTP lỗi.
                                 */
                                if (!response.isSuccessful()) {

                                    Toast.makeText(
                                            requireContext(),
                                            "Không thể xóa từ",
                                            Toast.LENGTH_SHORT
                                    ).show();


                                    /*
                                     * Reset lại card vì
                                     * server chưa xóa.
                                     */
                                    tuVungAdapter
                                            .notifyDataSetChanged();


                                    return;
                                }


                                ApiResponse<Object> apiResponse =
                                        response.body();


                                /*
                                 * API báo thất bại.
                                 */
                                if (apiResponse == null
                                        || !apiResponse.isThanhCong()) {


                                    Toast.makeText(
                                            requireContext(),
                                            apiResponse != null
                                                    ? apiResponse.getThongBao()
                                                    : "Không thể xóa từ",
                                            Toast.LENGTH_SHORT
                                    ).show();


                                    tuVungAdapter
                                            .notifyDataSetChanged();


                                    return;
                                }


                                /*
                                 * =================================
                                 * XÓA THÀNH CÔNG
                                 * =================================
                                 *
                                 * Không tự đoán dữ liệu local.
                                 * Lấy lại danh sách chuẩn từ DB.
                                 */

                                layDanhSachTu();


                                Toast.makeText(
                                        requireContext(),
                                        "Đã xóa từ",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }


                            @Override
                            public void onFailure(
                                    @NonNull Call<ApiResponse<Object>> call,
                                    @NonNull Throwable t) {


                                /*
                                 * Nếu request lỗi thì card
                                 * trở về trạng thái ban đầu.
                                 */
                                tuVungAdapter
                                        .notifyDataSetChanged();


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

    private void moPopupThemTu() {

        Dialog dialog =
                new Dialog(
                        requireContext()
                );

        dialog.setContentView(
                R.layout.botu_popup_themtu
        );


        /*
         * Nền ngoài popup trong suốt
         */
        Window window =
                dialog.getWindow();

        if (window != null) {

            window.setBackgroundDrawable(
                    new ColorDrawable(
                            Color.TRANSPARENT
                    )
            );

            window.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }


        /*
         * =========================================
         * ÁNH XẠ
         * =========================================
         */

        ImageView btnDongPopup =
                dialog.findViewById(
                        R.id.btn_dong_popup
                );


        TextView txtBoTuHienTai =
                dialog.findViewById(
                        R.id.txt_bo_tu_hien_tai
                );


        EditText edtTuVung =
                dialog.findViewById(
                        R.id.edt_tu_vung
                );


        EditText edtPhienAm =
                dialog.findViewById(
                        R.id.edt_phien_am
                );


        AutoCompleteTextView edtNghia =
                dialog.findViewById(
                        R.id.edt_nghia
                );


        TextView btnHuy =
                dialog.findViewById(
                        R.id.btn_huy
                );


        TextView btnLuuTu =
                dialog.findViewById(
                        R.id.btn_luu_tu
                );

        /*
         * =========================================
         * TỰ ĐỘNG TRA TỪ
         * =========================================
         */

        Handler handler =
                new Handler(
                        Looper.getMainLooper()
                );


        Runnable[] tacVuTraTu =
                new Runnable[1];


        edtTuVung.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after) {
                    }


                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count) {

                        /*
                         * Nếu trước đó đang chờ tra
                         * thì hủy.
                         */
                        if (tacVuTraTu[0] != null) {

                            handler.removeCallbacks(
                                    tacVuTraTu[0]
                            );
                        }


                        String tu =
                                s.toString()
                                        .trim();


                        /*
                         * Người dùng xóa từ:
                         * xóa luôn dữ liệu tự động.
                         */
                        if (tu.isEmpty()) {

                            edtPhienAm.setText("");

                            edtNghia.setAdapter(null);

                            return;
                        }


                        /*
                         * Chờ 500ms sau lần gõ cuối.
                         */
                        tacVuTraTu[0] =
                                () -> traTuChoPopup(
                                        tu,
                                        edtPhienAm,
                                        edtNghia
                                );


                        handler.postDelayed(
                                tacVuTraTu[0],
                                500
                        );
                    }


                    @Override
                    public void afterTextChanged(
                            Editable s) {
                    }
                }
        );


        /*
         * Hiển thị bộ từ hiện tại.
         */
        txtBoTuHienTai.setText(
                tenBoTu
        );


        /*
         * =========================================
         * ĐÓNG POPUP
         * =========================================
         */

        btnDongPopup.setOnClickListener(
                v -> dialog.dismiss()
        );


        btnHuy.setOnClickListener(
                v -> dialog.dismiss()
        );


        /*
         * =========================================
         * LƯU TỪ
         * =========================================
         */

        btnLuuTu.setOnClickListener(v -> {

            String tuVung =
                    edtTuVung
                            .getText()
                            .toString()
                            .trim();


            String phienAm =
                    edtPhienAm
                            .getText()
                            .toString()
                            .trim();


            String nghia =
                    edtNghia
                            .getText()
                            .toString()
                            .trim();


            /*
             * Từ và nghĩa là bắt buộc.
             */
            if (tuVung.isEmpty()) {

                edtTuVung.setError(
                        "Vui lòng nhập từ vựng"
                );

                edtTuVung.requestFocus();

                return;
            }


            if (nghia.isEmpty()) {

                edtNghia.setError(
                        "Vui lòng nhập nghĩa"
                );

                edtNghia.requestFocus();

                return;
            }


            luuTuVaoServer(
                    dialog,
                    tuVung,
                    phienAm,
                    nghia
            );
        });


        /*
         * Hiển thị popup.
         */
        dialog.show();


        /*
         * Phải set width sau show()
         */
        if (dialog.getWindow() != null) {

            dialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }

    private void traTuChoPopup(
            String tu,
            EditText edtPhienAm,
            AutoCompleteTextView edtNghia) {

        ApiService apiService =
                RetrofitClient
                        .getClient()
                        .create(
                                ApiService.class
                        );


        apiService
                .traTu(tu)
                .enqueue(
                        new Callback<DictionaryResponse>() {

                            @Override
                            public void onResponse(
                                    @NonNull Call<DictionaryResponse> call,
                                    @NonNull Response<DictionaryResponse> response) {

                                if (!response.isSuccessful()
                                        || response.body() == null) {

                                    return;
                                }


                                DictionaryResponse data =
                                        response.body();


                                /*
                                 * =========================
                                 * PHIÊN ÂM
                                 * =========================
                                 */

                                if (data.getIpa() != null) {

                                    edtPhienAm.setText(
                                            data.getIpa()
                                    );
                                }


                                /*
                                 * =========================
                                 * DANH SÁCH NGHĨA
                                 * =========================
                                 */

                                List<String> danhSachNghia =
                                        new ArrayList<>();


                                if (data.getMeanings() != null) {

                                    for (Meaning meaning
                                            : data.getMeanings()) {

                                        if (meaning.getDefinition()
                                                != null) {

                                            danhSachNghia.add(
                                                    meaning.getDefinition()
                                            );
                                        }
                                    }
                                }


                                /*
                                 * Đưa danh sách nghĩa
                                 * vào dropdown.
                                 */

                                ArrayAdapter<String> adapterNghia =
                                        new ArrayAdapter<>(
                                                requireContext(),
                                                android.R.layout
                                                        .simple_dropdown_item_1line,
                                                danhSachNghia
                                        );


                                edtNghia.setAdapter(
                                        adapterNghia
                                );


                                /*
                                 * Bấm vào ô nghĩa
                                 * → xổ danh sách.
                                 */
                                edtNghia.setOnClickListener(v -> {

                                    if (!danhSachNghia.isEmpty()) {

                                        edtNghia.showDropDown();
                                    }
                                });


                                edtNghia.setOnFocusChangeListener(
                                        (v, hasFocus) -> {

                                            if (hasFocus
                                                    && !danhSachNghia.isEmpty()) {

                                                edtNghia.showDropDown();
                                            }
                                        }
                                );
                            }


                            @Override
                            public void onFailure(
                                    @NonNull Call<DictionaryResponse> call,
                                    @NonNull Throwable t) {

                                /*
                                 * Không Toast ở đây.
                                 *
                                 * Tra tự động thất bại thì
                                 * người dùng vẫn có thể
                                 * tự nhập phiên âm + nghĩa.
                                 */
                            }
                        }
                );
    }

    private void luuTuVaoServer(
            Dialog dialog,
            String tuVung,
            String phienAm,
            String nghia) {


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
         * Tạo request.
         *
         * Hiện popup mới có:
         * - từ
         * - phiên âm
         * - nghĩa
         *
         * Các trường khác để null.
         */
        ThemTuRequest request =
                new ThemTuRequest(
                        tuVung,
                        phienAm.isEmpty()
                                ? null
                                : phienAm,
                        null,
                        nghia,
                        null,
                        null,
                        null
                );


        ApiService apiService =
                RetrofitClient
                        .getClient()
                        .create(
                                ApiService.class
                        );


        apiService
                .themTu(
                        "Bearer " + token,
                        boTuId,
                        request
                )
                .enqueue(
                        new Callback<ApiResponse<TuVung>>() {

                            @Override
                            public void onResponse(
                                    @NonNull Call<ApiResponse<TuVung>> call,
                                    @NonNull Response<ApiResponse<TuVung>> response) {


                                if (!response.isSuccessful()) {

                                    Toast.makeText(
                                            requireContext(),
                                            "Không thể thêm từ",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    return;
                                }


                                ApiResponse<TuVung> apiResponse =
                                        response.body();


                                if (apiResponse == null
                                        || !apiResponse.isThanhCong()) {

                                    Toast.makeText(
                                            requireContext(),
                                            apiResponse != null
                                                    ? apiResponse.getThongBao()
                                                    : "Không thể thêm từ",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    return;
                                }


                                /*
                                 * Thành công
                                 */
                                dialog.dismiss();


                                Toast.makeText(
                                        requireContext(),
                                        "Đã thêm từ",
                                        Toast.LENGTH_SHORT
                                ).show();


                                /*
                                 * Load lại danh sách từ từ DB.
                                 *
                                 * Hàm này đã có sẵn và cũng
                                 * cập nhật thống kê.
                                 */
                                layDanhSachTu();
                            }


                            @Override
                            public void onFailure(
                                    @NonNull Call<ApiResponse<TuVung>> call,
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

        btnThemTu.setOnClickListener(
                v -> moPopupThemTu()
        );

        edtTimTu.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after) {
                    }


                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count) {

                        timKiemTu(
                                s.toString()
                        );
                    }


                    @Override
                    public void afterTextChanged(
                            Editable s) {
                    }
                }
        );
    }

    private void timKiemTu(
            String tuKhoa) {

        /*
         * Chuẩn hóa từ khóa.
         */
        String tuKhoaTim =
                tuKhoa
                        .trim()
                        .toLowerCase();


        /*
         * Xóa danh sách đang hiển thị.
         *
         * Không đụng vào danhSachTu
         * vì đây là danh sách gốc từ API.
         */
        danhSachHienThi.clear();


        /*
         * Không nhập gì
         * → hiện lại toàn bộ.
         */
        if (tuKhoaTim.isEmpty()) {

            danhSachHienThi.addAll(
                    danhSachTu
            );

            tuVungAdapter
                    .notifyDataSetChanged();

            return;
        }


        /*
         * Tìm trong toàn bộ từ của bộ từ.
         */
        for (TuVung tuVung : danhSachTu) {

            String tuGoc =
                    tuVung.getTu_goc();

            String nghia =
                    tuVung.getNghia_tieng_viet();


            /*
             * Tìm theo từ tiếng Anh.
             */
            boolean khopTuGoc =
                    tuGoc != null
                            && tuGoc
                            .toLowerCase()
                            .contains(
                                    tuKhoaTim
                            );


            /*
             * Tìm theo nghĩa tiếng Việt.
             */
            boolean khopNghia =
                    nghia != null
                            && nghia
                            .toLowerCase()
                            .contains(
                                    tuKhoaTim
                            );


            if (khopTuGoc
                    || khopNghia) {

                danhSachHienThi.add(
                        tuVung
                );
            }
        }


        /*
         * Vẽ lại ListView.
         */
        tuVungAdapter
                .notifyDataSetChanged();
    }

    private void capNhatTrangThaiTu(
            TuVung tuVung) {


        /*
         * =====================================
         * TRẠNG THÁI MỚI
         * =====================================
         *
         * Đang thuộc     → 0
         * Chưa thuộc     → 1
         */

        int trangThaiMoi =
                tuVung.isDa_thuoc()
                        ? 0
                        : 1;


        /*
         * =====================================
         * TOKEN
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
         * REQUEST BODY
         * =====================================
         */

        CapNhatTrangThaiTuRequest request =
                new CapNhatTrangThaiTuRequest(
                        trangThaiMoi
                );


        /*
         * =====================================
         * API
         * =====================================
         */

        ApiService apiService =
                RetrofitClient
                        .getClient()
                        .create(
                                ApiService.class
                        );


        apiService
                .capNhatTrangThaiTu(
                        "Bearer " + token,
                        boTuId,
                        tuVung.getId(),
                        request
                )
                .enqueue(
                        new Callback<ApiResponse<Object>>() {


                            @Override
                            public void onResponse(
                                    @NonNull Call<ApiResponse<Object>> call,
                                    @NonNull Response<ApiResponse<Object>> response) {


                                /*
                                 * HTTP lỗi
                                 */
                                if (!response.isSuccessful()) {

                                    Toast.makeText(
                                            requireContext(),
                                            "Không thể cập nhật trạng thái",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    return;
                                }


                                ApiResponse<Object> apiResponse =
                                        response.body();


                                /*
                                 * Backend báo lỗi
                                 */
                                if (apiResponse == null
                                        || !apiResponse.isThanhCong()) {


                                    Toast.makeText(
                                            requireContext(),
                                            apiResponse != null
                                                    ? apiResponse.getThongBao()
                                                    : "Không thể cập nhật trạng thái",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    return;
                                }


                                /*
                                 * =================================
                                 * CẬP NHẬT LOCAL
                                 * =================================
                                 */

                                tuVung.setDa_thuoc(
                                        trangThaiMoi
                                );


                                /*
                                 * Vẽ lại badge.
                                 */
                                tuVungAdapter
                                        .notifyDataSetChanged();


                                /*
                                 * Cập nhật:
                                 *
                                 * Tổng từ
                                 * Đã thuộc
                                 * Chưa thuộc
                                 */
                                capNhatThongKe();
                            }


                            @Override
                            public void onFailure(
                                    @NonNull Call<ApiResponse<Object>> call,
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

}