package com.example.kenglish.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.kenglish.R;
import com.example.kenglish.model.TuVung;

import java.util.List;


public class TuVungAdapter
        extends ArrayAdapter<TuVung> {


    /*
     * =========================================
     * CALLBACK XÓA TỪ
     * =========================================
     */

    public interface OnXoaTuListener {

        void onXoaTu(
                TuVung tuVung
        );
    }

    public interface OnTrangThaiTuListener {

        void onDoiTrangThai(
                TuVung tuVung
        );
    }


    /*
     * =========================================
     * BIẾN
     * =========================================
     */

    private final Context context;

    private final List<TuVung> danhSachTu;

    private final OnXoaTuListener onXoaTuListener;

    private final OnTrangThaiTuListener onTrangThaiTuListener;


    /*
     * =========================================
     * CONSTRUCTOR
     * =========================================
     */

    public TuVungAdapter(
            @NonNull Context context,
            @NonNull List<TuVung> danhSachTu,
            OnXoaTuListener onXoaTuListener,
            OnTrangThaiTuListener onTrangThaiTuListener) {

        super(
                context,
                R.layout.botu_itemtuvung,
                danhSachTu
        );

        this.context =
                context;

        this.danhSachTu =
                danhSachTu;

        this.onXoaTuListener =
                onXoaTuListener;

        this.onTrangThaiTuListener =
                onTrangThaiTuListener;
    }


    /*
     * =========================================
     * HIỂN THỊ ITEM
     * =========================================
     */

    @SuppressLint("ClickableViewAccessibility")
    @NonNull
    @Override
    public View getView(
            int position,
            @Nullable View convertView,
            @NonNull ViewGroup parent) {


        /*
         * =====================================
         * TẠO VIEW
         * =====================================
         */

        if (convertView == null) {

            convertView =
                    LayoutInflater
                            .from(context)
                            .inflate(
                                    R.layout.botu_itemtuvung,
                                    parent,
                                    false
                            );
        }


        /*
         * =====================================
         * LẤY TỪ VỰNG
         * =====================================
         */

        TuVung tuVung =
                danhSachTu.get(position);


        /*
         * =====================================
         * ÁNH XẠ
         * =====================================
         */

        TextView txtTuGoc =
                convertView.findViewById(
                        R.id.txt_tu_goc
                );


        TextView txtPhienAmLoaiTu =
                convertView.findViewById(
                        R.id.txt_phien_am_loai_tu
                );


        TextView txtNghiaTiengViet =
                convertView.findViewById(
                        R.id.txt_nghia_tieng_viet
                );


        TextView txtCauViDu =
                convertView.findViewById(
                        R.id.txt_cau_vi_du
                );


        TextView txtTrangThai =
                convertView.findViewById(
                        R.id.txt_trang_thai
                );


        LinearLayout cardTuVung =
                convertView.findViewById(
                        R.id.card_tu_vung
                );


        LinearLayout layoutXoa =
                convertView.findViewById(
                        R.id.layout_xoa
                );


        /*
         * =====================================
         * TỪ GỐC
         * =====================================
         */

        txtTuGoc.setText(
                tuVung.getTu_goc()
        );


        /*
         * =====================================
         * PHIÊN ÂM + LOẠI TỪ
         * =====================================
         */

        String phienAm =
                tuVung.getPhien_am();


        String loaiTu =
                tuVung.getLoai_tu();


        if (phienAm == null) {

            phienAm = "";
        }


        if (loaiTu == null) {

            loaiTu = "";
        }


        String thongTinPhu;


        if (!phienAm.isEmpty()
                && !loaiTu.isEmpty()) {

            thongTinPhu =
                    phienAm
                            + "  •  "
                            + loaiTu;

        } else if (!phienAm.isEmpty()) {

            thongTinPhu =
                    phienAm;

        } else {

            thongTinPhu =
                    loaiTu;
        }


        txtPhienAmLoaiTu.setText(
                thongTinPhu
        );


        /*
         * Nếu không có cả phiên âm
         * lẫn loại từ thì ẩn dòng này.
         */

        if (thongTinPhu.isEmpty()) {

            txtPhienAmLoaiTu.setVisibility(
                    View.GONE
            );

        } else {

            txtPhienAmLoaiTu.setVisibility(
                    View.VISIBLE
            );
        }


        /*
         * =====================================
         * NGHĨA TIẾNG VIỆT
         * =====================================
         */

        txtNghiaTiengViet.setText(
                tuVung.getNghia_tieng_viet()
        );


        /*
         * =====================================
         * CÂU VÍ DỤ
         * =====================================
         */

        String cauViDu =
                tuVung.getCau_vi_du();


        if (cauViDu == null
                || cauViDu.trim().isEmpty()) {

            txtCauViDu.setVisibility(
                    View.GONE
            );

        } else {

            txtCauViDu.setVisibility(
                    View.VISIBLE
            );


            txtCauViDu.setText(
                    cauViDu
            );
        }


        /*
         * =====================================
         * TRẠNG THÁI
         * =====================================
         */

        if (tuVung.isDa_thuoc()) {

            txtTrangThai.setText(
                    "Đã thuộc"
            );

            txtTrangThai.setTextColor(
                    android.graphics.Color.parseColor(
                            "#3E7D58"
                    )
            );

        } else {

            txtTrangThai.setText(
                    "Chưa thuộc"
            );

            txtTrangThai.setTextColor(
                    android.graphics.Color.parseColor(
                            "#7C8492"
                    )
            );
        }

        /*
         * =====================================
         * BẤM TRẠNG THÁI
         * =====================================
         */

        txtTrangThai.setOnClickListener(
                view -> {

                    if (onTrangThaiTuListener != null) {

                        onTrangThaiTuListener
                                .onDoiTrangThai(
                                        tuVung
                                );
                    }
                }
        );


        /*
         * =====================================
         * RESET VỊ TRÍ CARD
         * =====================================
         *
         * ListView tái sử dụng item.
         *
         * Nếu không reset thì card khác
         * có thể bị lệch khi scroll.
         */

        cardTuVung
                .animate()
                .cancel();


        cardTuVung.setTranslationX(
                0f
        );


        /*
         * =====================================
         * THÔNG SỐ SWIPE
         * =====================================
         */

        float matDoManHinh =
                context
                        .getResources()
                        .getDisplayMetrics()
                        .density;


        /*
         * Chiều rộng vùng XÓA.
         */
        float khoangMoNutXoa =
                82f
                        * matDoManHinh;


        /*
         * Kéo quá 40dp:
         * mở nút XÓA.
         */
        float nguongMo =
                40f
                        * matDoManHinh;


        /*
         * Kéo quá 160dp:
         * xóa luôn.
         */
        float nguongXoa =
                160f
                        * matDoManHinh;


        /*
         * =====================================
         * BIẾN THEO DÕI GESTURE
         * =====================================
         */

        final float[] viTriBatDauX =
                new float[1];


        final float[] translationBanDau =
                new float[1];


        final boolean[] dangVuot =
                new boolean[1];


        /*
         * =====================================
         * SWIPE CARD
         * =====================================
         */

        cardTuVung.setOnTouchListener(
                (view, event) -> {


                    switch (event.getActionMasked()) {


                        /*
                         * =========================
                         * BẮT ĐẦU CHẠM
                         * =========================
                         */

                        case MotionEvent.ACTION_DOWN:


                            /*
                             * Hủy animation cũ nếu
                             * card đang chạy.
                             */
                            cardTuVung
                                    .animate()
                                    .cancel();


                            viTriBatDauX[0] =
                                    event.getRawX();


                            translationBanDau[0] =
                                    cardTuVung
                                            .getTranslationX();


                            dangVuot[0] =
                                    false;


                            return true;


                        /*
                         * =========================
                         * ĐANG KÉO
                         * =========================
                         */

                        case MotionEvent.ACTION_MOVE:


                            float khoangDiChuyen =
                                    event.getRawX()
                                            - viTriBatDauX[0];


                            /*
                             * Nếu ngón tay di chuyển
                             * đủ một chút thì xác định
                             * đây là swipe.
                             */
                            if (Math.abs(khoangDiChuyen)
                                    > 5f * matDoManHinh) {

                                dangVuot[0] =
                                        true;
                            }


                            float viTriMoi =
                                    translationBanDau[0]
                                            + khoangDiChuyen;


                            /*
                             * Không cho card đi sang
                             * phải quá vị trí ban đầu.
                             */
                            if (viTriMoi > 0f) {

                                viTriMoi =
                                        0f;
                            }


                            /*
                             * Giới hạn khoảng kéo trái.
                             */
                            if (viTriMoi < -nguongXoa) {

                                viTriMoi =
                                        -nguongXoa;
                            }


                            cardTuVung
                                    .setTranslationX(
                                            viTriMoi
                                    );


                            return true;


                        /*
                         * =========================
                         * THẢ TAY
                         * =========================
                         */

                        case MotionEvent.ACTION_UP:


                            float viTriCuoi =
                                    cardTuVung
                                            .getTranslationX();


                            /*
                             * -------------------------
                             * VUỐT ĐỦ XA
                             * → XÓA LUÔN
                             * -------------------------
                             */

                            if (viTriCuoi
                                    <= -nguongXoa) {


                                cardTuVung
                                        .animate()
                                        .translationX(
                                                -cardTuVung
                                                        .getWidth()
                                        )
                                        .setDuration(
                                                180
                                        )
                                        .withEndAction(
                                                () -> {

                                                    if (onXoaTuListener
                                                            != null) {

                                                        onXoaTuListener
                                                                .onXoaTu(
                                                                        tuVung
                                                                );
                                                    }
                                                }
                                        )
                                        .start();


                                return true;
                            }


                            /*
                             * -------------------------
                             * VUỐT VỪA
                             * → MỞ NÚT XÓA
                             * -------------------------
                             */

                            if (viTriCuoi
                                    <= -nguongMo) {


                                cardTuVung
                                        .animate()
                                        .translationX(
                                                -khoangMoNutXoa
                                        )
                                        .setDuration(
                                                150
                                        )
                                        .start();


                                return true;
                            }


                            /*
                             * -------------------------
                             * VUỐT KHÔNG ĐỦ
                             * → ĐÓNG CARD
                             * -------------------------
                             */

                            cardTuVung
                                    .animate()
                                    .translationX(
                                            0f
                                    )
                                    .setDuration(
                                            150
                                    )
                                    .start();


                            /*
                             * Nếu sau này đại ca muốn
                             * click card để xem chi tiết
                             * thì dangVuot[] có thể dùng
                             * để phân biệt click/swipe.
                             */

                            return true;


                        /*
                         * =========================
                         * GESTURE BỊ HỦY
                         * =========================
                         */

                        case MotionEvent.ACTION_CANCEL:


                            float viTriKhiHuy =
                                    cardTuVung
                                            .getTranslationX();


                            /*
                             * Nếu đã mở đủ xa thì
                             * giữ vùng XÓA.
                             */
                            if (viTriKhiHuy
                                    <= -nguongMo) {


                                cardTuVung
                                        .animate()
                                        .translationX(
                                                -khoangMoNutXoa
                                        )
                                        .setDuration(
                                                150
                                        )
                                        .start();

                            } else {


                                cardTuVung
                                        .animate()
                                        .translationX(
                                                0f
                                        )
                                        .setDuration(
                                                150
                                        )
                                        .start();
                            }


                            return true;
                    }


                    return false;
                }
        );


        /*
         * =====================================
         * BẤM VÙNG XÓA
         * =====================================
         */

        layoutXoa.setOnClickListener(
                view -> {


                    /*
                     * Cho card chạy hết sang trái
                     * trước khi gọi callback.
                     */

                    cardTuVung
                            .animate()
                            .translationX(
                                    -cardTuVung
                                            .getWidth()
                            )
                            .setDuration(
                                    180
                            )
                            .withEndAction(
                                    () -> {

                                        if (onXoaTuListener
                                                != null) {

                                            onXoaTuListener
                                                    .onXoaTu(
                                                            tuVung
                                                    );
                                        }
                                    }
                            )
                            .start();
                }
        );


        return convertView;
    }
}