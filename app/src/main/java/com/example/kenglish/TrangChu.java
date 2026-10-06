package com.example.kenglish;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.kenglish.api.ApiService;
import com.example.kenglish.api.RetrofitClient;
import com.example.kenglish.model.ApiResponse;
import com.example.kenglish.model.LichSuTraTu;
import com.example.kenglish.model.LichSuTraTuResponse;
import com.example.kenglish.model.SuggestionResponse;
import com.example.kenglish.model.ThongKeHocTap;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Fragment hiển thị màn hình Trang chủ.
 */
public class TrangChu extends Fragment {

    /*
     * Khu vực từ đến hạn.
     */
    private LinearLayout layoutTuDenHan;

    /*
     * Thông tin người dùng.
     */
    private TextView txtAnhDaiDien;
    private TextView txtChuoiHoc;
    private TextView btnBoTu;

    private TextView txtTongTu;
    private TextView txtDaThuoc;
    private TextView txtTienDo;
    private TextView txtTuDenHan;


    /*
     * Tra từ.
     */
    private EditText edtTraTu;

    private PopupWindow popupGoiY;

    private ApiService apiService;

    private LinearLayout layoutLichSuTraTu;
    private LinearLayout btnLuyenTapNhanh;
    private  LinearLayout btnTaoBoTuNhanh;
    private LinearLayout btnCongDongNhanh;

    private ImageView btnXoaLichSu;
    private View khungLichSuTraTu;


    /*
     * Dùng để debounce tìm kiếm.
     */
    private final Handler handlerTimKiem =
            new Handler(Looper.getMainLooper());

    private Runnable runnableTimKiem;


