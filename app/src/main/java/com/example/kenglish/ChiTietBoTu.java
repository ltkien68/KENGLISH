package com.example.kenglish;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
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
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    private TextView btnHocNgay;


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


        androidx.core.widget.NestedScrollView scrollChiTiet =
                view.findViewById(R.id.scroll_chi_tiet_bo_tu);

        scrollChiTiet.setDescendantFocusability(
                ViewGroup.FOCUS_BEFORE_DESCENDANTS
        );

        scrollChiTiet.setFocusableInTouchMode(true);
        scrollChiTiet.requestFocus();

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


        btnHocNgay = view.findViewById(R.id.btn_hoc_ngay);

        View layoutTimKiem = (View) edtTimTu.getParent();

        edtTimTu.setOnFocusChangeListener((v, hasFocus) -> {
            layoutTimKiem.setBackgroundResource(
                    hasFocus
                            ? R.drawable.nen_botu_timkiem_focus
                            : R.drawable.nen_botu_timkiem
            );
        });

    }


    /*
     * =========================================
     * NHẬN DỮ LIỆU TỪ MÀN HÌNH TRƯỚC
     * =========================================
     */


    private void capNhatChieuCaoDanhSach() {
        if (listTuVung == null) {
            return;
        }

        ListAdapter adapter = listTuVung.getAdapter();

        if (adapter == null) {
            return;
        }

        int soLuong = adapter.getCount();

        if (soLuong == 0) {
            ViewGroup.LayoutParams params = listTuVung.getLayoutParams();
            params.height = 0;
            listTuVung.setLayoutParams(params);
            return;
        }

        int chieuRong = listTuVung.getWidth();

        if (chieuRong <= 0) {
            listTuVung.post(this::capNhatChieuCaoDanhSach);
            return;
        }

        int widthSpec = View.MeasureSpec.makeMeasureSpec(
                chieuRong,
                View.MeasureSpec.EXACTLY
        );

        int heightSpec = View.MeasureSpec.makeMeasureSpec(
                0,
                View.MeasureSpec.UNSPECIFIED
        );

        int tongChieuCao = 0;

        View itemCu = null;
        int loaiItemCu = -1;

        for (int i = 0; i < soLuong; i++) {
            int loaiItem = adapter.getItemViewType(i);

            if (loaiItem != loaiItemCu) {
                itemCu = null;
                loaiItemCu = loaiItem;
            }

            View item = adapter.getView(
                    i,
                    itemCu,
                    listTuVung
            );

            item.measure(widthSpec, heightSpec);

            tongChieuCao += item.getMeasuredHeight();

            itemCu = item;
        }

        tongChieuCao += listTuVung.getDividerHeight()
                * Math.max(0, soLuong - 1);

        tongChieuCao += listTuVung.getPaddingTop()
                + listTuVung.getPaddingBottom();

        ViewGroup.LayoutParams params = listTuVung.getLayoutParams();

        if (params.height != tongChieuCao) {
            params.height = tongChieuCao;
            listTuVung.setLayoutParams(params);
        }
    }


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
                android.graphics.Color.parseColor("#087CFF")
        );
    }


    private void khoiTaoDanhSachTu() {
        tuVungAdapter = new TuVungAdapter(
                requireContext(),
                danhSachHienThi,
                tuVung -> xoaTu(tuVung),
                tuVung -> capNhatTrangThaiTu(tuVung)
        );

        listTuVung.setAdapter(tuVungAdapter);

        listTuVung.setNestedScrollingEnabled(false);

        tuVungAdapter.registerDataSetObserver(
                new DataSetObserver() {
                    @Override
                    public void onChanged() {
                        listTuVung.post(
                                () -> {
                                    if (isAdded() && getView() != null) {
                                        capNhatChieuCaoDanhSach();
                                    }
                                }
                        );
                    }

                    @Override
                    public void onInvalidated() {
                        onChanged();
                    }
                }
        );

        listTuVung.post(this::capNhatChieuCaoDanhSach);
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

        Dialog dialog = new Dialog(requireContext());

        dialog.setContentView(R.layout.botu_popup_themtu);

        Window window = dialog.getWindow();

        if (window != null) {
            window.setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT)
            );
        }

        // Ánh xạ giao diện
        ImageView btnDongPopup = dialog.findViewById(R.id.btn_dong_popup);
        TextView txtBoTuHienTai = dialog.findViewById(R.id.txt_bo_tu_hien_tai);

        EditText edtTuVung = dialog.findViewById(R.id.edt_tu_vung);
        EditText edtPhienAm = dialog.findViewById(R.id.edt_phien_am);
        AutoCompleteTextView edtNghia = dialog.findViewById(R.id.edt_nghia);

        TextView btnHuy = dialog.findViewById(R.id.btn_huy);
        TextView btnLuuTu = dialog.findViewById(R.id.btn_luu_tu);
        TextView btnThemLoaiTu = dialog.findViewById(R.id.btn_them_loai_tu);

        TextView txtGoiYLoaiTu = dialog.findViewById(R.id.txt_goi_y_loai_tu);

        txtBoTuHienTai.setText(tenBoTu);

        // Lưu các loại từ đang được chọn
        Set<String> danhSachLoaiTuChon = new LinkedHashSet<>();

        // Ánh xạ 9 chip có sẵn
        Map<String, TextView> danhSachChip = new LinkedHashMap<>();

        danhSachChip.put("noun", dialog.findViewById(R.id.chip_noun));
        danhSachChip.put("verb", dialog.findViewById(R.id.chip_verb));
        danhSachChip.put("adjective", dialog.findViewById(R.id.chip_adjective));
        danhSachChip.put("adverb", dialog.findViewById(R.id.chip_adverb));
        danhSachChip.put("pronoun", dialog.findViewById(R.id.chip_pronoun));
        danhSachChip.put("preposition", dialog.findViewById(R.id.chip_preposition));
        danhSachChip.put("conjunction", dialog.findViewById(R.id.chip_conjunction));
        danhSachChip.put("interjection", dialog.findViewById(R.id.chip_interjection));
        danhSachChip.put("determiner", dialog.findViewById(R.id.chip_determiner));

        // Xử lý chọn / bỏ chọn chip
        for (Map.Entry<String, TextView> entry : danhSachChip.entrySet()) {

            String loaiTu = entry.getKey();
            TextView chip = entry.getValue();

            chip.setOnClickListener(v -> {

                if (danhSachLoaiTuChon.contains(loaiTu)) {
                    danhSachLoaiTuChon.remove(loaiTu);
                } else {
                    danhSachLoaiTuChon.add(loaiTu);
                }

                capNhatTrangThaiChip(chip, danhSachLoaiTuChon.contains(loaiTu));
            });
        }

        // Tra từ điển khi nhập từ
        Handler handler = new Handler(Looper.getMainLooper());

        Runnable[] tacVuTraTu = new Runnable[1];

        edtTuVung.addTextChangedListener(new TextWatcher() {

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

                if (tacVuTraTu[0] != null) {
                    handler.removeCallbacks(tacVuTraTu[0]);
                }

                String tu = s.toString().trim();

                if (tu.isEmpty()) {

                    edtPhienAm.setText("");
                    edtNghia.setText("");
                    edtNghia.setAdapter(null);

                    danhSachLoaiTuChon.clear();

                    for (TextView chip : danhSachChip.values()) {
                        capNhatTrangThaiChip(chip, false);
                    }

                    txtGoiYLoaiTu.setText("GỢI Ý TỪ ĐIỂN");

                    return;
                }

                tacVuTraTu[0] = () -> traTuChoPopup(
                        tu,
                        edtPhienAm,
                        edtNghia,
                        danhSachChip,
                        danhSachLoaiTuChon,
                        txtGoiYLoaiTu
                );

                handler.postDelayed(tacVuTraTu[0], 500);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Xóa nhanh từ vựng
        ImageView imgXoaTu = dialog.findViewById(R.id.img_icon_xoa_tu);

        imgXoaTu.setOnClickListener(v -> {
            edtTuVung.setText("");
            edtTuVung.requestFocus();
        });

        // Thêm loại từ tùy chỉnh
        btnThemLoaiTu.setOnClickListener(v -> {

            EditText edtLoaiTuMoi = new EditText(requireContext());

            edtLoaiTuMoi.setHint("Ví dụ: phrasal verb");
            edtLoaiTuMoi.setSingleLine(true);
            edtLoaiTuMoi.setPadding(32, 20, 32, 20);

            new AlertDialog.Builder(requireContext())
                    .setTitle("Thêm loại từ")
                    .setMessage("Nhập tên loại từ muốn thêm")
                    .setView(edtLoaiTuMoi)
                    .setNegativeButton("Hủy", null)
                    .setPositiveButton("Thêm", (dialogThem, which) -> {

                        String loaiTuMoi = edtLoaiTuMoi.getText()
                                .toString()
                                .trim();

                        if (loaiTuMoi.isEmpty()) {
                            Toast.makeText(
                                    requireContext(),
                                    "Vui lòng nhập loại từ",
                                    Toast.LENGTH_SHORT
                            ).show();
                            return;
                        }

                        String khoa = loaiTuMoi.toLowerCase();

                        if (danhSachLoaiTuChon.contains(khoa)
                                || danhSachChip.containsKey(khoa)) {

                            Toast.makeText(
                                    requireContext(),
                                    "Loại từ này đã tồn tại",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        themChipTuyChinh(
                                loaiTuMoi,
                                khoa,
                                danhSachChip,
                                danhSachLoaiTuChon,
                                btnThemLoaiTu
                        );
                    })
                    .show();
        });

        // Đóng popup
        btnDongPopup.setOnClickListener(v -> dialog.dismiss());
        btnHuy.setOnClickListener(v -> dialog.dismiss());

        // Lưu từ
        btnLuuTu.setOnClickListener(v -> {

            String tuVung = edtTuVung.getText().toString().trim();
            String phienAm = edtPhienAm.getText().toString().trim();
            String nghia = edtNghia.getText().toString().trim();

            if (tuVung.isEmpty()) {
                edtTuVung.setError("Vui lòng nhập từ vựng");
                edtTuVung.requestFocus();
                return;
            }

            if (nghia.isEmpty()) {
                edtNghia.setError("Vui lòng nhập nghĩa");
                edtNghia.requestFocus();
                return;
            }

            // Ghép các loại từ thành chuỗi
            String loaiTu = String.join(",", danhSachLoaiTuChon);

            luuTuVaoServer(
                    dialog,
                    tuVung,
                    phienAm,
                    nghia,
                    loaiTu
            );
        });

        dialog.show();

        if (dialog.getWindow() != null) {

            int chieuRong = (int) (getResources()
                    .getDisplayMetrics().widthPixels * 0.84f);

            dialog.getWindow().setLayout(
                    chieuRong,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }

    private void capNhatTrangThaiChip(TextView chip, boolean daChon) {

        int mauNen;
        int mauChu;
        int mauVien;

        // Xác định bảng màu theo từng loại từ
        int id = chip.getId();

        if (id == R.id.chip_noun) {
            mauNen = Color.parseColor("#FCE8E8");
            mauChu = Color.parseColor("#C24141");
            mauVien = Color.parseColor("#E76B6B");

        } else if (id == R.id.chip_verb) {
            mauNen = Color.parseColor("#E1F0FF");
            mauChu = Color.parseColor("#1763B1");
            mauVien = Color.parseColor("#3989DF");

        } else if (id == R.id.chip_adjective) {
            mauNen = Color.parseColor("#F1E8FF");
            mauChu = Color.parseColor("#7541B8");
            mauVien = Color.parseColor("#9864D8");

        } else if (id == R.id.chip_adverb) {
            mauNen = Color.parseColor("#E0F5ED");
            mauChu = Color.parseColor("#16845C");
            mauVien = Color.parseColor("#35AC7E");

        } else if (id == R.id.chip_pronoun) {
            mauNen = Color.parseColor("#FFF0D9");
            mauChu = Color.parseColor("#A96813");
            mauVien = Color.parseColor("#D99636");

        } else if (id == R.id.chip_preposition) {
            mauNen = Color.parseColor("#E0F3F5");
            mauChu = Color.parseColor("#167B88");
            mauVien = Color.parseColor("#36A5B1");

        } else if (id == R.id.chip_conjunction) {
            mauNen = Color.parseColor("#FCE6F1");
            mauChu = Color.parseColor("#B33C79");
            mauVien = Color.parseColor("#D9679F");

        } else if (id == R.id.chip_interjection) {
            mauNen = Color.parseColor("#FFF5D9");
            mauChu = Color.parseColor("#98710C");
            mauVien = Color.parseColor("#D6AD32");

        } else if (id == R.id.chip_determiner) {
            mauNen = Color.parseColor("#E9ECFF");
            mauChu = Color.parseColor("#4C5EB4");
            mauVien = Color.parseColor("#7183D9");

        } else {
            // Màu mặc định cho loại từ tùy chỉnh
            mauNen = Color.parseColor("#E1F0FF");
            mauChu = Color.parseColor("#1763B1");
            mauVien = Color.parseColor("#3989DF");
        }

        // Tạo nền chip
        GradientDrawable nen = new GradientDrawable();

        nen.setShape(GradientDrawable.RECTANGLE);
        nen.setCornerRadius(dpToPx(9));

        if (daChon) {
            nen.setColor(mauNen);
            nen.setStroke(dpToPx(2), mauVien);

            chip.setTextColor(mauChu);
            chip.setTypeface(null, Typeface.BOLD);

        } else {
            nen.setColor(mauNen);
            nen.setStroke(dpToPx(1), Color.TRANSPARENT);

            chip.setTextColor(mauChu);
            chip.setTypeface(null, Typeface.NORMAL);
        }

        chip.setBackground(nen);
        chip.setSelected(daChon);
    }

    private void themChipTuyChinh(
            String tenHienThi,
            String khoa,
            Map<String, TextView> danhSachChip,
            Set<String> danhSachLoaiTuChon,
            TextView btnThemLoaiTu) {

        TextView chip = new TextView(requireContext());

        chip.setText(tenHienThi);
        chip.setTextSize(10);
        chip.setGravity(android.view.Gravity.CENTER);
        chip.setPadding(24, 0, 24, 0);
        capNhatTrangThaiChip(chip, false);
        chip.setClickable(true);
        chip.setFocusable(true);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        dpToPx(30)
                );

        params.topMargin = dpToPx(5);

        ViewGroup parent = (ViewGroup) btnThemLoaiTu.getParent();

        int viTri = parent.indexOfChild(btnThemLoaiTu);

        parent.addView(chip, viTri, params);

        danhSachChip.put(khoa, chip);

        chip.setOnClickListener(v -> {

            if (danhSachLoaiTuChon.contains(khoa)) {
                danhSachLoaiTuChon.remove(khoa);
            } else {
                danhSachLoaiTuChon.add(khoa);
            }

            capNhatTrangThaiChip(
                    chip,
                    danhSachLoaiTuChon.contains(khoa)
            );
        });
    }

    private int dpToPx(float dp) {
        return (int) (dp * getResources()
                .getDisplayMetrics().density + 0.5f);
    }

    private void traTuChoPopup(
            String tu,
            EditText edtPhienAm,
            AutoCompleteTextView edtNghia,
            Map<String, TextView> danhSachChip,
            Set<String> danhSachLoaiTuChon,
            TextView txtGoiYLoaiTu) {

        ApiService apiService = RetrofitClient
                .getClient()
                .create(ApiService.class);

        apiService.traTu(tu).enqueue(
                new Callback<DictionaryResponse>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<DictionaryResponse> call,
                            @NonNull Response<DictionaryResponse> response) {

                        if (!response.isSuccessful()
                                || response.body() == null) {
                            return;
                        }

                        DictionaryResponse data = response.body();

                        // Phiên âm
                        if (data.getIpa() != null) {
                            edtPhienAm.setText(data.getIpa());
                        }

                        // Danh sách nghĩa
                        List<String> danhSachNghia = new ArrayList<>();

                        if (data.getMeanings() != null) {

                            for (Meaning meaning : data.getMeanings()) {

                                if (meaning.getDefinition() != null
                                        && !danhSachNghia.contains(
                                        meaning.getDefinition())) {

                                    danhSachNghia.add(
                                            meaning.getDefinition()
                                    );
                                }
                            }
                        }

                        ArrayAdapter<String> adapterNghia =
                                new ArrayAdapter<>(
                                        requireContext(),
                                        android.R.layout.simple_dropdown_item_1line,
                                        danhSachNghia
                                );

                        edtNghia.setAdapter(adapterNghia);

                        edtNghia.setOnClickListener(v -> {
                            if (!danhSachNghia.isEmpty()) {
                                edtNghia.showDropDown();
                            }
                        });

                        edtNghia.setOnFocusChangeListener((v, hasFocus) -> {
                            if (hasFocus && !danhSachNghia.isEmpty()) {
                                edtNghia.showDropDown();
                            }
                        });

                        // Tự động nhận diện loại từ
                        danhSachLoaiTuChon.clear();

                        for (TextView chip : danhSachChip.values()) {
                            capNhatTrangThaiChip(chip, false);
                        }

                        boolean coLoaiTu = false;

                        if (data.getMeanings() != null) {

                            for (Meaning meaning : data.getMeanings()) {

                                String pos = meaning.getPos();

                                android.util.Log.d("Kenglish_POS", "POS nhận được: " + pos);

                                if (pos == null || pos.trim().isEmpty()) {
                                    continue;
                                }

                                String khoa = chuanHoaLoaiTu(pos);

                                TextView chip = danhSachChip.get(khoa);

                                if (chip != null) {

                                    danhSachLoaiTuChon.add(khoa);

                                    capNhatTrangThaiChip(chip, true);

                                    coLoaiTu = true;
                                }
                            }
                        }

                        txtGoiYLoaiTu.setText(
                                coLoaiTu
                                        ? "ĐÃ NHẬN DIỆN"
                                        : "CHƯA NHẬN DIỆN"
                        );
                    }

                    private String chuanHoaLoaiTu(String pos) {

                        if (pos == null) {
                            return "";
                        }

                        String loaiTu = pos.trim().toLowerCase();

                        switch (loaiTu) {

                            case "danh từ":
                            case "danh tu":
                                return "noun";

                            case "động từ":
                            case "dong tu":
                                return "verb";

                            case "tính từ":
                            case "tinh tu":
                                return "adjective";

                            case "trạng từ":
                            case "trang tu":
                                return "adverb";

                            case "đại từ":
                            case "dai tu":
                                return "pronoun";

                            case "giới từ":
                            case "gioi tu":
                                return "preposition";

                            case "liên từ":
                            case "lien tu":
                                return "conjunction";

                            case "thán từ":
                            case "than tu":
                                return "interjection";

                            case "từ hạn định":
                            case "tu han dinh":
                                return "determiner";

                            default:
                                return loaiTu;
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<DictionaryResponse> call,
                            @NonNull Throwable t) {

                        // Người dùng vẫn có thể nhập thủ công
                    }
                }
        );
    }

    private void luuTuVaoServer(
            Dialog dialog,
            String tuVung,
            String phienAm,
            String nghia,
            String loaiTu) {


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
        ThemTuRequest request = new ThemTuRequest(
                tuVung,
                phienAm.isEmpty() ? null : phienAm,
                loaiTu.isEmpty() ? null : loaiTu,
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


        btnHocNgay.setOnClickListener(v -> {

            if (boTuId <= 0) {
                Toast.makeText(
                        requireContext(),
                        "Không xác định được bộ từ",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            if (requireActivity() instanceof MainActivity) {

                ((MainActivity) requireActivity())
                        .chuyenDenLuyenTap(
                                boTuId,
                                tenBoTu
                        );
            }
        });


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