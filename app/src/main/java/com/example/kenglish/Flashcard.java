package com.example.kenglish;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.kenglish.model.TuVung;

import java.util.ArrayList;
import java.util.List;

public class Flashcard extends Fragment {

    private final List<TuVung> danhSachTu = new ArrayList<>();

    private int viTriHienTai = 0;
    private boolean dangHienMatSau = false;

    private LinearLayout cardFlashcard;

    private TextView btnQuayLai;
    private TextView btnTruoc;
    private TextView btnTiep;
    private TextView btnDaThuoc;
    private TextView btnChuaThuoc;

    private TextView txtTienDo;
    private TextView txtMatThe;
    private TextView txtTu;
    private TextView txtPhienAm;
    private TextView txtLoaiTu;
    private TextView txtViDu;
    private TextView txtGoiY;

    private ProgressBar progressFlashcard;

    public static Flashcard newInstance(List<TuVung> danhSach) {

        Flashcard fragment = new Flashcard();

        Bundle bundle = new Bundle();

        bundle.putSerializable(
                "danh_sach_tu",
                new ArrayList<>(danhSach)
        );

        fragment.setArguments(bundle);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.flashcard,
                container,
                false
        );

        anhXa(view);
        layDuLieu();
        xuLySuKien();
        hienThiThe();