    /**
     * Khởi tạo giao diện Trang chủ.
     */
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.trang_chu,
                container,
                false
        );

        anhXa(view);

        layThongKeHocTap();

        thietLapSuKienAnhDaiDien();

        thietLapSuKienTuDenHan();

        thietLapSuKienTaoBoTu();

        thietLapTraTu();

        hienThiChuoiHoc();

        layLichSuTraTu();

        thietLapXoaLichSu();

        thietLapSuKienLuyenTapNhanh();

        thietLapSuKienTaoBoTuNhanh();

        thietLapSuKienCongDongNhanh();

        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
            layThongKeHocTap();
        }
    }


    /**
     * Ánh xạ các thành phần giao diện.
     */
    private void anhXa(View view) {

        /*
         * Thông tin người dùng.
         */
        txtAnhDaiDien =
                view.findViewById(
                        R.id.txt_anh_dai_dien
                );

        txtChuoiHoc =
                view.findViewById(
                        R.id.txt_chuoi_hoc
                );

        btnBoTu =
                view.findViewById(
                        R.id.btn_tao_bo_tu
                );


        /*
         * Khu vực từ đến hạn.
         */
        layoutTuDenHan =
                view.findViewById(
                        R.id.layout_tudenhan
                );


        /*
         * Tra từ.
         */
        edtTraTu =
                view.findViewById(
                        R.id.edt_tra_tu
                );


        /*
         * Retrofit.
         */
        apiService =
                RetrofitClient
                        .getClient()
                        .create(ApiService.class);

        layoutLichSuTraTu =
                view.findViewById(
                        R.id.layout_lich_su_tra_tu
                );

        btnXoaLichSu =
                view.findViewById(
                        R.id.btn_xoa_lich_su
                );

        khungLichSuTraTu =
                view.findViewById(
                        R.id.khung_lich_su_tra_tu
                );

        btnLuyenTapNhanh = view.findViewById(R.id.btn_luyen_tap_nhanh);

        btnTaoBoTuNhanh = view.findViewById(R.id.btn_tao_bo_tu_nhanh);

        btnCongDongNhanh = view.findViewById(R.id.btn_cong_dong);

        txtTongTu =
                view.findViewById(
                        R.id.txt_tong_tu
                );

        txtDaThuoc =
                view.findViewById(
                        R.id.txt_da_thuoc
                );

        txtTienDo =
                view.findViewById(
                        R.id.txt_tien_do
                );

        txtTuDenHan =
                view.findViewById(
                        R.id.txt_tudenhan
                );
    }


    /**
     * Hiển thị chuỗi học hiện tại.
     *
     * Hiện tại đang dùng dữ liệu mẫu.
     * Sau này sẽ lấy dữ liệu từ Backend.
     */
    private void hienThiChuoiHoc() {

        int chuoiHoc = 32;

        txtChuoiHoc.setText(
                String.valueOf(chuoiHoc)
        );
    }

    private void layThongKeHocTap() {

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


        apiService
                .layThongKeHocTap(
                        "Bearer " + token
                )
                .enqueue(
                        new Callback<ApiResponse<ThongKeHocTap>>() {

                            @Override
                            public void onResponse(
                                    @NonNull Call<ApiResponse<ThongKeHocTap>> call,
                                    @NonNull Response<ApiResponse<ThongKeHocTap>> response) {

                                if (!isAdded()) {
                                    return;
                                }


                                if (response.isSuccessful()
                                        && response.body() != null
                                        && response.body().getData() != null) {

                                    ThongKeHocTap thongKe =
                                            response.body().getData();


                                    txtTongTu.setText(
                                            String.valueOf(
                                                    thongKe.getTongTu()
                                            )
                                    );

                                    txtDaThuoc.setText(
                                            String.valueOf(
                                                    thongKe.getDaThuoc()
                                            )
                                    );

                                    txtTienDo.setText(
                                            thongKe.getTienDo() + "%"
                                    );

                                    txtTuDenHan.setText(
                                            String.valueOf(
                                                    thongKe.getTuDenHan()
                                            )
                                    );
                                }
                            }


                            @Override
                            public void onFailure(
                                    @NonNull Call<ApiResponse<ThongKeHocTap>> call,
                                    @NonNull Throwable t) {

                            }
                        }
                );
    }


    /**
     * Khi nhấn ảnh đại diện
     * sẽ chuyển sang tab Cá nhân.
     */
    private void thietLapSuKienAnhDaiDien() {

        txtAnhDaiDien.setOnClickListener(v -> {

            BottomNavigationView thanhDieuHuong =
                    requireActivity().findViewById(
                            R.id.thanh_dieu_huong
                    );

            thanhDieuHuong.setSelectedItemId(
                    R.id.menu_ca_nhan
            );
        });
    }


    /**
     * Khi nhấn vào khu vực từ đến hạn
     * sẽ chuyển sang tab Luyện tập.
     */
    private void thietLapSuKienTuDenHan() {

        layoutTuDenHan.setOnClickListener(v -> {

            BottomNavigationView thanhDieuHuong =
                    requireActivity().findViewById(
                            R.id.thanh_dieu_huong
                    );

            thanhDieuHuong.setSelectedItemId(
                    R.id.menu_luyen_tap
            );
        });
    }

    private void thietLapSuKienLuyenTapNhanh() {
        btnLuyenTapNhanh.setOnClickListener(v -> {
            BottomNavigationView thanhDieuHuong = requireActivity().findViewById(R.id.thanh_dieu_huong);
            thanhDieuHuong.setSelectedItemId(R.id.menu_luyen_tap);
        });
    }

    private void thietLapSuKienTaoBoTuNhanh() {
        btnTaoBoTuNhanh.setOnClickListener(v -> {
            BottomNavigationView thanhDieuHuong = requireActivity().findViewById(R.id.thanh_dieu_huong);
            thanhDieuHuong.setSelectedItemId(R.id.menu_bo_tu);
        });
    }

    private void thietLapSuKienCongDongNhanh() {
        btnCongDongNhanh.setOnClickListener(v -> {
            BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.thanh_dieu_huong);
            bottomNavigationView.setSelectedItemId(R.id.menu_xep_hang);
        });
    }


    /**
     * Khi nhấn Tạo bộ từ
     * sẽ chuyển sang tab Bộ từ.
     */
    private void thietLapSuKienTaoBoTu() {

        btnBoTu.setOnClickListener(v -> {

            BottomNavigationView thanhDieuHuong =
                    requireActivity().findViewById(
                            R.id.thanh_dieu_huong
                    );

            thanhDieuHuong.setSelectedItemId(
                    R.id.menu_bo_tu
            );
        });
    }


    /**
     * Theo dõi nội dung người dùng nhập
     * trong ô tra từ.
     */
    private void thietLapTraTu() {

        edtTraTu.addTextChangedListener(
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
                         * Nếu người dùng vẫn đang gõ
                         * thì hủy lần tìm kiếm trước.
                         */
                        if (runnableTimKiem != null) {

                            handlerTimKiem.removeCallbacks(
                                    runnableTimKiem
                            );
                        }


                        String tuKhoa =
                                s.toString().trim();


                        /*
                         * Không có từ khóa thì đóng gợi ý.
                         */
                        if (tuKhoa.isEmpty()) {

                            dongGoiY();

                            return;
                        }


                        /*
                         * Chờ 400ms sau khi người dùng
                         * ngừng gõ rồi mới gọi API.
                         */
                        runnableTimKiem =
                                () -> timKiemTu(tuKhoa);

                        handlerTimKiem.postDelayed(
                                runnableTimKiem,
                                400
                        );
                    }


                    @Override
                    public void afterTextChanged(
                            Editable s) {
                    }
                }
        );
    }


    /**
     * Gọi API lấy danh sách từ gợi ý.
     */
    private void timKiemTu(String tuKhoa) {

        apiService
                .timKiemTu(tuKhoa)
                .enqueue(
                        new Callback<SuggestionResponse>() {

                            @Override
                            public void onResponse(
                                    @NonNull Call<SuggestionResponse> call,
                                    @NonNull Response<SuggestionResponse> response) {

                                /*
                                 * Fragment không còn gắn với Activity
                                 * thì không cập nhật giao diện.
                                 */
                                if (!isAdded()) {
                                    return;
                                }


                                if (response.isSuccessful()
                                        && response.body() != null) {

                                    hienThiGoiY(
                                            response.body()
                                                    .getSuggestions()
                                    );

                                } else {

                                    dongGoiY();
                                }
                            }


                            @Override
                            public void onFailure(
                                    @NonNull Call<SuggestionResponse> call,
                                    @NonNull Throwable t) {

                                if (!isAdded()) {
                                    return;
                                }

                                dongGoiY();
                            }
                        }
                );
    }


    /**
     * Hiển thị danh sách gợi ý bằng PopupWindow.
     *
     * PopupWindow nổi trên giao diện nên không làm
     * các component phía dưới bị đẩy xuống.
     */
    private void hienThiGoiY(List<String> danhSachTu) {

        if (danhSachTu == null || danhSachTu.isEmpty()) {

            dongGoiY();

            return;
        }


        dongGoiY();


        /*
         * Inflate giao diện popup.
         */
        View viewPopup =
                LayoutInflater
                        .from(requireContext())
                        .inflate(
                                R.layout.trangchu_tratu_popup_goiytu,
                                null
                        );


        LinearLayout layoutDanhSach =
                viewPopup.findViewById(
                        R.id.layout_danh_sach_goi_y
                );


        /*
         * Tạo từng item gợi ý.
         */
        for (String tu : danhSachTu) {

            View item =
                    LayoutInflater
                            .from(requireContext())
                            .inflate(
                                    R.layout.trangchu_tratu_goiytu,
                                    layoutDanhSach,
                                    false
                            );


            TextView txtTu =
                    item.findViewById(
                            R.id.txt_tu_goi_y
                    );


            txtTu.setText(tu);


            item.setOnClickListener(v -> {
                moChiTietTu(tu);
            });


            layoutDanhSach.addView(item);
        }


        /*
         * Popup có chiều rộng bằng toàn bộ
         * khung tra từ.
         */
        View khungTraTu =
                requireView().findViewById(
                        R.id.khung_tra_tu
                );


        popupGoiY =
                new PopupWindow(
                        viewPopup,
                        khungTraTu.getWidth(),
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        false
                );


        popupGoiY.setBackgroundDrawable(
                new ColorDrawable(
                        Color.TRANSPARENT
                )
        );


        popupGoiY.setOutsideTouchable(true);

        popupGoiY.setElevation(
                dpToPx(6)
        );


        /*
         * Hiện ngay dưới toàn bộ khung tìm kiếm.
         */
        popupGoiY.showAsDropDown(
                khungTraTu,
                0,
                dpToPx(6)
        );
    }


    /**
     * Đóng danh sách gợi ý.
     */
    private void dongGoiY() {

        if (popupGoiY != null
                && popupGoiY.isShowing()) {

            popupGoiY.dismiss();
        }
    }


    /**
     * Chuyển dp sang pixel.
     */
    private int dpToPx(int dp) {

        return (int) (
                dp
                        * getResources()
                        .getDisplayMetrics()
                        .density
        );
    }

    /**
     * Dọn Handler và PopupWindow
     * khi Fragment bị hủy View.
     */
    @Override
    public void onDestroyView() {

        super.onDestroyView();

        if (runnableTimKiem != null) {

            handlerTimKiem.removeCallbacks(
                    runnableTimKiem
            );
        }

        dongGoiY();
    }

    private void layLichSuTraTu() {

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

            khungLichSuTraTu.setVisibility(
                    View.GONE
            );

            return;
        }

        apiService
                .layLichSuTraTu(
                        "Bearer " + token
                )
                .enqueue(new Callback<LichSuTraTuResponse>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<LichSuTraTuResponse> call,
                            @NonNull Response<LichSuTraTuResponse> response) {

                        if (!isAdded()) {
                            return;
                        }

                        if (response.isSuccessful()
                                && response.body() != null) {

                            hienThiLichSu(
                                    response.body().getHistory()
                            );

                        } else {

                            khungLichSuTraTu.setVisibility(
                                    View.GONE
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<LichSuTraTuResponse> call,
                            @NonNull Throwable t) {

                        if (!isAdded()) {
                            return;
                        }

                        khungLichSuTraTu.setVisibility(
                                View.GONE
                        );
                    }
                });
    }

    private void hienThiLichSu(
            List<LichSuTraTu> danhSach) {

        layoutLichSuTraTu.removeAllViews();

        if (danhSach == null
                || danhSach.isEmpty()) {

            khungLichSuTraTu.setVisibility(
                    View.GONE
            );

            return;
        }

        khungLichSuTraTu.setVisibility(
                View.VISIBLE
        );

        for (int i = 0; i < danhSach.size(); i++) {

            LichSuTraTu lichSu =
                    danhSach.get(i);

            /*
             * Tạo dòng chứa từ.
             */
            TextView txtTu =
                    new TextView(
                            requireContext()
                    );

            txtTu.setText(
                    lichSu.getWord()
            );

            txtTu.setTextSize(13);

            txtTu.setTextColor(
                    Color.parseColor(
                            "#111111"
                    )
            );

            txtTu.setTypeface(
                    getResources().getFont(
                            R.font.juve_headin
                    )
            );

            txtTu.setGravity(
                    Gravity.CENTER_VERTICAL
            );

            txtTu.setPadding(
                    dpToPx(14),
                    0,
                    dpToPx(14),
                    0
            );

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            dpToPx(48)
                    );

            txtTu.setLayoutParams(
                    params
            );

            /*
             * Bấm vào từ -> mở chi tiết.
             */
            txtTu.setOnClickListener(v ->
                    moChiTietTu(
                            lichSu.getWord()
                    )
            );

            layoutLichSuTraTu.addView(
                    txtTu
            );

            /*
             * Thêm đường kẻ giữa các từ.
             *
             * Từ cuối cùng không cần divider.
             */
            if (i < danhSach.size() - 1) {

                View divider =
                        new View(
                                requireContext()
                        );

                divider.setBackgroundColor(
                        Color.parseColor(
                                "#E2E3E3"
                        )
                );

                LinearLayout.LayoutParams dividerParams =
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                dpToPx(1)
                        );

                dividerParams.setMarginStart(
                        dpToPx(14)
                );

                dividerParams.setMarginEnd(
                        dpToPx(14)
                );

                divider.setLayoutParams(
                        dividerParams
                );

                layoutLichSuTraTu.addView(
                        divider
                );
            }
        }
    }

    private void moChiTietTu(String tu) {

        dongGoiY();

        edtTraTu.setText("");

        Bundle bundle =
                new Bundle();

        bundle.putString(
                "tu",
                tu
        );

        ChiTietTu chiTietTu =
                new ChiTietTu();

        chiTietTu.setArguments(
                bundle
        );

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.khung_noi_dung,
                        chiTietTu
                )
                .addToBackStack(null)
                .commit();
    }

    private void thietLapXoaLichSu() {

        btnXoaLichSu.setOnClickListener(v ->
                xoaLichSuTraTu()
        );
    }

    private void xoaLichSuTraTu() {

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


        apiService
                .xoaLichSuTraTu(
                        "Bearer " + token
                )
                .enqueue(new Callback<ApiResponse>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<ApiResponse> call,
                            @NonNull Response<ApiResponse> response) {

                        if (!isAdded()) {
                            return;
                        }


                        if (response.isSuccessful()) {

                            layoutLichSuTraTu.removeAllViews();

                            khungLichSuTraTu.setVisibility(
                                    View.GONE
                            );
                        }
                    }


                    @Override
                    public void onFailure(
                            @NonNull Call<ApiResponse> call,
                            @NonNull Throwable t) {

                        if (!isAdded()) {
                            return;
                        }

                        Toast.makeText(
                                requireContext(),
                                "Không thể xóa lịch sử",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }
}