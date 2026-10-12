package com.example.kenglish;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kenglish.api.ApiService;
import com.example.kenglish.api.RetrofitClient;
import com.example.kenglish.adapter.TinNhanKaiAdapter;
import com.example.kenglish.model.AiChatRequest;
import com.example.kenglish.model.AiChatResponse;
import com.example.kenglish.model.KaiIntentRequest;
import com.example.kenglish.model.KaiIntentResponse;
import com.example.kenglish.model.KaiCreateVocabularyRequest;
import com.example.kenglish.model.KaiCreateVocabularyResponse;
import com.example.kenglish.model.TinNhanKai;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KaiChat extends Fragment {

    private RecyclerView recyclerTinNhan;
    private ScrollView layoutChao;
    private EditText edtTinNhan;
    private ImageButton btnGui;
    private ImageButton btnLamMoi;

    private TinNhanKaiAdapter adapter;
    private final List<TinNhanKai> danhSachTinNhan = new ArrayList<>();

    private ApiService apiService;
    private Call<?> requestHienTai;
    private AlertDialog hopThoaiXacNhan;

    private boolean dangGui = false;
    private int phienChat = 0;

    public KaiChat() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.kai_chat, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerTinNhan = view.findViewById(R.id.recycler_tin_nhan_kai);
        layoutChao = view.findViewById(R.id.layout_chao_kai);
        edtTinNhan = view.findViewById(R.id.edt_tin_nhan_kai);
        btnGui = view.findViewById(R.id.btn_gui_kai);
        btnLamMoi = view.findViewById(R.id.btn_lam_moi_chat);

        apiService = RetrofitClient.getClient().create(ApiService.class);
        adapter = new TinNhanKaiAdapter(danhSachTinNhan);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(requireContext());
        layoutManager.setStackFromEnd(true);

        recyclerTinNhan.setLayoutManager(layoutManager);
        recyclerTinNhan.setAdapter(adapter);

        capNhatGiaoDien();
        capNhatNutGui();

        btnGui.setOnClickListener(v -> guiTinNhan());
        btnLamMoi.setOnClickListener(v -> lamMoiChat());

        ganSuKienGoiY(
                view,
                R.id.goi_y_1,
                "Giải thích sự khác nhau giữa do và make"
        );

        ganSuKienGoiY(
                view,
                R.id.goi_y_2,
                "Tạo bộ từ gồm 10 từ vựng chủ đề công nghệ rồi mở luyện tập"
        );

        ganSuKienGoiY(
                view,
                R.id.goi_y_3,
                "Cách ghi nhớ từ vựng hiệu quả"
        );

        ganSuKienGoiY(
                view,
                R.id.goi_y_4,
                "Giải thích thì hiện tại hoàn thành"
        );
    }

    private void ganSuKienGoiY(View view, int id, String cauHoi) {
        TextView goiY = view.findViewById(id);

        goiY.setOnClickListener(v -> {
            if (dangGui) {
                return;
            }

            edtTinNhan.setText(cauHoi);
            guiTinNhan();
        });
    }

    private String layToken() {
        SharedPreferences preferences =
                requireContext().getSharedPreferences(
                        "Kenglish",
                        Context.MODE_PRIVATE
                );

        String token = preferences.getString("token", "");
        return token == null ? "" : token.trim();
    }

    private void guiTinNhan() {
        if (dangGui) {
            return;
        }

        String noiDung = edtTinNhan.getText()
                .toString()
                .trim();

        if (noiDung.isEmpty()) {
            return;
        }

        if (noiDung.length() > 2000) {
            edtTinNhan.setError("Câu hỏi tối đa 2000 ký tự");
            return;
        }

        String token = layToken();

        if (token.isEmpty()) {
            themTinNhan(
                    "Bạn cần đăng nhập để sử dụng Kai.",
                    false
            );
            return;
        }

        edtTinNhan.setText("");
        anBanPhim();

        themTinNhan(noiDung, true);
        themTinNhan("Kai đang phân tích yêu cầu...", false);

        dangGui = true;
        capNhatNutGui();

        final int phienYeuCau = phienChat;

        phanTichYeuCau(
                noiDung,
                token,
                phienYeuCau
        );
    }

    private void phanTichYeuCau(
            String noiDung,
            String token,
            int phienYeuCau) {

        Call<KaiIntentResponse> request =
                apiService.phanTichYeuCauKai(
                        "Bearer " + token,
                        new KaiIntentRequest(noiDung)
                );

        requestHienTai = request;

        request.enqueue(new Callback<KaiIntentResponse>() {
            @Override
            public void onResponse(
                    @NonNull Call<KaiIntentResponse> call,
                    @NonNull Response<KaiIntentResponse> response) {

                if (!conHieuLuc(phienYeuCau)) {
                    return;
                }

                if (!response.isSuccessful()) {
                    ketThucVoiLoi(response.code());
                    return;
                }

                KaiIntentResponse body = response.body();

                if (body == null ||
                        !body.isThanhCong() ||
                        body.getData() == null) {
                    ketThucVoiThongBao(
                            "Kai chưa phân tích được yêu cầu. Bạn thử lại nhé."
                    );
                    return;
                }

                KaiIntentResponse.DuLieu duLieu = body.getData();

                if ("tao_bo_tu".equals(duLieu.getHanhDong())) {
                    if (
                            duLieu.getChuDe() == null ||
                                    duLieu.getChuDe().trim().isEmpty() ||
                                    duLieu.getSoLuong() < 1 ||
                                    duLieu.getSoLuong() > 10
                    ) {
                        ketThucVoiThongBao(
                                "Thông tin bộ từ chưa hợp lệ. Bạn hãy thử lại."
                        );
                        return;
                    }

                    requestHienTai = null;

                    thayTinNhanCho(
                            "Kai đã hiểu yêu cầu. Mình sẽ tạo bộ từ chủ đề "
                                    + duLieu.getChuDe()
                                    + " gồm "
                                    + duLieu.getSoLuong()
                                    + " từ vựng."
                    );

                    hienThiXacNhan(
                            duLieu,
                            token,
                            phienYeuCau
                    );
                    return;
                }

                if ("chat".equals(duLieu.getHanhDong())) {
                    goiChatThongThuong(
                            noiDung,
                            token,
                            phienYeuCau
                    );
                    return;
                }

                ketThucVoiThongBao(
                        "Kai chưa hỗ trợ hành động này."
                );
            }

            @Override
            public void onFailure(
                    @NonNull Call<KaiIntentResponse> call,
                    @NonNull Throwable t) {

                if (!conHieuLuc(phienYeuCau)) {
                    return;
                }

                if (call.isCanceled()) {
                    return;
                }

                ketThucVoiThongBao(thongBaoLoiKetNoi(t));
            }
        });
    }

    private void goiChatThongThuong(
            String noiDung,
            String token,
            int phienYeuCau) {

        thayTinNhanCho("Kai đang suy nghĩ...");

        Call<AiChatResponse> request =
                apiService.chatKai(
                        "Bearer " + token,
                        new AiChatRequest(noiDung)
                );

        requestHienTai = request;

        request.enqueue(new Callback<AiChatResponse>() {
            @Override
            public void onResponse(
                    @NonNull Call<AiChatResponse> call,
                    @NonNull Response<AiChatResponse> response) {

                if (!conHieuLuc(phienYeuCau)) {
                    return;
                }

                if (!response.isSuccessful()) {
                    ketThucVoiLoi(response.code());
                    return;
                }

                AiChatResponse body = response.body();

                if (
                        body == null ||
                                !body.isSuccess() ||
                                body.getData() == null ||
                                body.getData().getTraLoi() == null ||
                                body.getData().getTraLoi().trim().isEmpty()
                ) {
                    ketThucVoiThongBao(
                            "Kai chưa nhận được câu trả lời hợp lệ."
                    );
                    return;
                }

                ketThucVoiThongBao(
                        lamSachMarkdown(
                                body.getData().getTraLoi().trim()
                        )
                );
            }

            @Override
            public void onFailure(
                    @NonNull Call<AiChatResponse> call,
                    @NonNull Throwable t) {

                if (!conHieuLuc(phienYeuCau)) {
                    return;
                }

                if (call.isCanceled()) {
                    return;
                }

                ketThucVoiThongBao(thongBaoLoiKetNoi(t));
            }
        });
    }

    private void hienThiXacNhan(
            KaiIntentResponse.DuLieu duLieu,
            String token,
            int phienYeuCau) {

        if (!conHieuLuc(phienYeuCau)) {
            return;
        }

        String chuDe = duLieu.getChuDe();
        int soLuong = duLieu.getSoLuong();
        boolean moLuyenTap = duLieu.isMoLuyenTap();

        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_xac_nhan_tao_bo_tu, null);

        TextView txtChuDe =
                dialogView.findViewById(R.id.txt_dialog_chu_de);
        TextView txtSoLuong =
                dialogView.findViewById(R.id.txt_dialog_so_luong);
        TextView txtHanhDong =
                dialogView.findViewById(R.id.txt_dialog_hanh_dong);
        TextView btnHuy =
                dialogView.findViewById(R.id.btn_dialog_huy);
        TextView btnTao =
                dialogView.findViewById(R.id.btn_dialog_tao);

        txtChuDe.setText(chuDe);
        txtSoLuong.setText("Số lượng: " + soLuong + " từ vựng");
        txtHanhDong.setText(
                moLuyenTap
                        ? "Sau khi tạo: Mở Luyện tập"
                        : "Sau khi tạo: Lưu vào Bộ từ của tôi"
        );

        hopThoaiXacNhan = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        AlertDialog dialog = hopThoaiXacNhan;

        dialog.setOnCancelListener(d -> {
            if (conHieuLuc(phienYeuCau)) {
                ketThucVoiThongBao(
                        "Đã hủy tạo bộ từ. Kai chưa thay đổi dữ liệu."
                );
            }
        });

        btnHuy.setOnClickListener(v -> {
            dialog.dismiss();

            if (conHieuLuc(phienYeuCau)) {
                ketThucVoiThongBao(
                        "Đã hủy tạo bộ từ. Kai chưa thay đổi dữ liệu."
                );
            }
        });

        btnTao.setOnClickListener(v -> {
            dialog.dismiss();

            if (!conHieuLuc(phienYeuCau)) {
                return;
            }

            thayTinNhanCho("Kai đang tạo bộ từ và lưu từ vựng...");

            taoBoTuBangAI(
                    chuDe,
                    soLuong,
                    moLuyenTap,
                    token,
                    phienYeuCau
            );
        });

        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(
                    android.R.color.transparent
            );

            dialog.getWindow().setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.88f),
                    android.view.WindowManager.LayoutParams.WRAP_CONTENT
            );
        }
    }

    private void taoBoTuBangAI(
            String chuDe,
            int soLuong,
            boolean moLuyenTap,
            String token,
            int phienYeuCau) {

        Call<KaiCreateVocabularyResponse> request =
                apiService.taoBoTuBangKai(
                        "Bearer " + token,
                        new KaiCreateVocabularyRequest(
                                chuDe,
                                soLuong
                        )
                );

        requestHienTai = request;

        request.enqueue(new Callback<KaiCreateVocabularyResponse>() {
            @Override
            public void onResponse(
                    @NonNull Call<KaiCreateVocabularyResponse> call,
                    @NonNull Response<KaiCreateVocabularyResponse> response) {

                if (!conHieuLuc(phienYeuCau)) {
                    return;
                }

                if (!response.isSuccessful()) {
                    ketThucVoiLoi(response.code());
                    return;
                }

                KaiCreateVocabularyResponse body = response.body();

                if (
                        body == null ||
                                !body.isThanhCong() ||
                                body.getData() == null
                ) {
                    ketThucVoiThongBao(
                            "Kai chưa nhận được kết quả tạo bộ từ hợp lệ."
                    );
                    return;
                }

                KaiCreateVocabularyResponse.DuLieu duLieu =
                        body.getData();

                int boTuId = duLieu.getBoTuId();
                String tenBoTu = duLieu.getTenBoTu();

                if (boTuId <= 0 ||
                        tenBoTu == null ||
                        tenBoTu.trim().isEmpty()) {
                    ketThucVoiThongBao(
                            "Backend đã phản hồi nhưng thiếu thông tin bộ từ."
                    );
                    return;
                }

                String thongBao =
                        "Đã tạo thành công bộ từ \""
                                + tenBoTu
                                + "\" với "
                                + duLieu.getSoTu()
                                + " từ vựng.";

                if (moLuyenTap) {
                    thongBao += "\nKai đang mở Luyện tập...";
                }

                ketThucVoiThongBao(thongBao);

                if (moLuyenTap && conHieuLuc(phienYeuCau)) {
                    if (requireActivity() instanceof MainActivity) {
                        ((MainActivity) requireActivity())
                                .chuyenDenLuyenTap(
                                        boTuId,
                                        tenBoTu
                                );
                    }
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<KaiCreateVocabularyResponse> call,
                    @NonNull Throwable t) {

                if (!conHieuLuc(phienYeuCau)) {
                    return;
                }

                if (call.isCanceled()) {
                    return;
                }

                ketThucVoiThongBao(
                        "Không nhận được kết quả tạo bộ từ. "
                                + "Bạn hãy kiểm tra Bộ từ của tôi trước "
                                + "khi thử lại để tránh tạo trùng."
                );
            }
        });
    }

    private void ketThucVoiThongBao(String noiDung) {
        requestHienTai = null;
        dangGui = false;
        capNhatNutGui();
        thayTinNhanCho(noiDung);
    }

    private void ketThucVoiLoi(int maLoi) {
        if (maLoi == 401 || maLoi == 403) {
            ketThucVoiThongBao(
                    "Phiên đăng nhập không hợp lệ hoặc đã hết hạn. "
                            + "Bạn hãy đăng nhập lại nhé."
            );
            return;
        }

        if (maLoi == 429) {
            ketThucVoiThongBao(
                    "Kai đang nhận quá nhiều yêu cầu hoặc "
                            + "đã hết hạn mức Gemini. Bạn thử lại sau nhé."
            );
            return;
        }

        if (maLoi == 503) {
            ketThucVoiThongBao(
                    "Dịch vụ AI chưa được cấu hình."
            );
            return;
        }

        if (maLoi == 502 || maLoi >= 500) {
            ketThucVoiThongBao(
                    "Dịch vụ AI đang gặp sự cố. Bạn thử lại sau nhé."
            );
            return;
        }

        ketThucVoiThongBao(
                "Kai không thể xử lý yêu cầu này. Mã lỗi: " + maLoi
        );
    }

    private String thongBaoLoiKetNoi(Throwable t) {
        if (t instanceof IOException) {
            return "Không thể kết nối đến máy chủ Kenglish. "
                    + "Hãy kiểm tra mạng và backend nhé.";
        }

        return "Kai gặp lỗi khi xử lý yêu cầu. Bạn thử lại sau nhé.";
    }

    private String lamSachMarkdown(String noiDung) {
        if (noiDung == null) {
            return "";
        }

        return noiDung
                .replaceAll("(?m)^\\s{0,3}#{1,6}\\s*", "")
                .replace("**", "")
                .replace("__", "")
                .replaceAll("(?m)^\\s*[-*_]{3,}\\s*$", "")
                .replaceAll("(?m)^\\s*[-*+]\\s+", "• ")
                .replaceAll("\n{3,}", "\n\n")
                .trim();
    }

    private boolean conHieuLuc(int phienYeuCau) {
        return isAdded()
                && getView() != null
                && phienYeuCau == phienChat;
    }

    private void themTinNhan(String noiDung, boolean nguoiDung) {
        int viTri = danhSachTinNhan.size();

        danhSachTinNhan.add(
                new TinNhanKai(noiDung, nguoiDung)
        );

        adapter.notifyItemInserted(viTri);
        capNhatGiaoDien();
        cuonXuongCuoi();
    }

    private void thayTinNhanCho(String noiDung) {
        if (danhSachTinNhan.isEmpty()) {
            return;
        }

        int viTri = danhSachTinNhan.size() - 1;

        danhSachTinNhan.set(
                viTri,
                new TinNhanKai(noiDung, false)
        );

        adapter.notifyItemChanged(viTri);
        cuonXuongCuoi();
    }

    private void cuonXuongCuoi() {
        if (recyclerTinNhan == null ||
                danhSachTinNhan.isEmpty()) {
            return;
        }

        recyclerTinNhan.post(() -> {
            if (
                    recyclerTinNhan.getAdapter() != null &&
                            !danhSachTinNhan.isEmpty()
            ) {
                recyclerTinNhan.scrollToPosition(
                        danhSachTinNhan.size() - 1
                );
            }
        });
    }

    private void capNhatGiaoDien() {
        boolean coTinNhan = !danhSachTinNhan.isEmpty();

        layoutChao.setVisibility(
                coTinNhan ? View.GONE : View.VISIBLE
        );

        recyclerTinNhan.setVisibility(
                coTinNhan ? View.VISIBLE : View.GONE
        );
    }

    private void capNhatNutGui() {
        if (btnGui == null) {
            return;
        }

        btnGui.setEnabled(!dangGui);
        btnGui.setAlpha(dangGui ? 0.5f : 1.0f);
    }

    private void lamMoiChat() {
        phienChat++;

        if (hopThoaiXacNhan != null) {
            hopThoaiXacNhan.dismiss();
            hopThoaiXacNhan = null;
        }

        if (requestHienTai != null) {
            requestHienTai.cancel();
            requestHienTai = null;
        }

        dangGui = false;
        capNhatNutGui();

        int soTinNhan = danhSachTinNhan.size();
        danhSachTinNhan.clear();

        if (soTinNhan > 0) {
            adapter.notifyItemRangeRemoved(0, soTinNhan);
        }

        edtTinNhan.setText("");
        capNhatGiaoDien();
    }

    private void anBanPhim() {
        if (getContext() == null) {
            return;
        }

        InputMethodManager imm =
                (InputMethodManager)
                        requireContext().getSystemService(
                                Context.INPUT_METHOD_SERVICE
                        );

        if (imm != null) {
            imm.hideSoftInputFromWindow(
                    edtTinNhan.getWindowToken(),
                    0
            );
        }

        edtTinNhan.clearFocus();
    }

    @Override
    public void onDestroyView() {
        phienChat++;

        if (hopThoaiXacNhan != null) {
            hopThoaiXacNhan.dismiss();
            hopThoaiXacNhan = null;
        }

        if (requestHienTai != null) {
            requestHienTai.cancel();
            requestHienTai = null;
        }

        dangGui = false;

        super.onDestroyView();
    }
}