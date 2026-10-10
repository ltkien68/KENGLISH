package com.example.kenglish;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
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

import com.example.kenglish.model.KetQuaTuVung;
import com.example.kenglish.model.TuVung;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class TracNghiem extends Fragment {
    private static final long THOI_GIAN_CAU = 30000L;
    private static final long THOI_GIAN_PHAN_HOI = 2000L;

    private final List<TuVung> danhSachTu = new ArrayList<>();
    private final List<KetQuaTuVung> danhSachKetQua = new ArrayList<>();
    private final List<String> dapAn = new ArrayList<>();
    private final List<TextView> viewDapAn = new ArrayList<>();

    private final Handler handler = new Handler(Looper.getMainLooper());
    private CountDownTimer dongHo;
    private TextToSpeech textToSpeech;

    private TextView txtCau, txtDiem, txtGio, txtTu, txtPhienAm;
    private TextView txtLoaiTu, txtPhanHoi, btnTamDung;
    private ProgressBar progressCau, progressGio;
    private LinearLayout layoutDapAn;

    private int viTri = 0;
    private int soCauDung = 0;
    private int dapAnDung = -1;
    private boolean daTraLoi = false;
    private boolean tamDung = false;
    private boolean dangPhanHoi = false;
    private long thoiGianConLai = THOI_GIAN_CAU;
    private Runnable chuyenCauRunnable;

    public static TracNghiem newInstance(List<TuVung> danhSach) {
        TracNghiem fragment = new TracNghiem();
        Bundle bundle = new Bundle();
        bundle.putSerializable("danh_sach_tu", new ArrayList<>(danhSach));
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tracnghiem, container, false);
        anhXa(view);
        layDuLieu();
        khoiTaoPhatAm();
        xuLySuKien(view);

        if (danhSachTu.size() < 4 || !duNghiaKhacNhau()) {
            Toast.makeText(requireContext(),
                    "Cần tối thiểu 4 nghĩa khác nhau để chơi",
                    Toast.LENGTH_LONG).show();
            view.post(() -> {
                if (isAdded()) {
                    requireActivity().getSupportFragmentManager().popBackStack();
                }
            });
            return view;
        }

        view.post(() -> {
            if (isAdded() && getView() != null) {
                hienThiCauHoi();
            }
        });
        return view;
    }

    private void anhXa(View view) {
        txtCau = view.findViewById(R.id.txt_cau_trac_nghiem);
        txtDiem = view.findViewById(R.id.txt_diem_trac_nghiem);
        txtGio = view.findViewById(R.id.txt_gio_trac_nghiem);
        txtTu = view.findViewById(R.id.txt_tu_trac_nghiem);
        txtPhienAm = view.findViewById(R.id.txt_phien_am_trac_nghiem);
        txtLoaiTu = view.findViewById(R.id.txt_loai_tu_trac_nghiem);
        txtPhanHoi = view.findViewById(R.id.txt_phan_hoi_trac_nghiem);
        btnTamDung = view.findViewById(R.id.btn_tam_dung_trac_nghiem);
        progressCau = view.findViewById(R.id.progress_cau_trac_nghiem);
        progressGio = view.findViewById(R.id.progress_gio_trac_nghiem);
        layoutDapAn = view.findViewById(R.id.layout_dap_an_trac_nghiem);
    }

    @SuppressWarnings("unchecked")
    private void layDuLieu() {
        Bundle bundle = getArguments();
        if (bundle == null) return;
        Object duLieu = bundle.getSerializable("danh_sach_tu");
        if (duLieu instanceof ArrayList) {
            danhSachTu.addAll((ArrayList<TuVung>) duLieu);
        }
    }

    private void khoiTaoPhatAm() {
        textToSpeech = new TextToSpeech(requireContext(), trangThai -> {
            if (trangThai == TextToSpeech.SUCCESS && textToSpeech != null) {
                textToSpeech.setLanguage(Locale.US);
            }
        });
    }


    private void xuLySuKien(View view) {
        view.findViewById(R.id.btn_choi_lai_trac_nghiem)
                .setOnClickListener(v -> choiLai());

        btnTamDung.setOnClickListener(v -> tamDungHoacTiepTuc());

        view.findViewById(R.id.btn_thoat_trac_nghiem)
                .setOnClickListener(v -> {
                    huyDongHo();
                    huyChuyenCau();
                    requireActivity().getSupportFragmentManager().popBackStack();
                });

        view.findViewById(R.id.btn_nghe_trac_nghiem)
                .setOnClickListener(v -> {
                    if (textToSpeech != null && !danhSachTu.isEmpty()) {
                        textToSpeech.speak(
                                danhSachTu.get(viTri).getTu_goc(),
                                TextToSpeech.QUEUE_FLUSH,
                                null,
                                "trac_nghiem_tts"
                        );
                    }
                });
    }

    private boolean duNghiaKhacNhau() {
        Set<String> tapNghia = new HashSet<>();
        for (TuVung tu : danhSachTu) {
            String nghia = chuanHoa(tu.getNghia_tieng_viet());
            if (!nghia.isEmpty()) tapNghia.add(nghia);
        }
        return tapNghia.size() >= 4;
    }

    private String chuanHoa(String giaTri) {
        return giaTri == null ? "" : giaTri.trim();
    }

    private void hienThiCauHoi() {
        if (!isAdded() || getView() == null) return;
        if (viTri >= danhSachTu.size()) {
            moKetQua();
            return;
        }

        TuVung tu = danhSachTu.get(viTri);
        daTraLoi = false;
        tamDung = false;
        dangPhanHoi = false;
        thoiGianConLai = THOI_GIAN_CAU;
        btnTamDung.setText("TẠM DỪNG");

        txtCau.setText("Câu " + (viTri + 1) + " / " + danhSachTu.size());
        txtDiem.setText("Đúng: " + soCauDung);
        txtTu.setText(chuanHoa(tu.getTu_goc()));
        txtPhienAm.setText(chuanHoa(tu.getPhien_am()));
        txtLoaiTu.setText(chuanHoa(tu.getLoai_tu()));
        txtPhanHoi.setVisibility(View.GONE);
        progressCau.setProgress(viTri * 100 / danhSachTu.size());

        taoDapAn(tu);
        hienThiDapAn();
        batDauDongHo();
    }

    private void taoDapAn(TuVung tuHienTai) {
        dapAn.clear();
        String nghiaDung = chuanHoa(tuHienTai.getNghia_tieng_viet());
        dapAn.add(nghiaDung);

        List<String> nghiaSai = new ArrayList<>();
        for (TuVung tu : danhSachTu) {
            String nghia = chuanHoa(tu.getNghia_tieng_viet());
            if (tu.getId() != tuHienTai.getId()
                    && !nghia.isEmpty()
                    && !nghia.equalsIgnoreCase(nghiaDung)
                    && !chuaNghia(nghiaSai, nghia)) {
                nghiaSai.add(nghia);
            }
        }

        Collections.shuffle(nghiaSai);
        for (int i = 0; i < Math.min(3, nghiaSai.size()); i++) {
            dapAn.add(nghiaSai.get(i));
        }

        Collections.shuffle(dapAn);
        dapAnDung = dapAn.indexOf(nghiaDung);
    }

    private boolean chuaNghia(List<String> danhSach, String nghia) {
        for (String item : danhSach) {
            if (item.equalsIgnoreCase(nghia)) return true;
        }
        return false;
    }

    private void hienThiDapAn() {
        layoutDapAn.removeAllViews();
        viewDapAn.clear();

        for (int i = 0; i < dapAn.size(); i++) {
            final int viTriDapAn = i;
            TextView txt = new TextView(requireContext());
            txt.setText((i + 1) + ".   " + dapAn.get(i));
            txt.setTextSize(13);
            txt.setTextColor(Color.parseColor("#111111"));
            txt.setTypeface(getResources().getFont(R.font.juve_normal));
            txt.setGravity(Gravity.CENTER_VERTICAL);
            txt.setPadding(dp(16), dp(14), dp(16), dp(14));
            txt.setMinHeight(dp(72));
            txt.setBackground(taoNen("#FFFFFF", "#DCE8F7"));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.topMargin = dp(10);
            layoutDapAn.addView(txt, params);
            viewDapAn.add(txt);
            txt.setOnClickListener(v -> chonDapAn(viTriDapAn));
        }
    }

    private GradientDrawable taoNen(String mauNen, String mauVien) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.parseColor(mauNen));
        drawable.setCornerRadius(dp(6));
        drawable.setStroke(dp(2), Color.parseColor(mauVien));
        return drawable;
    }

    private int dp(int giaTri) {
        return Math.round(giaTri * getResources().getDisplayMetrics().density);
    }

    private void batDauDongHo() {
        huyDongHo();
        txtGio.setText((int) Math.ceil(thoiGianConLai / 1000.0) + "s");
        progressGio.setProgress((int) (thoiGianConLai / 1000));

        dongHo = new CountDownTimer(thoiGianConLai, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                thoiGianConLai = millisUntilFinished;
                txtGio.setText((int) Math.ceil(millisUntilFinished / 1000.0) + "s");
                progressGio.setProgress((int) Math.ceil(millisUntilFinished / 1000.0));
            }

            @Override
            public void onFinish() {
                thoiGianConLai = 0;
                txtGio.setText("0s");
                progressGio.setProgress(0);
                if (!daTraLoi) xuLyKetQua(-1);
            }
        }.start();
    }

    private void huyDongHo() {
        if (dongHo != null) {
            dongHo.cancel();
            dongHo = null;
        }
    }

    private void chonDapAn(int viTriChon) {
        if (daTraLoi || tamDung || dangPhanHoi) return;
        xuLyKetQua(viTriChon);
    }

    private void xuLyKetQua(int viTriChon) {
        if (daTraLoi) return;
        daTraLoi = true;
        dangPhanHoi = true;
        huyDongHo();

        boolean dung = viTriChon == dapAnDung;
        if (dung) soCauDung++;

        TuVung tu = danhSachTu.get(viTri);
        danhSachKetQua.add(new KetQuaTuVung(
                tu.getId(),
                tu.getTu_goc(),
                tu.getNghia_tieng_viet(),
                dung ? 1 : 0
        ));

        for (int i = 0; i < viewDapAn.size(); i++) {
            TextView txt = viewDapAn.get(i);
            if (i == dapAnDung) {
                txt.setBackground(taoNen("#EEF8F1", "#2E9E60"));
                txt.setTextColor(Color.parseColor("#2E9E60"));
            } else if (i == viTriChon) {
                txt.setBackground(taoNen("#FFF0F0", "#E55454"));
                txt.setTextColor(Color.parseColor("#E55454"));
            }
        }

        txtDiem.setText("Đúng: " + soCauDung);
        txtPhanHoi.setVisibility(View.VISIBLE);
        txtPhanHoi.setText((dung ? "Chính xác!" :
                viTriChon == -1 ? "Hết thời gian!" : "Chưa chính xác!")
                + "\nNghĩa đúng: " + dapAn.get(dapAnDung));

        chuyenCauRunnable = () -> {
            chuyenCauRunnable = null;
            if (!isAdded() || getView() == null) return;
            viTri++;
            hienThiCauHoi();
        };
        handler.postDelayed(chuyenCauRunnable, THOI_GIAN_PHAN_HOI);
    }

    private void moKetQua() {
        huyDongHo();
        huyChuyenCau();
        if (!isAdded()) return;

        KetQuaLuyenTap fragment = KetQuaLuyenTap.newInstance(
                "Trắc nghiệm",
                new ArrayList<>(danhSachKetQua)
        );

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .hide(this)
                .add(R.id.khung_noi_dung, fragment, "KET_QUA_LUYEN_TAP")
                .addToBackStack("KET_QUA_LUYEN_TAP")
                .commit();
    }

    private void huyChuyenCau() {
        if (chuyenCauRunnable != null) {
            handler.removeCallbacks(chuyenCauRunnable);
            chuyenCauRunnable = null;
        }
    }

    private void choiLai() {
        huyDongHo();
        huyChuyenCau();
        viTri = 0;
        soCauDung = 0;
        danhSachKetQua.clear();
        Collections.shuffle(danhSachTu);
        hienThiCauHoi();
    }

    private void tamDungHoacTiepTuc() {
        if (daTraLoi || dangPhanHoi) return;
        tamDung = !tamDung;
        if (tamDung) {
            huyDongHo();
            btnTamDung.setText("TIẾP TỤC");
            for (TextView txt : viewDapAn) txt.setEnabled(false);
        } else {
            btnTamDung.setText("TẠM DỪNG");
            for (TextView txt : viewDapAn) txt.setEnabled(true);
            batDauDongHo();
        }
    }

    @Override
    public void onDestroyView() {
        huyDongHo();
        huyChuyenCau();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
        }
        super.onDestroyView();
    }
}
