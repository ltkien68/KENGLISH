package com.example.kenglish;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

    private ImageView btnPhatAm;

    private LinearLayout layoutDanhGia;

    private ProgressBar progressFlashcard;

    private TextToSpeech textToSpeech;

    private boolean ttsSanSang = false;
    private boolean ttsDaKhoiTao = false;

    private String tuChoPhatAm;

    private final Map<Integer, KetQuaTuVung> ketQuaPhienHoc =
            new LinkedHashMap<>();

    // ==================== KHỞI TẠO ====================

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
        khoiTaoPhatAm();
        xuLySuKien();
        hienThiThe();

        return view;
    }

    // ==================== ÁNH XẠ VIEW ====================

    private void anhXa(View view) {

        cardFlashcard = view.findViewById(
                R.id.card_flashcard
        );

        btnQuayLai = view.findViewById(
                R.id.btn_quay_lai
        );

        btnTruoc = view.findViewById(
                R.id.btn_truoc
        );

        btnTiep = view.findViewById(
                R.id.btn_tiep
        );

        btnDaThuoc = view.findViewById(
                R.id.btn_da_thuoc
        );

        btnChuaThuoc = view.findViewById(
                R.id.btn_chua_thuoc
        );

        btnPhatAm = view.findViewById(
                R.id.btn_phat_am_flashcard
        );

        txtTienDo = view.findViewById(
                R.id.txt_tien_do_flashcard
        );

        txtMatThe = view.findViewById(
                R.id.txt_mat_the
        );

        txtTu = view.findViewById(
                R.id.txt_tu_flashcard
        );

        txtPhienAm = view.findViewById(
                R.id.txt_phien_am_flashcard
        );

        txtLoaiTu = view.findViewById(
                R.id.txt_loai_tu_flashcard
        );

        txtViDu = view.findViewById(
                R.id.txt_vi_du_flashcard
        );

        txtGoiY = view.findViewById(
                R.id.txt_goi_y_lat
        );

        progressFlashcard = view.findViewById(
                R.id.progress_flashcard
        );

        layoutDanhGia = view.findViewById(
                R.id.layout_danh_gia
        );

        cardFlashcard.setCameraDistance(
                8000 * getResources()
                        .getDisplayMetrics().density
        );
    }

    // ==================== LẤY DỮ LIỆU ====================

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

            danhSachTu.clear();

            danhSachTu.addAll(
                    (ArrayList<TuVung>) duLieu
            );
        }
    }

    // ==================== TEXT TO SPEECH ====================

    private void khoiTaoPhatAm() {

        textToSpeech = new TextToSpeech(
                requireContext(),
                trangThai -> {

                    if (textToSpeech == null) {
                        return;
                    }

                    ttsDaKhoiTao = true;

                    if (trangThai == TextToSpeech.SUCCESS) {

                        int ketQua = textToSpeech.setLanguage(
                                Locale.US
                        );

                        if (ketQua >= TextToSpeech.LANG_AVAILABLE) {

                            ttsSanSang = true;

                            // Phát âm nếu người dùng đã nhấn loa
                            // trong lúc TTS đang khởi tạo.
                            if (tuChoPhatAm != null) {

                                phatAmNgay(tuChoPhatAm);

                                tuChoPhatAm = null;
                            }

                        } else {

                            ttsSanSang = false;

                            if (isAdded()) {
                                Toast.makeText(
                                        requireContext(),
                                        "Thiết bị chưa hỗ trợ giọng đọc tiếng Anh",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }

                    } else {

                        ttsSanSang = false;

                        if (isAdded()) {
                            Toast.makeText(
                                    requireContext(),
                                    "Không thể khởi tạo Text To Speech",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
                }
        );
    }

    // Phát âm từ được truyền vào.
    private void phatAm(String tu) {

        if (tu == null || tu.trim().isEmpty()) {
            return;
        }

        if (textToSpeech == null || !ttsSanSang) {

            // Chờ TTS khởi tạo xong.
            if (!ttsDaKhoiTao) {

                tuChoPhatAm = tu;

            } else {

                Toast.makeText(
                        requireContext(),
                        "Chưa thể phát âm từ này",
                        Toast.LENGTH_SHORT
                ).show();
            }

            return;
        }

        phatAmNgay(tu);
    }

    // Thực hiện phát âm.
    private void phatAmNgay(String tu) {

        if (textToSpeech == null || !ttsSanSang) {
            return;
        }

        textToSpeech.speak(
                tu,
                TextToSpeech.QUEUE_FLUSH,
                null,
                "flashcard_tts"
        );
    }

    // ==================== SỰ KIỆN ====================

    private void xuLySuKien() {

        // Quay lại
        btnQuayLai.setOnClickListener(v ->
                requireActivity()
                        .getSupportFragmentManager()
                        .popBackStack()
        );

        // Lật thẻ
        cardFlashcard.setOnClickListener(v -> latThe());

        // Phát âm từ hiện tại
        btnPhatAm.setOnClickListener(v -> {

            if (viTriHienTai >= 0
                    && viTriHienTai < danhSachTu.size()) {

                TuVung tu = danhSachTu.get(viTriHienTai);

                phatAm(tu.getTu_goc());
            }
        });

        // Thẻ trước
        btnTruoc.setOnClickListener(v -> {

            if (viTriHienTai > 0) {

                viTriHienTai--;
                dangHienMatSau = false;

                hienThiThe();
            }
        });

        // Thẻ tiếp theo
        btnTiep.setOnClickListener(v -> {

            if (danhSachTu.isEmpty()) {
                return;
            }

            if (viTriHienTai < danhSachTu.size() - 1) {

                viTriHienTai++;
                dangHienMatSau = false;

                hienThiThe();

            } else {

                // Chỉ hoàn thành khi mọi từ đều đã đánh giá.
                if (ketQuaPhienHoc.size() == danhSachTu.size()) {

                    moManHinhKetQua();

                } else {

                    Toast.makeText(
                            requireContext(),
                            "Hãy đánh giá tất cả từ trước khi hoàn thành",
                            Toast.LENGTH_SHORT
                    ).show();

                    // Quay đến từ đầu tiên chưa đánh giá.
                    for (int i = 0; i < danhSachTu.size(); i++) {

                        if (!ketQuaPhienHoc.containsKey(
                                danhSachTu.get(i).getId())) {

                            viTriHienTai = i;
                            dangHienMatSau = false;

                            hienThiThe();

                            break;
                        }
                    }
                }
            }
        });

        // Đánh dấu đã thuộc
        btnDaThuoc.setOnClickListener(v -> {
            danhDauTu(1);
        });

        // Đánh dấu chưa thuộc
        btnChuaThuoc.setOnClickListener(v -> {
            danhDauTu(0);
        });
    }

    // ==================== HIỂN THỊ THẺ ====================

    private void hienThiThe() {

        if (danhSachTu.isEmpty()) {

            txtTu.setText("Không có từ vựng");
            txtPhienAm.setText("");

            txtLoaiTu.setVisibility(View.GONE);
            txtViDu.setVisibility(View.GONE);

            txtTienDo.setText("0 / 0");

            btnPhatAm.setVisibility(View.GONE);

            return;
        }

        TuVung tu = danhSachTu.get(viTriHienTai);

        hienThiTrangThaiDanhGia();

        layoutDanhGia.setVisibility(
                dangHienMatSau ? View.VISIBLE : View.GONE
        );

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

        btnPhatAm.setVisibility(
                coNoiDung(tu.getTu_goc())
                        ? View.VISIBLE
                        : View.GONE
        );

        btnTruoc.setAlpha(
                viTriHienTai == 0 ? 0.4f : 1f
        );

        btnTiep.setText(
                viTriHienTai == danhSachTu.size() - 1
                        ? "HOÀN THÀNH"
                        : "TIẾP"
        );
    }

    // ==================== LẬT THẺ ====================

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

    // ==================== ĐÁNH GIÁ TỪ ====================

    private void danhDauTu(int trangThai) {

        if (danhSachTu.isEmpty() || !dangHienMatSau) {
            return;
        }

        TuVung tu = danhSachTu.get(viTriHienTai);

        KetQuaTuVung ketQua = new KetQuaTuVung(
                tu.getId(),
                tu.getTu_goc(),
                tu.getNghia_tieng_viet(),
                trangThai
        );

        // Lưu hoặc ghi đè kết quả của từ hiện tại.
        ketQuaPhienHoc.put(tu.getId(), ketQua);

        // Cập nhật giao diện lựa chọn.
        hienThiTrangThaiDanhGia();

        // Không tự chuyển thẻ, để người dùng
        // chủ động bấm TIẾP hoặc TRƯỚC.
    }

    // ==================== MÀN HÌNH KẾT QUẢ ====================

    private void moManHinhKetQua() {

        if (!isAdded() || ketQuaPhienHoc.isEmpty()) {
            return;
        }

        ArrayList<KetQuaTuVung> danhSachKetQua =
                new ArrayList<>();

        // Giữ đúng thứ tự từ trong phiên học.
        for (TuVung tu : danhSachTu) {

            KetQuaTuVung ketQua =
                    ketQuaPhienHoc.get(tu.getId());

            if (ketQua != null) {
                danhSachKetQua.add(ketQua);
            }
        }

        KetQuaLuyenTap fragment =
                KetQuaLuyenTap.newInstance(
                        "Flashcard",
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

    // ==================== HỖ TRỢ ====================

    private boolean coNoiDung(String giaTri) {

        return giaTri != null
                && !giaTri.trim().isEmpty();
    }

    private String giaTri(String noiDung) {

        return coNoiDung(noiDung)
                ? noiDung
                : "";
    }

    // ==================== TRẠNG THÁI ĐÁNH GIÁ ====================

    private void hienThiTrangThaiDanhGia() {

        if (danhSachTu.isEmpty()) {
            return;
        }

        TuVung tu = danhSachTu.get(viTriHienTai);

        KetQuaTuVung ketQua =
                ketQuaPhienHoc.get(tu.getId());

        // Chưa đánh giá từ này.
        if (ketQua == null) {

            btnDaThuoc.setAlpha(1f);
            btnChuaThuoc.setAlpha(1f);

            btnDaThuoc.setSelected(false);
            btnChuaThuoc.setSelected(false);

            return;
        }

        boolean daThuoc = ketQua.getDa_thuoc() == 1;

        btnDaThuoc.setSelected(daThuoc);
        btnChuaThuoc.setSelected(!daThuoc);

        // Nút được chọn nổi bật hơn.
        btnDaThuoc.setAlpha(daThuoc ? 1f : 0.4f);
        btnChuaThuoc.setAlpha(daThuoc ? 0.4f : 1f);
    }

    // ==================== GIẢI PHÓNG TTS ====================

    @Override
    public void onDestroyView() {

        if (textToSpeech != null) {

            textToSpeech.stop();
            textToSpeech.shutdown();

            textToSpeech = null;
        }

        ttsSanSang = false;
        ttsDaKhoiTao = false;
        tuChoPhatAm = null;

        super.onDestroyView();
    }
}
