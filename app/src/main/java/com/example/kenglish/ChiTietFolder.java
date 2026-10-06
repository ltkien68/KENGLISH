package com.example.kenglish;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kenglish.adapter.BoTuFolderAdapter;
import com.example.kenglish.api.ApiService;
import com.example.kenglish.api.RetrofitClient;
import com.example.kenglish.model.ApiResponse;
import com.example.kenglish.model.BoTuModel;
import com.example.kenglish.model.ChiTietFolderResponse;
import com.example.kenglish.model.Folder;
import com.example.kenglish.model.TaoBoTuRequest;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChiTietFolder extends Fragment {


    /*
     * =========================================
     * VIEW
     * =========================================
     */

    private View btnQuayLai;

    private TextView txtTenFolder;
    private TextView txtSoBoTu;
    private TextView btnTaoBoTu;

    private RecyclerView listBoTuFolder;

    private LinearLayout layoutFolderTrong;

    private TextView txtTieuDeBoTu;

    private ImageView btnTimKiem;
    private ImageView btnDongTimKiem;

    private LinearLayout layoutTimKiem;

    private EditText edtTimBoTu;


    /*
     * =========================================
     * DỮ LIỆU
     * =========================================
     */

    /*
     * Danh sách gốc lấy từ server.
     */
    private final List<BoTuModel> danhSachBoTu =
            new ArrayList<>();


    /*
     * Danh sách đang hiển thị trên RecyclerView.
     *
     * Khi tìm kiếm, chỉ thay đổi danh sách này.
     * Không thay đổi danhSachBoTu.
     */
    private final List<BoTuModel> danhSachHienThi =
            new ArrayList<>();


    private BoTuFolderAdapter adapter;

    private int folderId = -1;

    private String tenFolder = "Folder";


    /*
     * =========================================
     * KHỞI TẠO
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
                        R.layout.botu_chitietfolder,
                        container,
                        false
                );

        anhXa(view);

        layDuLieuTruyenSang();

        khoiTaoDanhSach();

        hienThiThongTinFolder();

        xuLySuKien();

        layBoTuTrongFolder();

        return view;
    }


    /*
     * =========================================
     * ÁNH XẠ
     * =========================================
     */

    private void anhXa(View view) {

        btnQuayLai =
                view.findViewById(
                        R.id.btn_quay_lai
                );

        txtTenFolder =
                view.findViewById(
                        R.id.txt_ten_folder
                );

        txtSoBoTu =
                view.findViewById(
                        R.id.txt_so_bo_tu
                );

        btnTaoBoTu =
                view.findViewById(
                        R.id.btn_tao_bo_tu
                );

        listBoTuFolder =
                view.findViewById(
                        R.id.list_bo_tu_folder
                );

        layoutFolderTrong =
                view.findViewById(
                        R.id.layout_folder_trong
                );

        txtTieuDeBoTu =
                view.findViewById(
                        R.id.txt_tieu_de_bo_tu
                );

        btnTimKiem =
                view.findViewById(
                        R.id.btn_tim_kiem
                );

        layoutTimKiem =
                view.findViewById(
                        R.id.layout_tim_kiem
                );

        edtTimBoTu =
                view.findViewById(
                        R.id.edt_tim_bo_tu
                );

        btnDongTimKiem =
                view.findViewById(
                        R.id.btn_dong_tim_kiem
                );
    }


    /*
     * =========================================
     * NHẬN DỮ LIỆU FOLDER
     * =========================================
     */

    private void layDuLieuTruyenSang() {

        Bundle arguments =
                getArguments();

        if (arguments == null) {
            return;
        }

        folderId =
                arguments.getInt(
                        "folder_id",
                        -1
                );

        tenFolder =
                arguments.getString(
                        "ten_folder",
                        "Folder"
                );
    }


    private void hienThiThongTinFolder() {

        txtTenFolder.setText(
                tenFolder
        );
    }


    /*
     * =========================================
     * SỰ KIỆN
     * =========================================
     */

    private void xuLySuKien() {

        /*
         * Quay lại màn Bộ từ.
         */
        btnQuayLai.setOnClickListener(
                v -> requireActivity()
                        .getSupportFragmentManager()
                        .popBackStack()
        );


        /*
         * Tạo bộ từ ngay trong Folder.
         */
        btnTaoBoTu.setOnClickListener(
                v -> moPopupTaoBoTu()
        );

        btnTimKiem.setOnClickListener(v -> {

            txtTieuDeBoTu.setVisibility(
                    View.GONE
            );

            btnTimKiem.setVisibility(
                    View.GONE
            );

            layoutTimKiem.setVisibility(
                    View.VISIBLE
            );

            layoutTimKiem.setAlpha(
                    0f
            );

            layoutTimKiem.animate()
                    .alpha(1f)
                    .setDuration(180)
                    .start();

            edtTimBoTu.requestFocus();

            InputMethodManager inputMethodManager =
                    (InputMethodManager)
                            requireContext()
                                    .getSystemService(
                                            Context.INPUT_METHOD_SERVICE
                                    );

            inputMethodManager.showSoftInput(
                    edtTimBoTu,
                    InputMethodManager.SHOW_IMPLICIT
            );
        });

        btnDongTimKiem.setOnClickListener(v -> {

            edtTimBoTu.setText("");

            layoutTimKiem.setVisibility(
                    View.GONE
            );

            txtTieuDeBoTu.setVisibility(
                    View.VISIBLE
            );

            btnTimKiem.setVisibility(
                    View.VISIBLE
            );


            InputMethodManager inputMethodManager =
                    (InputMethodManager)
                            requireContext()
                                    .getSystemService(
                                            Context.INPUT_METHOD_SERVICE
                                    );

            inputMethodManager.hideSoftInputFromWindow(
                    edtTimBoTu.getWindowToken(),
                    0
            );
        });

        edtTimBoTu.addTextChangedListener(
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

                        timKiemBoTu(
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

    private void timKiemBoTu(String tuKhoa) {

        String tuKhoaTim =
                tuKhoa
                        .trim()
                        .toLowerCase();


        /*
         * Xóa dữ liệu đang hiển thị.
         */
        danhSachHienThi.clear();


        /*
         * Không có từ khóa
         * → hiện lại toàn bộ bộ từ.
         */
        if (tuKhoaTim.isEmpty()) {

            danhSachHienThi.addAll(
                    danhSachBoTu
            );

        } else {

            /*
             * Tìm theo tên bộ từ.
             */
            for (BoTuModel boTu : danhSachBoTu) {

                String tenBoTu =
                        boTu.getTenBoTu();


                if (tenBoTu != null
                        && tenBoTu
                        .toLowerCase()
                        .contains(tuKhoaTim)) {

                    danhSachHienThi.add(
                            boTu
                    );
                }
            }
        }


        /*
         * Cập nhật RecyclerView.
         */
        adapter.notifyDataSetChanged();


        /*
         * Xử lý trường hợp không có kết quả.
         */
        if (danhSachHienThi.isEmpty()) {

            listBoTuFolder.setVisibility(
                    View.GONE
            );

            layoutFolderTrong.setVisibility(
                    View.VISIBLE
            );

        } else {

            listBoTuFolder.setVisibility(
                    View.VISIBLE
            );

            layoutFolderTrong.setVisibility(
                    View.GONE
            );
        }
    }


    /*
     * =========================================
     * RECYCLER VIEW
     * =========================================
     */

    private void khoiTaoDanhSach() {

        /*
         * Adapter chỉ hiển thị danhSachHienThi.
         *
         * danhSachBoTu giữ dữ liệu gốc.
         */
        adapter =
                new BoTuFolderAdapter(
                        danhSachHienThi,

                        new BoTuFolderAdapter.OnBoTuClickListener() {

                            @Override
                            public void onXemBoTu(
                                    BoTuModel boTu) {

                                moChiTietBoTu(
                                        boTu
                                );
                            }


                            @Override
                            public void onXoaBoTu(
                                    BoTuModel boTu) {

                                xoaBoTu(
                                        boTu
                                );
                            }
                        }
                );


        listBoTuFolder.setLayoutManager(
                new LinearLayoutManager(
                        requireContext()
                )
        );


        int khoangCach =
                (int) (
                        8
                                * getResources()
                                .getDisplayMetrics()
                                .density
                );


        listBoTuFolder.addItemDecoration(
                new RecyclerView.ItemDecoration() {

                    @Override
                    public void getItemOffsets(
                            @NonNull Rect outRect,
                            @NonNull View view,
                            @NonNull RecyclerView parent,
                            @NonNull RecyclerView.State state) {

                        outRect.bottom =
                                khoangCach;
                    }
                }
        );


        listBoTuFolder.setAdapter(
                adapter
        );
    }

    /*
     * =========================================
     * XÓA BỘ TỪ
     * =========================================
     */

    private void xoaBoTu(
            BoTuModel boTu) {

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
                .xoaBoTu(
                        "Bearer " + token,
                        boTu.getId()
                )
                .enqueue(
                        new Callback<ApiResponse<Object>>() {

                            @Override
                            public void onResponse(
                                    Call<ApiResponse<Object>> call,
                                    Response<ApiResponse<Object>> response) {

                                if (!isAdded()) {

                                    return;
                                }


                                if (response.isSuccessful()
                                        && response.body() != null
                                        && response.body()
                                        .isThanhCong()) {


                                    capNhatSauKhiXoaBoTu(
                                            boTu
                                    );


                                    Toast.makeText(
                                            requireContext(),
                                            "Đã xóa bộ từ",
                                            Toast.LENGTH_SHORT
                                    ).show();


                                } else {

                                    String thongBao =
                                            "Không thể xóa bộ từ";


                                    if (response.body() != null
                                            && response.body()
                                            .getThongBao() != null) {

                                        thongBao =
                                                response.body()
                                                        .getThongBao();
                                    }


                                    Toast.makeText(
                                            requireContext(),
                                            thongBao,
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            }


                            @Override
                            public void onFailure(
                                    Call<ApiResponse<Object>> call,
                                    Throwable t) {

                                if (!isAdded()) {

                                    return;
                                }


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
     * CẬP NHẬT SAU KHI XÓA BỘ TỪ
     * =========================================
     */

    private void capNhatSauKhiXoaBoTu(
            BoTuModel boTu) {


        /*
         * Xóa khỏi danh sách gốc.
         */
        for (int i =
             danhSachBoTu.size() - 1;
             i >= 0;
             i--) {

            if (danhSachBoTu
                    .get(i)
                    .getId()
                    == boTu.getId()) {

                danhSachBoTu.remove(
                        i
                );

                break;
            }
        }


        /*
         * Tìm vị trí trong danh sách
         * đang hiển thị.
         *
         * Quan trọng vì người dùng
         * có thể đang tìm kiếm.
         */
        int viTriHienThi =
                -1;


        for (int i = 0;
             i < danhSachHienThi.size();
             i++) {

            if (danhSachHienThi
                    .get(i)
                    .getId()
                    == boTu.getId()) {

                viTriHienThi =
                        i;

                break;
            }
        }


        /*
         * Nếu Bộ từ đang hiển thị
         * thì xóa đúng card đó.
         */
        if (viTriHienThi != -1) {

            danhSachHienThi.remove(
                    viTriHienThi
            );


            adapter.notifyItemRemoved(
                    viTriHienThi
            );
        }


        /*
         * Cập nhật:
         *
         * - số bộ từ
         * - trạng thái Folder trống
         */
        capNhatGiaoDien();
    }


    /*
     * =========================================
     * MỞ CHI TIẾT BỘ TỪ
     * =========================================
     */

    private void moChiTietBoTu(
            BoTuModel boTu) {

        ChiTietBoTu fragment =
                new ChiTietBoTu();


        /*
         * Chưa cần query database từ vựng.
         *
         * Tạm thời truyền thông tin đang có
         * trong BoTuModel sang màn ChiTietBoTu.
         */
        Bundle bundle =
                new Bundle();


        bundle.putInt(
                "bo_tu_id",
                boTu.getId()
        );


        bundle.putString(
                "ten_bo_tu",
                boTu.getTenBoTu()
        );


        bundle.putString(
                "mo_ta",
                boTu.getMoTa()
        );


        bundle.putInt(
                "so_luong_tu",
                boTu.getSoLuongTu()
        );


        bundle.putInt(
                "so_tu_da_thuoc",
                boTu.getSoTuDaThuoc()
        );


        fragment.setArguments(
                bundle
        );


        /*
         * Ẩn ChiTietFolder.
         *
         * Không replace để khi Back
         * có thể quay lại đúng Folder cũ.
         */
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()

                .hide(this)

                .add(
                        R.id.khung_noi_dung,
                        fragment,
                        "CHI_TIET_BO_TU"
                )

                .addToBackStack(
                        "CHI_TIET_BO_TU"
                )

                .commit();
    }


    /*
     * =========================================
     * GET BỘ TỪ TRONG FOLDER
     * =========================================
     */

    private void layBoTuTrongFolder() {

        if (folderId == -1) {

            Toast.makeText(
                    requireContext(),
                    "Không tìm thấy thư mục",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


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
                .layBoTuTheoFolder(
                        "Bearer " + token,
                        folderId
                )
                .enqueue(
                        new Callback<ApiResponse<ChiTietFolderResponse>>() {

                            @Override
                            public void onResponse(
                                    Call<ApiResponse<ChiTietFolderResponse>> call,
                                    Response<ApiResponse<ChiTietFolderResponse>> response) {

                                if (!isAdded()) {
                                    return;
                                }


                                if (response.isSuccessful()
                                        && response.body() != null
                                        && response.body().isThanhCong()
                                        && response.body().getData() != null) {


                                    ChiTietFolderResponse duLieu =
                                            response.body()
                                                    .getData();


                                    /*
                                     * Folder thật từ database.
                                     */
                                    Folder folder =
                                            duLieu.getFolder();


                                    if (folder != null) {

                                        tenFolder =
                                                folder.getTenFolder();

                                        txtTenFolder.setText(
                                                tenFolder
                                        );
                                    }


                                    /*
                                     * Danh sách bộ từ.
                                     */
                                    /*
                                     * Danh sách gốc.
                                     */
                                    danhSachBoTu.clear();


                                    List<BoTuModel> danhSachTuServer =
                                            duLieu.getBoTu();


                                    if (danhSachTuServer != null) {

                                        danhSachBoTu.addAll(
                                                danhSachTuServer
                                        );
                                    }


                                    /*
                                     * Danh sách hiển thị ban đầu
                                     * giống danh sách gốc.
                                     */
                                    danhSachHienThi.clear();

                                    danhSachHienThi.addAll(
                                            danhSachBoTu
                                    );


                                    adapter.notifyDataSetChanged();


                                    capNhatGiaoDien();


                                } else {

                                    String thongBao =
                                            "Không thể tải bộ từ";


                                    if (response.body() != null
                                            && response.body()
                                            .getThongBao() != null) {

                                        thongBao =
                                                response.body()
                                                        .getThongBao();
                                    }


                                    Toast.makeText(
                                            requireContext(),
                                            thongBao,
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            }


                            @Override
                            public void onFailure(
                                    Call<ApiResponse<ChiTietFolderResponse>> call,
                                    Throwable t) {

                                if (!isAdded()) {
                                    return;
                                }


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
     * POPUP TẠO BỘ TỪ
     * =========================================
     */

    private void moPopupTaoBoTu() {

        BottomSheetDialog dialog =
                new BottomSheetDialog(
                        requireContext()
                );


        /*
         * Dùng lại popup tạo bộ từ
         * của màn Bộ từ chính.
         */
        View popupView =
                getLayoutInflater()
                        .inflate(
                                R.layout.botu_popup_taobotu,
                                null
                        );


        dialog.setContentView(
                popupView
        );


        EditText edtTenBoTu =
                popupView.findViewById(
                        R.id.edt_ten_bo_tu
                );


        EditText edtMoTaBoTu =
                popupView.findViewById(
                        R.id.edt_mo_ta_bo_tu
                );


        TextView btnXacNhan =
                popupView.findViewById(
                        R.id.btn_xac_nhan_tao_bo_tu
                );


        btnXacNhan.setOnClickListener(
                v -> {

                    String tenBoTu =
                            edtTenBoTu
                                    .getText()
                                    .toString()
                                    .trim();


                    String moTa =
                            edtMoTaBoTu
                                    .getText()
                                    .toString()
                                    .trim();


                    if (tenBoTu.isEmpty()) {

                        edtTenBoTu.setError(
                                "Vui lòng nhập tên bộ từ"
                        );

                        edtTenBoTu.requestFocus();

                        return;
                    }


                    taoBoTuTrongFolder(
                            tenBoTu,
                            moTa,
                            dialog
                    );
                }
        );


        dialog.show();


        moBanPhim(
                edtTenBoTu,
                dialog
        );
    }


    /*
     * =========================================
     * POST TẠO BỘ TỪ TRONG FOLDER
     * =========================================
     */

    private void taoBoTuTrongFolder(
            String tenBoTu,
            String moTa,
            BottomSheetDialog dialog) {


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
         * Màn Bộ từ chính:
         *
         * folder_id = null
         *
         *
         * Màn ChiTietFolder:
         *
         * folder_id = folderId hiện tại
         */
        TaoBoTuRequest request =
                new TaoBoTuRequest(
                        tenBoTu,
                        moTa,
                        folderId
                );


        ApiService apiService =
                RetrofitClient
                        .getClient()
                        .create(
                                ApiService.class
                        );


        apiService
                .taoBoTu(
                        "Bearer " + token,
                        request
                )
                .enqueue(
                        new Callback<ApiResponse<BoTuModel>>() {

                            @Override
                            public void onResponse(
                                    Call<ApiResponse<BoTuModel>> call,
                                    Response<ApiResponse<BoTuModel>> response) {

                                if (!isAdded()) {
                                    return;
                                }


                                if (response.isSuccessful()
                                        && response.body() != null
                                        && response.body().isThanhCong()) {


                                    dialog.dismiss();


                                    Toast.makeText(
                                            requireContext(),
                                            "Đã tạo bộ từ: "
                                                    + tenBoTu,
                                            Toast.LENGTH_SHORT
                                    ).show();


                                    /*
                                     * Load lại đúng Folder hiện tại.
                                     */
                                    layBoTuTrongFolder();


                                } else {

                                    String thongBao =
                                            "Không thể tạo bộ từ";


                                    if (response.body() != null
                                            && response.body()
                                            .getThongBao() != null) {

                                        thongBao =
                                                response.body()
                                                        .getThongBao();
                                    }


                                    Toast.makeText(
                                            requireContext(),
                                            thongBao,
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            }


                            @Override
                            public void onFailure(
                                    Call<ApiResponse<BoTuModel>> call,
                                    Throwable t) {

                                if (!isAdded()) {
                                    return;
                                }


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
     * BÀN PHÍM
     * =========================================
     */

    private void moBanPhim(
            EditText editText,
            BottomSheetDialog dialog) {

        editText.requestFocus();


        Window window =
                dialog.getWindow();


        if (window != null) {

            window.setSoftInputMode(
                    WindowManager.LayoutParams
                            .SOFT_INPUT_STATE_ALWAYS_VISIBLE
            );
        }
    }


    /*
     * =========================================
     * CẬP NHẬT GIAO DIỆN
     * =========================================
     */

    private void capNhatGiaoDien() {

        int soBoTu =
                danhSachBoTu.size();


        txtSoBoTu.setText(
                soBoTu + " bộ từ"
        );


        if (soBoTu == 0) {

            listBoTuFolder.setVisibility(
                    View.GONE
            );

            layoutFolderTrong.setVisibility(
                    View.VISIBLE
            );

        } else {

            listBoTuFolder.setVisibility(
                    View.VISIBLE
            );

            layoutFolderTrong.setVisibility(
                    View.GONE
            );
        }
    }

}