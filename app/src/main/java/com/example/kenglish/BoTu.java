package com.example.kenglish;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.kenglish.api.ApiService;
import com.example.kenglish.api.RetrofitClient;
import com.example.kenglish.model.ApiResponse;
import com.example.kenglish.model.BoTuModel;
import com.example.kenglish.model.Folder;
import com.example.kenglish.model.TaoBoTuRequest;
import com.example.kenglish.model.TaoFolderRequest;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.kenglish.adapter.BoTuAdapter;
import com.example.kenglish.model.MucBoTu;

import java.util.ArrayList;
import java.util.List;


/**
 * Fragment hiển thị màn hình quản lý các bộ từ vựng.
 */
public class BoTu extends Fragment {

    private TextView btnTaoBoTu;
    private LinearLayout btnTaoFolder;
    private ListView listBoTu;

    private final List<MucBoTu> danhSachMuc =
            new ArrayList<>();

    private BoTuAdapter boTuAdapter;


    /**
     * Khởi tạo giao diện của màn hình Bộ từ.
     */
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.bo_tu,
                container,
                false
        );

        anhXa(view);
        khoiTaoDanhSach();
        xuLySuKien();
        layDuLieuBoTu();

        return view;
    }


    /**
     * Ánh xạ các thành phần giao diện.
     */
    private void anhXa(View view) {

        btnTaoBoTu = view.findViewById(
                R.id.component_tao_bo_tu
        );

        btnTaoFolder = view.findViewById(
                R.id.component_tao_folder
        );

        listBoTu =
                view.findViewById(
                        R.id.list_bo_tu
                );
    }


    /**
     * Xử lý các sự kiện của màn hình Bộ từ.
     */
    private void xuLySuKien() {

        btnTaoBoTu.setOnClickListener(v -> {
            moPopupTaoBoTu();
        });

        btnTaoFolder.setOnClickListener(v -> {
            moPopupTaoFolder();
        });
    }

    private void khoiTaoDanhSach() {

        boTuAdapter =
                new BoTuAdapter(
                        requireContext(),
                        danhSachMuc
                );

        listBoTu.setAdapter(
                boTuAdapter
        );
    }

    private void layDuLieuBoTu() {

        danhSachMuc.clear();

        boTuAdapter.notifyDataSetChanged();

        layDanhSachFolder();
    }

    private void layDanhSachFolder() {

        SharedPreferences sharedPreferences =
                requireContext().getSharedPreferences(
                        "Kenglish",
                        Context.MODE_PRIVATE
                );

        String token =
                sharedPreferences.getString(
                        "token",
                        null
                );


        if (token == null) {
            return;
        }


        ApiService apiService =
                RetrofitClient
                        .getClient()
                        .create(ApiService.class);


        apiService
                .layDanhSachFolder(
                        "Bearer " + token
                )
                .enqueue(
                        new Callback<ApiResponse<List<Folder>>>() {

                            @Override
                            public void onResponse(
                                    Call<ApiResponse<List<Folder>>> call,
                                    Response<ApiResponse<List<Folder>>> response) {

                                if (!isAdded()) {
                                    return;
                                }


                                if (response.isSuccessful()
                                        && response.body() != null
                                        && response.body().isThanhCong()) {

                                    List<Folder> danhSachFolder =
                                            response.body().getData();


                                    if (danhSachFolder != null) {

                                        for (Folder folder : danhSachFolder) {

                                            danhSachMuc.add(
                                                    new MucBoTu(
                                                            folder
                                                    )
                                            );
                                        }
                                    }
                                }


                                /*
                                 * Sau khi lấy Folder xong
                                 * thì lấy tiếp Bộ từ.
                                 */
                                layDanhSachBoTu();
                            }


                            @Override
                            public void onFailure(
                                    Call<ApiResponse<List<Folder>>> call,
                                    Throwable t) {

                                if (!isAdded()) {
                                    return;
                                }


                                /*
                                 * Folder lỗi thì vẫn thử
                                 * tải danh sách Bộ từ.
                                 */
                                layDanhSachBoTu();
                            }
                        }
                );
    }

    private void layDanhSachBoTu() {

        SharedPreferences sharedPreferences =
                requireContext().getSharedPreferences(
                        "Kenglish",
                        Context.MODE_PRIVATE
                );

        String token =
                sharedPreferences.getString(
                        "token",
                        null
                );


        if (token == null) {
            return;
        }


        ApiService apiService =
                RetrofitClient
                        .getClient()
                        .create(ApiService.class);


        apiService
                .layDanhSachBoTu(
                        "Bearer " + token
                )
                .enqueue(
                        new Callback<ApiResponse<List<BoTuModel>>>() {

                            @Override
                            public void onResponse(
                                    Call<ApiResponse<List<BoTuModel>>> call,
                                    Response<ApiResponse<List<BoTuModel>>> response) {

                                if (!isAdded()) {
                                    return;
                                }


                                if (response.isSuccessful()
                                        && response.body() != null
                                        && response.body().isThanhCong()) {

                                    List<BoTuModel> danhSachBoTu =
                                            response.body().getData();


                                    if (danhSachBoTu != null) {

                                        for (BoTuModel boTu : danhSachBoTu) {

                                            danhSachMuc.add(
                                                    new MucBoTu(
                                                            boTu
                                                    )
                                            );
                                        }
                                    }
                                }


                                /*
                                 * Báo Adapter rằng dữ liệu
                                 * đã thay đổi.
                                 */
                                boTuAdapter.notifyDataSetChanged();
                            }


                            @Override
                            public void onFailure(
                                    Call<ApiResponse<List<BoTuModel>>> call,
                                    Throwable t) {

                                if (!isAdded()) {
                                    return;
                                }


                                boTuAdapter.notifyDataSetChanged();

                                Toast.makeText(
                                        requireContext(),
                                        "Không thể tải danh sách bộ từ",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                );
    }


    /**
     * Hiển thị popup tạo bộ từ mới.
     */
    private void moPopupTaoBoTu() {

        BottomSheetDialog dialog =
                new BottomSheetDialog(
                        requireContext()
                );

        View popupView =
                getLayoutInflater().inflate(
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


        btnXacNhan.setOnClickListener(v -> {

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


            taoBoTu(
                    tenBoTu,
                    moTa,
                    dialog
            );
        });


        dialog.show();


        moBanPhim(
                edtTenBoTu,
                dialog
        );
    }


    /**
     * Hiển thị popup tạo folder mới.
     */
    private void moPopupTaoFolder() {

        BottomSheetDialog dialog =
                new BottomSheetDialog(
                        requireContext()
                );

        View popupView =
                getLayoutInflater().inflate(
                        R.layout.botu_popup_taofolder,
                        null
                );

        dialog.setContentView(
                popupView
        );


        EditText edtTenFolder =
                popupView.findViewById(
                        R.id.edt_ten_folder
                );

        TextView btnXacNhan =
                popupView.findViewById(
                        R.id.btn_xac_nhan_tao_folder
                );


        btnXacNhan.setOnClickListener(v -> {

            String tenFolder =
                    edtTenFolder
                            .getText()
                            .toString()
                            .trim();


            if (tenFolder.isEmpty()) {

                edtTenFolder.setError(
                        "Vui lòng nhập tên folder"
                );

                edtTenFolder.requestFocus();

                return;
            }


            taoFolder(
                    tenFolder,
                    dialog
            );
        });


        dialog.show();


        moBanPhim(
                edtTenFolder,
                dialog
        );
    }


    /**
     * Tự động focus vào ô nhập liệu
     * và mở bàn phím khi popup được hiển thị.
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


    /**
     * Gửi yêu cầu tạo bộ từ mới lên server.
     *
     * folder_id = null vì bộ từ được tạo
     * trực tiếp từ màn hình Bộ từ.
     */
    private void taoBoTu(
            String tenBoTu,
            String moTa,
            BottomSheetDialog dialog) {

        SharedPreferences sharedPreferences =
                requireContext().getSharedPreferences(
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


        TaoBoTuRequest request =
                new TaoBoTuRequest(
                        tenBoTu,
                        moTa,
                        null
                );


        ApiService apiService =
                RetrofitClient
                        .getClient()
                        .create(ApiService.class);


        apiService.taoBoTu(
                "Bearer " + token,
                request
        ).enqueue(new Callback<ApiResponse<BoTuModel>>() {

            @Override
            public void onResponse(
                    Call<ApiResponse<BoTuModel>> call,
                    Response<ApiResponse<BoTuModel>> response) {

                if (!isAdded()) {
                    return;
                }


                if (response.isSuccessful()
                        && response.body() != null) {

                    ApiResponse<BoTuModel> ketQua =
                            response.body();


                    if (ketQua.isThanhCong()) {

                        Toast.makeText(
                                requireContext(),
                                "Đã tạo bộ từ: " + tenBoTu,
                                Toast.LENGTH_SHORT
                        ).show();

                        dialog.dismiss();

                        layDuLieuBoTu();
                    } else {

                        String thongBao = ketQua.getThongBao();

                        if (thongBao == null || thongBao.isEmpty()) {
                            thongBao = "Có lỗi xảy ra";
                        }

                        Toast.makeText(
                                requireContext(),
                                thongBao,
                                Toast.LENGTH_SHORT
                        ).show();
                    }

                } else {

                    Toast.makeText(
                            requireContext(),
                            "Không thể tạo bộ từ",
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
        });
    }


    /**
     * Gửi yêu cầu tạo folder mới lên server.
     */
    private void taoFolder(
            String tenFolder,
            BottomSheetDialog dialog) {

        SharedPreferences sharedPreferences =
                requireContext().getSharedPreferences(
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


        TaoFolderRequest request =
                new TaoFolderRequest(
                        tenFolder
                );


        ApiService apiService =
                RetrofitClient
                        .getClient()
                        .create(ApiService.class);


        apiService.taoFolder(
                "Bearer " + token,
                request
        ).enqueue(new Callback<ApiResponse<Folder>>() {

            @Override
            public void onResponse(
                    Call<ApiResponse<Folder>> call,
                    Response<ApiResponse<Folder>> response) {

                if (!isAdded()) {
                    return;
                }


                if (response.isSuccessful()
                        && response.body() != null) {

                    ApiResponse<Folder> ketQua =
                            response.body();


                    if (ketQua.isThanhCong()) {

                        Toast.makeText(
                                requireContext(),
                                "Đã tạo folder: " + tenFolder,
                                Toast.LENGTH_SHORT
                        ).show();

                        dialog.dismiss();

                        layDuLieuBoTu();

                    } else {

                        String thongBao = ketQua.getThongBao();

                        if (thongBao == null || thongBao.isEmpty()) {
                            thongBao = "Có lỗi xảy ra";
                        }

                        Toast.makeText(
                                requireContext(),
                                thongBao,
                                Toast.LENGTH_SHORT
                        ).show();
                    }

                } else {

                    Toast.makeText(
                            requireContext(),
                            "Không thể tạo folder",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }


            @Override
            public void onFailure(
                    Call<ApiResponse<Folder>> call,
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
        });
    }
}