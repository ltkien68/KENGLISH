package com.example.kenglish;

import android.content.ClipData;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.DragEvent;
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

import com.example.kenglish.adapter.BoTuAdapter;
import com.example.kenglish.api.ApiService;
import com.example.kenglish.api.RetrofitClient;
import com.example.kenglish.model.ApiResponse;
import com.example.kenglish.model.BoTuModel;
import com.example.kenglish.model.ChuyenBoTuRequest;
import com.example.kenglish.model.Folder;
import com.example.kenglish.model.MucBoTu;
import com.example.kenglish.model.TaoBoTuRequest;
import com.example.kenglish.model.TaoFolderRequest;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoTu extends Fragment {

    private TextView btnTaoBoTu;
    private LinearLayout btnTaoFolder;
    private RecyclerView listBoTu;
    private RecyclerView listFolderNgang;

    private final List<MucBoTu> danhSachFolder = new ArrayList<>();
    private final List<MucBoTu> danhSachBoTu = new ArrayList<>();

    private BoTuAdapter folderAdapter;
    private BoTuAdapter boTuAdapter;
    private int phienTaiDuLieu = 0;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bo_tu, container, false);

        anhXa(view);
        khoiTaoDanhSach();
        xuLySuKien();
        layDuLieuBoTu();

        return view;
    }

    private void anhXa(View view) {
        btnTaoBoTu = view.findViewById(R.id.component_tao_bo_tu);
        btnTaoFolder = view.findViewById(R.id.component_tao_folder);
        listBoTu = view.findViewById(R.id.list_bo_tu);
        listFolderNgang = view.findViewById(R.id.list_folder_ngang);
    }

    private void xuLySuKien() {
        btnTaoBoTu.setOnClickListener(v -> moPopupTaoBoTu());
        btnTaoFolder.setOnClickListener(v -> moPopupTaoFolder());
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden && getView() != null) {
            layDuLieuBoTu();
        }
    }

    private void khoiTaoDanhSach() {

        BoTuAdapter.OnBoTuDragListener dragListener =
                new BoTuAdapter.OnBoTuDragListener() {

                    @Override
                    public void onBatDauKeo(View view, BoTuModel boTu) {
                        batDauKeoBoTu(view, boTu);
                    }

                    @Override
                    public void onMoFolder(Folder folder) {
                        moChiTietFolder(folder);
                    }

                    @Override
                    public void onThaVaoFolder(BoTuModel boTu, Folder folder) {
                        xuLyThaVaoFolder(boTu, folder);
                    }

                    @Override
                    public void onXemBoTu(BoTuModel boTu) {
                        moChiTietBoTu(boTu);
                    }

                    @Override
                    public void onLuyenTapBoTu(BoTuModel boTu) {
                        if (requireActivity() instanceof MainActivity) {
                            ((MainActivity) requireActivity())
                                    .chuyenDenLuyenTap(
                                            boTu.getId(),
                                            boTu.getTenBoTu()
                                    );
                        }
                    }
                };

        BoTuAdapter.OnXoaBoTuListener xoaListener = this::xoaBoTu;

        folderAdapter = new BoTuAdapter(
                danhSachFolder,
                dragListener,
                xoaListener
        );

        boTuAdapter = new BoTuAdapter(
                danhSachBoTu,
                dragListener,
                xoaListener
        );

        listFolderNgang.setLayoutManager(
                new LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false
                )
        );

        listBoTu.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        int khoangCach = dp(8);

        listFolderNgang.addItemDecoration(
                new RecyclerView.ItemDecoration() {
                    @Override
                    public void getItemOffsets(
                            @NonNull Rect outRect,
                            @NonNull View view,
                            @NonNull RecyclerView parent,
                            @NonNull RecyclerView.State state) {

                        outRect.right = khoangCach;
                    }
                }
        );

        listBoTu.addItemDecoration(
                new RecyclerView.ItemDecoration() {
                    @Override
                    public void getItemOffsets(
                            @NonNull Rect outRect,
                            @NonNull View view,
                            @NonNull RecyclerView parent,
                            @NonNull RecyclerView.State state) {

                        outRect.bottom = khoangCach;
                    }
                }
        );

        listFolderNgang.setAdapter(folderAdapter);
        listBoTu.setAdapter(boTuAdapter);
    }

    private int dp(int giaTri) {
        return (int) (giaTri *
                getResources().getDisplayMetrics().density + 0.5f);
    }

    private String layToken() {
        SharedPreferences preferences =
                requireContext().getSharedPreferences(
                        "Kenglish",
                        Context.MODE_PRIVATE
                );

        return preferences.getString("token", null);
    }

    private ApiService layApiService() {
        return RetrofitClient.getClient().create(ApiService.class);
    }

    private void thongBao(String noiDung) {
        if (isAdded()) {
            Toast.makeText(
                    requireContext(),
                    noiDung,
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void layDuLieuBoTu() {
        if (!isAdded()) {
            return;
        }

        int phien = ++phienTaiDuLieu;

        String token = layToken();

        if (token == null) {
            thongBao("Phiên đăng nhập không hợp lệ");
            return;
        }

        layApiService().layDanhSachFolder("Bearer " + token)
                .enqueue(new Callback<ApiResponse<List<Folder>>>() {

                    @Override
                    public void onResponse(
                            Call<ApiResponse<List<Folder>>> call,
                            Response<ApiResponse<List<Folder>>> response) {

                        if (!isAdded() || phien != phienTaiDuLieu) {
                            return;
                        }

                        List<MucBoTu> ketQua = new ArrayList<>();

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isThanhCong()
                                && response.body().getData() != null) {

                            for (Folder folder : response.body().getData()) {
                                ketQua.add(new MucBoTu(folder));
                            }
                        }

                        danhSachFolder.clear();
                        danhSachFolder.addAll(ketQua);
                        folderAdapter.notifyDataSetChanged();

                        layDanhSachBoTu(phien);
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<List<Folder>>> call,
                            Throwable t) {

                        if (!isAdded() || phien != phienTaiDuLieu) {
                            return;
                        }

                        layDanhSachBoTu(phien);
                    }
                });
    }

    private void layDanhSachBoTu(int phien) {
        String token = layToken();

        if (token == null) {
            return;
        }

        layApiService().layDanhSachBoTu("Bearer " + token)
                .enqueue(new Callback<ApiResponse<List<BoTuModel>>>() {

                    @Override
                    public void onResponse(
                            Call<ApiResponse<List<BoTuModel>>> call,
                            Response<ApiResponse<List<BoTuModel>>> response) {

                        if (!isAdded() || phien != phienTaiDuLieu) {
                            return;
                        }

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isThanhCong()
                                && response.body().getData() != null) {

                            danhSachBoTu.clear();

                            for (BoTuModel boTu : response.body().getData()) {
                                danhSachBoTu.add(new MucBoTu(boTu));
                            }

                            boTuAdapter.notifyDataSetChanged();
                        } else {
                            thongBao("Không thể tải danh sách bộ từ");
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<List<BoTuModel>>> call,
                            Throwable t) {

                        if (!isAdded() || phien != phienTaiDuLieu) {
                            return;
                        }

                        thongBao("Không thể kết nối đến server");
                    }
                });
    }

    private void moChiTietFolder(Folder folder) {
        ChiTietFolder fragment = new ChiTietFolder();

        Bundle bundle = new Bundle();
        bundle.putInt("folder_id", folder.getId());
        bundle.putString("ten_folder", folder.getTenFolder());

        fragment.setArguments(bundle);

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .hide(this)
                .add(
                        R.id.khung_noi_dung,
                        fragment,
                        "CHI_TIET_FOLDER"
                )
                .addToBackStack("CHI_TIET_FOLDER")
                .commit();
    }

    private void moChiTietBoTu(BoTuModel boTu) {
        ChiTietBoTu fragment = new ChiTietBoTu();

        Bundle bundle = new Bundle();

        bundle.putInt("bo_tu_id", boTu.getId());
        bundle.putString("ten_bo_tu", boTu.getTenBoTu());
        bundle.putString("mo_ta", boTu.getMoTa());
        bundle.putInt("so_luong_tu", boTu.getSoLuongTu());
        bundle.putInt("so_tu_da_thuoc", boTu.getSoTuDaThuoc());

        fragment.setArguments(bundle);

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .hide(this)
                .add(
                        R.id.khung_noi_dung,
                        fragment,
                        "CHI_TIET_BO_TU"
                )
                .addToBackStack("CHI_TIET_BO_TU")
                .commit();
    }

    private void batDauKeoBoTu(View view, BoTuModel boTu) {

        ClipData clipData = ClipData.newPlainText(
                "bo_tu_id",
                String.valueOf(boTu.getId())
        );

        View.DragShadowBuilder shadowBuilder =
                new View.DragShadowBuilder(view);

        view.animate()
                .scaleX(0.92f)
                .scaleY(0.92f)
                .alpha(0.45f)
                .setDuration(120)
                .start();

        view.setOnDragListener((v, event) -> {
            if (event.getAction() == DragEvent.ACTION_DRAG_ENDED) {
                v.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .alpha(1f)
                        .setDuration(180)
                        .start();
            }

            return true;
        });

        boolean daBatDau = view.startDragAndDrop(
                clipData,
                shadowBuilder,
                boTu,
                0
        );

        if (!daBatDau) {
            view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .alpha(1f)
                    .start();
        }
    }

    private void xuLyThaVaoFolder(BoTuModel boTu, Folder folder) {
        String token = layToken();

        if (token == null) {
            thongBao("Phiên đăng nhập không hợp lệ");
            return;
        }

        ChuyenBoTuRequest request =
                new ChuyenBoTuRequest(folder.getId());

        layApiService().chuyenBoTuVaoFolder(
                "Bearer " + token,
                boTu.getId(),
                request
        ).enqueue(new Callback<ApiResponse<Object>>() {

            @Override
            public void onResponse(
                    Call<ApiResponse<Object>> call,
                    Response<ApiResponse<Object>> response) {

                if (!isAdded()) {
                    return;
                }

                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().isThanhCong()) {

                    capNhatSauKhiChuyen(boTu, folder);
                    thongBao("Đã chuyển vào " + folder.getTenFolder());

                } else {
                    String thongBaoLoi = "Không thể chuyển bộ từ";

                    if (response.body() != null
                            && response.body().getThongBao() != null) {
                        thongBaoLoi = response.body().getThongBao();
                    }

                    thongBao(thongBaoLoi);
                }
            }

            @Override
            public void onFailure(
                    Call<ApiResponse<Object>> call,
                    Throwable t) {

                thongBao("Không thể kết nối đến server");
            }
        });
    }

    private void capNhatSauKhiChuyen(
            BoTuModel boTu,
            Folder folder) {

        int viTriBoTu = -1;
        int viTriFolder = -1;

        for (int i = 0; i < danhSachBoTu.size(); i++) {
            MucBoTu muc = danhSachBoTu.get(i);

            if (muc.getBoTu().getId() == boTu.getId()) {
                viTriBoTu = i;
                break;
            }
        }

        for (int i = 0; i < danhSachFolder.size(); i++) {
            MucBoTu muc = danhSachFolder.get(i);

            if (muc.getFolder().getId() == folder.getId()) {
                viTriFolder = i;
                break;
            }
        }

        if (viTriBoTu == -1 || viTriFolder == -1) {
            layDuLieuBoTu();
            return;
        }

        danhSachFolder.get(viTriFolder)
                .getFolder()
                .tangSoBoTu();

        folderAdapter.notifyItemChanged(viTriFolder);

        danhSachBoTu.remove(viTriBoTu);
        boTuAdapter.notifyItemRemoved(viTriBoTu);
    }

    private void xoaBoTu(BoTuModel boTu) {
        String token = layToken();

        if (token == null) {
            thongBao("Phiên đăng nhập không hợp lệ");
            return;
        }

        layApiService().xoaBoTu(
                "Bearer " + token,
                boTu.getId()
        ).enqueue(new Callback<ApiResponse<Object>>() {

            @Override
            public void onResponse(
                    Call<ApiResponse<Object>> call,
                    Response<ApiResponse<Object>> response) {

                if (!isAdded()) {
                    return;
                }

                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().isThanhCong()) {

                    capNhatSauKhiXoaBoTu(boTu);
                    thongBao("Đã xóa bộ từ");

                } else {
                    String loi = "Không thể xóa bộ từ";

                    if (response.body() != null
                            && response.body().getThongBao() != null) {
                        loi = response.body().getThongBao();
                    }

                    thongBao(loi);
                }
            }

            @Override
            public void onFailure(
                    Call<ApiResponse<Object>> call,
                    Throwable t) {

                thongBao("Không thể kết nối đến server");
            }
        });
    }

    private void capNhatSauKhiXoaBoTu(BoTuModel boTu) {
        for (int i = 0; i < danhSachBoTu.size(); i++) {
            MucBoTu muc = danhSachBoTu.get(i);

            if (muc.getBoTu().getId() == boTu.getId()) {
                danhSachBoTu.remove(i);
                boTuAdapter.notifyItemRemoved(i);
                return;
            }
        }
    }

    private void moPopupTaoBoTu() {
        BottomSheetDialog dialog =
                new BottomSheetDialog(requireContext());

        View popupView = getLayoutInflater().inflate(
                R.layout.botu_popup_taobotu,
                null
        );

        dialog.setContentView(popupView);

        EditText edtTenBoTu =
                popupView.findViewById(R.id.edt_ten_bo_tu);

        EditText edtMoTaBoTu =
                popupView.findViewById(R.id.edt_mo_ta_bo_tu);

        TextView btnXacNhan =
                popupView.findViewById(R.id.btn_xac_nhan_tao_bo_tu);

        btnXacNhan.setOnClickListener(v -> {
            String tenBoTu = edtTenBoTu.getText()
                    .toString().trim();

            String moTa = edtMoTaBoTu.getText()
                    .toString().trim();

            if (tenBoTu.isEmpty()) {
                edtTenBoTu.setError("Vui lòng nhập tên bộ từ");
                edtTenBoTu.requestFocus();
                return;
            }

            taoBoTu(tenBoTu, moTa, dialog);
        });

        dialog.show();
        moBanPhim(edtTenBoTu, dialog);
    }

    private void moPopupTaoFolder() {
        BottomSheetDialog dialog =
                new BottomSheetDialog(requireContext());

        View popupView = getLayoutInflater().inflate(
                R.layout.botu_popup_taofolder,
                null
        );

        dialog.setContentView(popupView);

        EditText edtTenFolder =
                popupView.findViewById(R.id.edt_ten_folder);

        TextView btnXacNhan =
                popupView.findViewById(R.id.btn_xac_nhan_tao_folder);

        btnXacNhan.setOnClickListener(v -> {
            String tenFolder = edtTenFolder.getText()
                    .toString().trim();

            if (tenFolder.isEmpty()) {
                edtTenFolder.setError("Vui lòng nhập tên folder");
                edtTenFolder.requestFocus();
                return;
            }

            taoFolder(tenFolder, dialog);
        });

        dialog.show();
        moBanPhim(edtTenFolder, dialog);
    }

    private void moBanPhim(
            EditText editText,
            BottomSheetDialog dialog) {

        editText.requestFocus();

        Window window = dialog.getWindow();

        if (window != null) {
            window.setSoftInputMode(
                    WindowManager.LayoutParams
                            .SOFT_INPUT_STATE_ALWAYS_VISIBLE
            );
        }
    }

    private void taoBoTu(
            String tenBoTu,
            String moTa,
            BottomSheetDialog dialog) {

        String token = layToken();

        if (token == null) {
            thongBao("Phiên đăng nhập không hợp lệ");
            return;
        }

        TaoBoTuRequest request =
                new TaoBoTuRequest(tenBoTu, moTa, null);

        layApiService().taoBoTu(
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
                        && response.body() != null
                        && response.body().isThanhCong()) {

                    dialog.dismiss();
                    thongBao("Đã tạo bộ từ: " + tenBoTu);
                    layDuLieuBoTu();

                } else {
                    String loi = "Không thể tạo bộ từ";

                    if (response.body() != null
                            && response.body().getThongBao() != null) {
                        loi = response.body().getThongBao();
                    }

                    thongBao(loi);
                }
            }

            @Override
            public void onFailure(
                    Call<ApiResponse<BoTuModel>> call,
                    Throwable t) {

                thongBao("Không thể kết nối đến server");
            }
        });
    }

    private void taoFolder(
            String tenFolder,
            BottomSheetDialog dialog) {

        String token = layToken();

        if (token == null) {
            thongBao("Phiên đăng nhập không hợp lệ");
            return;
        }

        TaoFolderRequest request =
                new TaoFolderRequest(tenFolder);

        layApiService().taoFolder(
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
                        && response.body() != null
                        && response.body().isThanhCong()) {

                    dialog.dismiss();
                    thongBao("Đã tạo folder: " + tenFolder);
                    layDuLieuBoTu();

                } else {
                    String loi = "Không thể tạo folder";

                    if (response.body() != null
                            && response.body().getThongBao() != null) {
                        loi = response.body().getThongBao();
                    }

                    thongBao(loi);
                }
            }

            @Override
            public void onFailure(
                    Call<ApiResponse<Folder>> call,
                    Throwable t) {

                thongBao("Không thể kết nối đến server");
            }
        });
    }
}