        return view;
    }

    private void anhXa(View view) {

        cardFlashcard = view.findViewById(R.id.card_flashcard);

        btnQuayLai = view.findViewById(R.id.btn_quay_lai);
        btnTruoc = view.findViewById(R.id.btn_truoc);
        btnTiep = view.findViewById(R.id.btn_tiep);
        btnDaThuoc = view.findViewById(R.id.btn_da_thuoc);
        btnChuaThuoc = view.findViewById(R.id.btn_chua_thuoc);

        txtTienDo = view.findViewById(R.id.txt_tien_do_flashcard);
        txtMatThe = view.findViewById(R.id.txt_mat_the);
        txtTu = view.findViewById(R.id.txt_tu_flashcard);
        txtPhienAm = view.findViewById(R.id.txt_phien_am_flashcard);
        txtLoaiTu = view.findViewById(R.id.txt_loai_tu_flashcard);
        txtViDu = view.findViewById(R.id.txt_vi_du_flashcard);
        txtGoiY = view.findViewById(R.id.txt_goi_y_lat);

        progressFlashcard = view.findViewById(R.id.progress_flashcard);

        cardFlashcard.setCameraDistance(
                8000 * getResources().getDisplayMetrics().density
        );
    }

    @SuppressWarnings("unchecked")
    private void layDuLieu() {

        Bundle bundle = getArguments();

        if (bundle == null) {
            return;
        }

        Object duLieu = bundle.getSerializable("danh_sach_tu");

        if (duLieu instanceof ArrayList) {

            danhSachTu.clear();

            danhSachTu.addAll(
                    (ArrayList<TuVung>) duLieu
            );
        }
    }

    private void xuLySuKien() {

        btnQuayLai.setOnClickListener(v ->
                requireActivity()
                        .getSupportFragmentManager()
                        .popBackStack()
        );

        cardFlashcard.setOnClickListener(v -> latThe());

        btnTruoc.setOnClickListener(v -> {

            if (viTriHienTai > 0) {
                viTriHienTai--;
                dangHienMatSau = false;
                hienThiThe();
            }
        });

        btnTiep.setOnClickListener(v -> {

            if (viTriHienTai < danhSachTu.size() - 1) {
                viTriHienTai++;
                dangHienMatSau = false;
                hienThiThe();
            } else {
                hienThiKetQua();
            }
        });

        btnDaThuoc.setOnClickListener(v -> {
            danhDauTu(1);
        });

        btnChuaThuoc.setOnClickListener(v -> {
            danhDauTu(0);
        });
    }

    private void hienThiThe() {

        if (danhSachTu.isEmpty()) {

            txtTu.setText("Không có từ vựng");
            txtPhienAm.setText("");
            txtLoaiTu.setVisibility(View.GONE);
            txtViDu.setVisibility(View.GONE);
            txtTienDo.setText("0 / 0");

            return;
        }

        TuVung tu = danhSachTu.get(viTriHienTai);

        txtTienDo.setText(
                (viTriHienTai + 1) + " / " + danhSachTu.size()
        );

        int phanTram = (viTriHienTai + 1)
                * 100 / danhSachTu.size();

        progressFlashcard.setProgress(phanTram);

        if (dangHienMatSau) {

            txtMatThe.setText("MẶT SAU");

            txtTu.setText(
                    giaTri(tu.getNghia_tieng_viet())
            );

            txtPhienAm.setText(
                    giaTri(tu.getTu_goc())
            );

            txtLoaiTu.setText(
                    giaTri(tu.getLoai_tu())
            );

            txtViDu.setText(
                    giaTri(tu.getCau_vi_du())
            );

            txtLoaiTu.setVisibility(
                    coNoiDung(tu.getLoai_tu())
                            ? View.VISIBLE
                            : View.GONE
            );

            txtViDu.setVisibility(
                    coNoiDung(tu.getCau_vi_du())
                            ? View.VISIBLE
                            : View.GONE
            );

        } else {

            txtMatThe.setText("MẶT TRƯỚC");

            txtTu.setText(
                    giaTri(tu.getTu_goc())
            );

            txtPhienAm.setText(
                    giaTri(tu.getPhien_am())
            );

            txtLoaiTu.setVisibility(View.GONE);
            txtViDu.setVisibility(View.GONE);
        }

        txtGoiY.setText("Chạm để lật thẻ");

        btnTruoc.setAlpha(
                viTriHienTai == 0 ? 0.4f : 1f
        );

        btnTiep.setText(
                viTriHienTai == danhSachTu.size() - 1
                        ? "HOÀN THÀNH"
                        : "TIẾP →"
        );
    }

    private void latThe() {

        if (danhSachTu.isEmpty()) {
            return;
        }

        cardFlashcard.animate()
                .rotationY(90f)
                .setDuration(150)
                .withEndAction(() -> {

                    if (!isAdded() || getView() == null) {
                        return;
                    }

                    dangHienMatSau = !dangHienMatSau;

                    hienThiThe();

                    cardFlashcard.setRotationY(-90f);

                    cardFlashcard.animate()
                            .rotationY(0f)
                            .setDuration(150)
                            .start();
                })
                .start();
    }

    private void danhDauTu(int trangThai) {

        if (danhSachTu.isEmpty()) {
            return;
        }

        TuVung tu = danhSachTu.get(viTriHienTai);

        // Chỉ cập nhật trong phiên học.
        // Chưa gửi dữ liệu lên MySQL.
        tu.setDa_thuoc(trangThai);

        if (viTriHienTai < danhSachTu.size() - 1) {

            viTriHienTai++;
            dangHienMatSau = false;
            hienThiThe();

        } else {

            hienThiKetQua();
        }
    }

    private void hienThiKetQua() {

        int soTuDaThuoc = 0;

        for (TuVung tu : danhSachTu) {

            if (tu.isDa_thuoc()) {
                soTuDaThuoc++;
            }
        }

        int soTuChuaThuoc =
                danhSachTu.size() - soTuDaThuoc;

        new androidx.appcompat.app.AlertDialog.Builder(
                requireContext()
        )
                .setTitle("Hoàn thành Flashcard")
                .setMessage(
                        "Tổng số từ: " + danhSachTu.size()
                                + "\nĐã thuộc: " + soTuDaThuoc
                                + "\nChưa thuộc: " + soTuChuaThuoc
                )
                .setPositiveButton("Hoàn thành", (dialog, which) -> {

                    requireActivity()
                            .getSupportFragmentManager()
                            .popBackStack();
                })
                .setNegativeButton("Học lại", (dialog, which) -> {

                    viTriHienTai = 0;
                    dangHienMatSau = false;
                    hienThiThe();
                })
                .show();
    }

    private boolean coNoiDung(String giaTri) {

        return giaTri != null
                && !giaTri.trim().isEmpty();
    }

    private String giaTri(String noiDung) {

        return coNoiDung(noiDung)
                ? noiDung
                : "";
    }
}
