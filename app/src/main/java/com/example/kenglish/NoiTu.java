package com.example.kenglish;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.kenglish.model.KetQuaTuVung;
import com.example.kenglish.model.TuVung;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class NoiTu extends Fragment {

    private static final int SO_CAP_MOI_LUOT = 5;
    private static final long THOI_GIAN_BAO_SAI = 500L;

    private final List<TuVung> danhSachTu = new ArrayList<>();
    private final Set<Integer> tapTuNoiSai = new HashSet<>();

    private final Handler handler = new Handler(Looper.getMainLooper());

    private LinearLayout layoutCotTu;
    private LinearLayout layoutCotNghia;

    private TextView txtLuot;
    private TextView txtTienDo;
    private TextView txtPhanHoi;
    private TextView btnThoat;

    private ProgressBar progressNoiTu;

    private TextToSpeech textToSpeech;

    private TuVung tuDangChon;
    private TuVung nghiaDangChon;

    private TextView chipTuDangChon;
    private TextView chipNghiaDangChon;

    private int viTriLuot = 0;
    private int soCapDaNoi = 0;
    private int soCapTrongLuot = 0;
    private int soCapDaNoiTrongLuot = 0;

    private int tongSoLuot = 0;

    private boolean dangXuLyCap = false;
    private boolean ttsSanSang = false;
    private boolean daMoKetQua = false;

    private String tuChoPhatAm;

    public static NoiTu newInstance(List<TuVung> danhSach) {

        NoiTu fragment = new NoiTu();

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
                R.layout.noitu,
                container,
                false
        );

        anhXa(view);
        layDuLieu();

        if (!duLieuHopLe()) {

            Toast.makeText(
                    requireContext(),
                    "Cần ít nhất 4 từ có nghĩa khác nhau để chơi",
                    Toast.LENGTH_LONG
            ).show();

            view.post(() -> {
                if (isAdded()) {
                    requireActivity()
                            .getSupportFragmentManager()
                            .popBackStack();
                }
            });

            return view;
        }

        khoiTaoPhatAm();
        xuLySuKien();

        tongSoLuot = (int) Math.ceil(
                danhSachTu.size() / (double) SO_CAP_MOI_LUOT
        );

        progressNoiTu.setMax(danhSachTu.size());

        hienThiLuot();

        return view;
    }

    private void anhXa(View view) {

        layoutCotTu = view.findViewById(
                R.id.layout_cot_tu_noi_tu
        );

        layoutCotNghia = view.findViewById(
                R.id.layout_cot_nghia_noi_tu
        );

        txtLuot = view.findViewById(
                R.id.txt_luot_noi_tu
        );

        txtTienDo = view.findViewById(
                R.id.txt_tien_do_noi_tu
        );

        txtPhanHoi = view.findViewById(
                R.id.txt_phan_hoi_noi_tu
        );

        progressNoiTu = view.findViewById(
                R.id.progress_noi_tu
        );

        btnThoat = view.findViewById(
                R.id.btn_thoat_noi_tu
        );
    }

    @SuppressWarnings("unchecked")
    private void layDuLieu() {

        Bundle bundle = getArguments();

        if (bundle == null) {
            return;
        }

        Object duLieu = bundle.getSerializable(
                "danh_sach_tu"
        );

        if (duLieu instanceof ArrayList) {
            danhSachTu.addAll((ArrayList<TuVung>) duLieu);
        }
    }

    private boolean duLieuHopLe() {

        if (danhSachTu.size() < 4) {
            return false;
        }

        Set<String> tapNghia = new HashSet<>();

        for (TuVung tu : danhSachTu) {

            String nghia = chuanHoa(tu.getNghia_tieng_viet());

            if (nghia.isEmpty()) {
                return false;
            }

            if (!tapNghia.add(nghia.toLowerCase(Locale.ROOT))) {
                return false;
            }
        }

        return true;
    }

    private String chuanHoa(String giaTri) {
        return giaTri == null ? "" : giaTri.trim();
    }

    private void khoiTaoPhatAm() {

        textToSpeech = new TextToSpeech(
                requireContext(),
                trangThai -> {

                    if (trangThai != TextToSpeech.SUCCESS
                            || textToSpeech == null) {
                        return;
                    }

                    int ketQua = textToSpeech.setLanguage(Locale.US);

                    if (ketQua >= TextToSpeech.LANG_AVAILABLE) {

                        ttsSanSang = true;

                        if (tuChoPhatAm != null) {

                            phatAmNgay(tuChoPhatAm);

                            tuChoPhatAm = null;
                        }
                    }
                }
        );
    }

    private void phatAmTu(String tu) {

        if (tu == null || tu.trim().isEmpty()) {
            return;
        }

        if (!ttsSanSang || textToSpeech == null) {

            tuChoPhatAm = tu;
            return;
        }

        phatAmNgay(tu);
    }

    private void phatAmNgay(String tu) {

        if (textToSpeech == null || !ttsSanSang) {
            return;
        }

        textToSpeech.speak(
                tu,
                TextToSpeech.QUEUE_FLUSH,
                null,
                "noi_tu_tts"
        );
    }

    private void xuLySuKien() {

        btnThoat.setOnClickListener(v -> {

            handler.removeCallbacksAndMessages(null);

            requireActivity()
                    .getSupportFragmentManager()
                    .popBackStack();
        });
    }

    // ==================== HIỂN THỊ LƯỢT ====================

    private void hienThiLuot() {

        if (!isAdded()) {
            return;
        }

        layoutCotTu.removeAllViews();
        layoutCotNghia.removeAllViews();

        tuDangChon = null;
        nghiaDangChon = null;

        chipTuDangChon = null;
        chipNghiaDangChon = null;

        dangXuLyCap = false;

        int viTriBatDau = viTriLuot * SO_CAP_MOI_LUOT;

        int viTriKetThuc = Math.min(
                viTriBatDau + SO_CAP_MOI_LUOT,
                danhSachTu.size()
        );

        List<TuVung> danhSachLuot = new ArrayList<>(
                danhSachTu.subList(viTriBatDau, viTriKetThuc)
        );

        soCapTrongLuot = danhSachLuot.size();
        soCapDaNoiTrongLuot = 0;

        List<TuVung> cotTu = new ArrayList<>(danhSachLuot);
        List<TuVung> cotNghia = new ArrayList<>(danhSachLuot);

        Collections.shuffle(cotTu);
        Collections.shuffle(cotNghia);

        for (TuVung tu : cotTu) {

            TextView chip = taoChip(
                    tu.getTu_goc()
            );

            layoutCotTu.addView(chip);

            chip.setOnClickListener(v -> chonChipTu(tu, chip));
        }

        for (TuVung tu : cotNghia) {

            TextView chip = taoChip(
                    tu.getNghia_tieng_viet()
            );

            layoutCotNghia.addView(chip);

            chip.setOnClickListener(v -> chonChipNghia(tu, chip));
        }

        capNhatTienDo();

        txtLuot.setText(
                "LƯỢT " + (viTriLuot + 1)
                        + "/" + tongSoLuot
                        + " · " + soCapTrongLuot + " cặp"
        );

        txtPhanHoi.setText("Chọn một cặp từ tương ứng");
        txtPhanHoi.setTextColor(Color.parseColor("#7C8492"));
    }

    // ==================== TẠO CHIP ====================

    private TextView taoChip(String noiDung) {

        TextView chip = new TextView(requireContext());

        chip.setText(chuanHoa(noiDung));
        chip.setTextSize(13);
        chip.setTextColor(Color.parseColor("#4F4F4F"));
        chip.setGravity(Gravity.CENTER);
        chip.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);

        chip.setPadding(
                dp(7),
                dp(10),
                dp(7),
                dp(10)
        );

        chip.setMinHeight(dp(44));
        chip.setMaxLines(3);

        chip.setBackground(
                taoNenChip(
                        "#F5F5F6",
                        "#E2E3E3",
                        1
                )
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        params.topMargin = dp(8);

        chip.setLayoutParams(params);

        return chip;
    }

    private GradientDrawable taoNenChip(
            String mauNen,
            String mauVien,
            int doDayVien) {

        GradientDrawable drawable = new GradientDrawable();

        drawable.setColor(Color.parseColor(mauNen));
        drawable.setCornerRadius(dp(50));

        drawable.setStroke(
                dp(doDayVien),
                Color.parseColor(mauVien)
        );

        return drawable;
    }

    private void chonTrangThaiChip(
            TextView chip,
            String mauNen,
            String mauVien,
            int doDayVien,
            String mauChu,
            boolean dam) {

        chip.setBackground(
                taoNenChip(
                        mauNen,
                        mauVien,
                        doDayVien
                )
        );

        chip.setTextColor(Color.parseColor(mauChu));

        chip.setTypeface(
                Typeface.DEFAULT,
                dam ? Typeface.BOLD : Typeface.NORMAL
        );
    }

    private void datChipMacDinh(TextView chip) {

        chonTrangThaiChip(
                chip,
                "#F5F5F6",
                "#E2E3E3",
                1,
                "#4F4F4F",
                false
        );
    }

    private void datChipDuocChon(TextView chip) {

        chonTrangThaiChip(
                chip,
                "#EEF4FB",
                "#37659C",
                2,
                "#37659C",
                true
        );
    }

    private void datChipSai(TextView chip) {

        chonTrangThaiChip(
                chip,
                "#FCEBEC",
                "#D64545",
                2,
                "#D64545",
                true
        );
    }

    // ==================== CHỌN CHIP ====================

    private void chonChipTu(
            TuVung tu,
            TextView chip) {

        if (dangXuLyCap) {
            return;
        }

        if (chipTuDangChon == chip) {

            datChipMacDinh(chip);

            chipTuDangChon = null;
            tuDangChon = null;

            return;
        }

        if (chipTuDangChon != null) {
            datChipMacDinh(chipTuDangChon);
        }

        tuDangChon = tu;
        chipTuDangChon = chip;

        datChipDuocChon(chip);

        kiemTraCap();
    }

    private void chonChipNghia(
            TuVung tu,
            TextView chip) {

        if (dangXuLyCap) {
            return;
        }

        if (chipNghiaDangChon == chip) {

            datChipMacDinh(chip);

            chipNghiaDangChon = null;
            nghiaDangChon = null;

            return;
        }

        if (chipNghiaDangChon != null) {
            datChipMacDinh(chipNghiaDangChon);
        }

        nghiaDangChon = tu;
        chipNghiaDangChon = chip;

        datChipDuocChon(chip);

        kiemTraCap();
    }

    // ==================== KIỂM TRA CẶP ====================

    private void kiemTraCap() {

        if (tuDangChon == null || nghiaDangChon == null) {
            return;
        }

        if (dangXuLyCap) {
            return;
        }

        dangXuLyCap = true;

        // Phát âm từ tiếng Anh vừa được chọn.
        phatAmTu(tuDangChon.getTu_goc());

        boolean dung = tuDangChon.getId() == nghiaDangChon.getId();

        if (dung) {
            xuLyNoiDung();
        } else {
            xuLyNoiSai();
        }
    }

    private void xuLyNoiDung() {

        txtPhanHoi.setText("Chính xác! Tuyệt vời!");
        txtPhanHoi.setTextColor(Color.parseColor("#37659C"));

        // Ẩn hai chip đã nối đúng.
        chipTuDangChon.setVisibility(View.GONE);
        chipNghiaDangChon.setVisibility(View.GONE);

        soCapDaNoi++;
        soCapDaNoiTrongLuot++;

        capNhatTienDo();

        tuDangChon = null;
        nghiaDangChon = null;

        chipTuDangChon = null;
        chipNghiaDangChon = null;

        if (soCapDaNoiTrongLuot >= soCapTrongLuot) {

            handler.postDelayed(() -> {

                if (!isAdded() || getView() == null) {
                    return;
                }

                viTriLuot++;

                if (viTriLuot >= tongSoLuot) {
                    moKetQua();
                } else {
                    hienThiLuot();
                }

            }, 450);

        } else {
            dangXuLyCap = false;
        }
    }

    private void xuLyNoiSai() {

        // Ghi nhận từ tiếng Anh đã từng nối sai.
        tapTuNoiSai.add(tuDangChon.getId());

        datChipSai(chipTuDangChon);
        datChipSai(chipNghiaDangChon);

        txtPhanHoi.setText("Chưa chính xác, thử lại nhé!");
        txtPhanHoi.setTextColor(Color.parseColor("#D64545"));

        // Rung nhẹ hai chip.
        rungChip(chipTuDangChon);
        rungChip(chipNghiaDangChon);

        handler.postDelayed(() -> {

            if (!isAdded() || getView() == null) {
                return;
            }

            chipTuDangChon.clearAnimation();
            chipNghiaDangChon.clearAnimation();

            datChipMacDinh(chipTuDangChon);
            datChipMacDinh(chipNghiaDangChon);

            tuDangChon = null;
            nghiaDangChon = null;

            chipTuDangChon = null;
            chipNghiaDangChon = null;

            dangXuLyCap = false;

        }, THOI_GIAN_BAO_SAI);
    }

    private void rungChip(View view) {

        TranslateAnimation animation = new TranslateAnimation(
                -dp(4),
                dp(4),
                0,
                0
        );

        animation.setDuration(55);
        animation.setRepeatCount(3);
        animation.setRepeatMode(TranslateAnimation.REVERSE);

        view.startAnimation(animation);
    }

    // ==================== TIẾN ĐỘ ====================

    private void capNhatTienDo() {

        txtTienDo.setText(
                "Đã nối " + soCapDaNoi
                        + "/" + danhSachTu.size() + " cặp"
        );

        progressNoiTu.setProgress(soCapDaNoi);
    }

    // ==================== KẾT QUẢ ====================

    private void moKetQua() {

        if (daMoKetQua || !isAdded()) {
            return;
        }

        daMoKetQua = true;

        ArrayList<KetQuaTuVung> danhSachKetQua =
                new ArrayList<>();

        for (TuVung tu : danhSachTu) {

            int daThuoc = tapTuNoiSai.contains(tu.getId())
                    ? 0
                    : 1;

            danhSachKetQua.add(
                    new KetQuaTuVung(
                            tu.getId(),
                            tu.getTu_goc(),
                            tu.getNghia_tieng_viet(),
                            daThuoc
                    )
            );
        }

        KetQuaLuyenTap fragment = KetQuaLuyenTap.newInstance(
                "Nối từ với nghĩa",
                danhSachKetQua
        );

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .hide(this)
                .add(
                        R.id.khung_noi_dung,
                        fragment,
                        "KET_QUA_LUYEN_TAP"
                )
                .addToBackStack("KET_QUA_LUYEN_TAP")
                .commit();
    }

    private int dp(int giaTri) {

        return Math.round(
                giaTri * getResources().getDisplayMetrics().density
        );
    }

    @Override
    public void onDestroyView() {

        handler.removeCallbacksAndMessages(null);

        if (textToSpeech != null) {

            textToSpeech.stop();
            textToSpeech.shutdown();

            textToSpeech = null;
        }

        ttsSanSang = false;

        super.onDestroyView();
    }
}