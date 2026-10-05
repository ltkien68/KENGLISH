package com.example.kenglish;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
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


    /*
     * =========================================
     * DỮ LIỆU
     * =========================================
     */

    private final List<BoTuModel> danhSachBoTu =
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
    }


    /*
     * =========================================
     * RECYCLER VIEW
     * =========================================
     */

    private void khoiTaoDanhSach() {

        /*
         * Khi người dùng bấm "XEM BỘ TỪ"
         * trong BoTuFolderAdapter,
         * Adapter gửi BoTuModel về đây.
         */
        adapter =
                new BoTuFolderAdapter(
                        danhSachBoTu,
                        boTu -> moChiTietBoTu(
                                boTu
                        )
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
                                    danhSachBoTu.clear();


                                    List<BoTuModel> danhSachTuServer =
                                            duLieu.getBoTu();


                                    if (danhSachTuServer != null) {

                                        danhSachBoTu.addAll(
                                                danhSachTuServer
                                        );
                                    }


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