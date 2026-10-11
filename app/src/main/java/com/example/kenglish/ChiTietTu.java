package com.example.kenglish;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.kenglish.api.ApiService;
import com.example.kenglish.api.RetrofitClient;
import com.example.kenglish.model.ApiResponse;
import com.example.kenglish.model.DictionaryResponse;
import com.example.kenglish.model.Meaning;
import com.example.kenglish.model.ThemLichSuRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChiTietTu extends Fragment {

    private TextView txtTuVung;
    private TextView txtIpa;
    private TextView btnLuuTu;

    private ImageView btnQuayLai;
    private ImageView btnPhatAm;

    private LinearLayout layoutDanhSachNghia;

    private ApiService apiService;

    private String tuCanTra;
    private String audioUrl;

    private MediaPlayer mediaPlayer;

    private static final int MAU_XANH = Color.rgb(8, 124, 255);
    private static final int MAU_NAVY = Color.rgb(8, 47, 112);
    private static final int MAU_NOI_DUNG = Color.rgb(36, 59, 85);
    private static final int MAU_CHU_PHU = Color.rgb(130, 148, 173);

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.chitiettu,
                container,
                false
        );

        anhXa(view);

        BottomNavigationView thanhDieuHuong =
                requireActivity().findViewById(
                        R.id.thanh_dieu_huong
                );

        if (thanhDieuHuong != null) {
            thanhDieuHuong.setVisibility(View.GONE);
        }

        apiService = RetrofitClient
                .getClient()
                .create(ApiService.class);

        layTuCanTra();
        thietLapSuKien();

        if (tuCanTra != null && !tuCanTra.trim().isEmpty()) {
            traTu(tuCanTra);
        } else {
            Toast.makeText(
                    requireContext(),
                    "Không tìm thấy từ cần tra",
                    Toast.LENGTH_SHORT
            ).show();
        }

        return view;
    }

    private void anhXa(View view) {

        txtTuVung = view.findViewById(R.id.txt_tu_vung);
        txtIpa = view.findViewById(R.id.txt_ipa);

        btnQuayLai = view.findViewById(R.id.btn_quay_lai);
        btnPhatAm = view.findViewById(R.id.btn_phat_am);
        btnLuuTu = view.findViewById(R.id.btn_luu_tu);

        layoutDanhSachNghia =
                view.findViewById(R.id.layout_danh_sach_nghia);
    }

    private void layTuCanTra() {

        Bundle bundle = getArguments();

        if (bundle != null) {
            tuCanTra = bundle.getString("tu");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        BottomNavigationView thanhDieuHuong =
                requireActivity().findViewById(
                        R.id.thanh_dieu_huong
                );

        if (thanhDieuHuong != null) {
            thanhDieuHuong.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        BottomNavigationView thanhDieuHuong =
                requireActivity().findViewById(
                        R.id.thanh_dieu_huong
                );

        if (thanhDieuHuong != null) {
            thanhDieuHuong.setVisibility(View.VISIBLE);
        }
    }

    private void thietLapSuKien() {

        btnQuayLai.setOnClickListener(v ->
                requireActivity()
                        .getSupportFragmentManager()
                        .popBackStack()
        );

        btnPhatAm.setOnClickListener(v -> phatAm());

        btnLuuTu.setOnClickListener(v ->
                Toast.makeText(
                        requireContext(),
                        "Chức năng lưu từ sẽ làm tiếp",
                        Toast.LENGTH_SHORT
                ).show()
        );
    }

    // ==================================================
    // TRA TỪ
    // ==================================================

    private void traTu(String tu) {

        apiService.traTu(tu)
                .enqueue(new Callback<DictionaryResponse>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<DictionaryResponse> call,
                            @NonNull Response<DictionaryResponse> response) {

                        if (!isAdded() || getView() == null) {
                            return;
                        }

                        if (response.isSuccessful()
                                && response.body() != null) {

                            DictionaryResponse duLieu = response.body();

                            hienThiChiTietTu(duLieu);

                            if (duLieu.getWord() != null) {
                                themVaoLichSu(duLieu.getWord());
                            }

                        } else {
                            Toast.makeText(
                                    requireContext(),
                                    "Không tìm thấy từ",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<DictionaryResponse> call,
                            @NonNull Throwable t) {

                        if (!isAdded() || getView() == null) {
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

    private void hienThiChiTietTu(DictionaryResponse duLieu) {

        txtTuVung.setText(duLieu.getWord());

        String ipa = duLieu.getIpa();

        if (ipa != null && !ipa.trim().isEmpty()) {
            txtIpa.setText(ipa);
            txtIpa.setVisibility(View.VISIBLE);
        } else {
            txtIpa.setVisibility(View.GONE);
        }

        audioUrl = duLieu.getAudio();

        hienThiDanhSachNghia(duLieu.getMeanings());
    }

    // ==================================================
    // HIỂN THỊ CARD NGHĨA
    // ==================================================

    private void hienThiDanhSachNghia(List<Meaning> danhSachNghia) {

        layoutDanhSachNghia.removeAllViews();

        if (danhSachNghia == null || danhSachNghia.isEmpty()) {

            TextView txtKhongCoNghia = new TextView(requireContext());

            txtKhongCoNghia.setText("Chưa có nghĩa cho từ này.");
            txtKhongCoNghia.setTextSize(13);
            txtKhongCoNghia.setTextColor(MAU_CHU_PHU);
            txtKhongCoNghia.setTypeface(
                    Typeface.create("sans-serif", Typeface.NORMAL)
            );

            layoutDanhSachNghia.addView(txtKhongCoNghia);
            return;
        }

        int soNghia = Math.min(danhSachNghia.size(), 3);

        for (int i = 0; i < soNghia; i++) {

            Meaning meaning = danhSachNghia.get(i);

            // Mỗi nghĩa là một card riêng.
            LinearLayout cardNghia = new LinearLayout(requireContext());

            cardNghia.setOrientation(LinearLayout.VERTICAL);
            cardNghia.setPadding(
                    dpToPx(14),
                    dpToPx(14),
                    dpToPx(14),
                    dpToPx(14)
            );

            cardNghia.setBackgroundResource(
                    R.drawable.nen_chitiettu_nghia
            );

            LinearLayout.LayoutParams paramsCard =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

            if (i > 0) {
                paramsCard.topMargin = dpToPx(10);
            }

            cardNghia.setLayoutParams(paramsCard);

            // Loại từ
            String loaiTu = meaning.getPos();

            if (loaiTu != null && !loaiTu.trim().isEmpty()) {
                cardNghia.addView(taoTextLoaiTu(loaiTu));
            }

            // Nghĩa tiếng Việt
            cardNghia.addView(
                    taoTextNghia(
                            i + 1,
                            meaning.getDefinition()
                    )
            );

            // Ví dụ (nếu API có)
            String viDu = meaning.getExample();

            if (viDu != null && !viDu.trim().isEmpty()) {
                cardNghia.addView(taoKhungViDu(viDu));
            }

            layoutDanhSachNghia.addView(cardNghia);
        }
    }

    private TextView taoTextLoaiTu(String loaiTu) {

        TextView textView = new TextView(requireContext());

        textView.setText(loaiTu);
        textView.setTextSize(13);
        textView.setTextColor(MAU_XANH);
        textView.setTypeface(
                Typeface.create("sans-serif-medium", Typeface.NORMAL)
        );

        textView.setGravity(android.view.Gravity.CENTER_VERTICAL);

        textView.setPadding(
                dpToPx(12),
                dpToPx(6),
                dpToPx(12),
                dpToPx(6)
        );

        textView.setBackgroundResource(
                R.drawable.nen_chitiettu_loaitu
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        textView.setLayoutParams(params);

        return textView;
    }

    private TextView taoTextNghia(int soThuTu, String nghia) {

        TextView textView = new TextView(requireContext());

        String noiDung = nghia == null ? "" : nghia;

        textView.setText(soThuTu + ". " + noiDung);
        textView.setTextSize(15);
        textView.setTextColor(MAU_NOI_DUNG);
        textView.setTypeface(
                Typeface.create("sans-serif", Typeface.NORMAL)
        );

        textView.setLineSpacing(dpToPx(4), 1f);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.topMargin = dpToPx(12);

        textView.setLayoutParams(params);

        return textView;
    }

    private LinearLayout taoKhungViDu(String viDu) {

        LinearLayout khungViDu = new LinearLayout(requireContext());

        khungViDu.setOrientation(LinearLayout.HORIZONTAL);
        khungViDu.setGravity(android.view.Gravity.CENTER_VERTICAL);

        khungViDu.setPadding(
                dpToPx(12),
                dpToPx(12),
                dpToPx(12),
                dpToPx(12)
        );

        khungViDu.setBackgroundResource(
                R.drawable.nen_chitiettu_vidu
        );

        LinearLayout.LayoutParams paramsKhung =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        paramsKhung.topMargin = dpToPx(12);
        khungViDu.setLayoutParams(paramsKhung);

        // Icon ví dụ
        ImageView iconViDu = new ImageView(requireContext());

        iconViDu.setImageResource(R.drawable.ic_tailieu);
        iconViDu.setColorFilter(MAU_XANH);

        LinearLayout.LayoutParams paramsIcon =
                new LinearLayout.LayoutParams(
                        dpToPx(20),
                        dpToPx(20)
                );

        iconViDu.setLayoutParams(paramsIcon);
        iconViDu.setContentDescription("Ví dụ");

        khungViDu.addView(iconViDu);

        // Nội dung ví dụ
        TextView txtViDu = new TextView(requireContext());

        txtViDu.setText(viDu);
        txtViDu.setTextSize(13);
        txtViDu.setTextColor(MAU_NAVY);
        txtViDu.setTypeface(
                Typeface.create("sans-serif", Typeface.NORMAL)
        );

        txtViDu.setLineSpacing(dpToPx(3), 1f);

        LinearLayout.LayoutParams paramsText =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                );

        paramsText.leftMargin = dpToPx(10);
        txtViDu.setLayoutParams(paramsText);

        khungViDu.addView(txtViDu);

        return khungViDu;
    }

    // ==================================================
    // PHÁT ÂM
    // ==================================================

    private void phatAm() {

        if (audioUrl == null || audioUrl.trim().isEmpty()) {

            Toast.makeText(
                    requireContext(),
                    "Từ này chưa có phát âm",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        giaiPhongMediaPlayer();

        MediaPlayer player = new MediaPlayer();
        mediaPlayer = player;

        try {

            player.setDataSource(audioUrl);

            player.setOnPreparedListener(mp -> {
                if (mediaPlayer == mp) {
                    mp.start();
                }
            });

            player.setOnCompletionListener(mp -> {
                if (mediaPlayer == mp) {
                    giaiPhongMediaPlayer();
                }
            });

            player.setOnErrorListener((mp, what, extra) -> {

                if (mediaPlayer == mp) {
                    giaiPhongMediaPlayer();
                }

                if (isAdded()) {
                    Toast.makeText(
                            requireContext(),
                            "Không thể phát âm",
                            Toast.LENGTH_SHORT
                    ).show();
                }

                return true;
            });

            player.prepareAsync();

        } catch (IOException | IllegalArgumentException | IllegalStateException e) {

            giaiPhongMediaPlayer();

            Toast.makeText(
                    requireContext(),
                    "Không thể phát âm",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void giaiPhongMediaPlayer() {

        if (mediaPlayer != null) {

            try {
                mediaPlayer.release();
            } catch (IllegalStateException ignored) {
            }

            mediaPlayer = null;
        }
    }

    // ==================================================
    // LỊCH SỬ TRA TỪ
    // ==================================================

    private void themVaoLichSu(String tu) {

        SharedPreferences sharedPreferences =
                requireContext().getSharedPreferences(
                        "Kenglish",
                        Context.MODE_PRIVATE
                );

        String token = sharedPreferences.getString("token", null);

        if (token == null) {
            return;
        }

        ThemLichSuRequest request = new ThemLichSuRequest(tu);

        apiService.themLichSuTraTu(
                "Bearer " + token,
                request
        ).enqueue(new Callback<ApiResponse>() {

            @Override
            public void onResponse(
                    @NonNull Call<ApiResponse> call,
                    @NonNull Response<ApiResponse> response) {

                // Ghi lịch sử ngầm, không cần Toast.
            }

            @Override
            public void onFailure(
                    @NonNull Call<ApiResponse> call,
                    @NonNull Throwable t) {

                // Không làm gián đoạn chức năng tra từ.
            }
        });
    }

    // ==================================================
    // TIỆN ÍCH
    // ==================================================

    private int dpToPx(int dp) {

        return (int) (
                dp * getResources()
                        .getDisplayMetrics()
                        .density
                        + 0.5f
        );
    }

    @Override
    public void onDestroyView() {

        giaiPhongMediaPlayer();

        txtTuVung = null;
        txtIpa = null;
        btnLuuTu = null;
        btnQuayLai = null;
        btnPhatAm = null;
        layoutDanhSachNghia = null;

        super.onDestroyView();
    }
}
