package com.example.kenglish;

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
import com.example.kenglish.model.TinNhanKai;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
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

    private final List<TinNhanKai> danhSachTinNhan =
            new ArrayList<>();

    private ApiService apiService;
    private Call<AiChatResponse> requestHienTai;

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

        return inflater.inflate(
                R.layout.kai_chat,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        recyclerTinNhan = view.findViewById(
                R.id.recycler_tin_nhan_kai
        );

        layoutChao = view.findViewById(
                R.id.layout_chao_kai
        );

        edtTinNhan = view.findViewById(
                R.id.edt_tin_nhan_kai
        );

        btnGui = view.findViewById(
                R.id.btn_gui_kai
        );

        btnLamMoi = view.findViewById(
                R.id.btn_lam_moi_chat
        );

        apiService = RetrofitClient.getClient()
                .create(ApiService.class);

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
                "Gợi ý 10 từ vựng chủ đề công nghệ"
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

    private void ganSuKienGoiY(
            View view,
            int id,
            String cauHoi) {

        TextView goiY = view.findViewById(id);

        goiY.setOnClickListener(v -> {

            if (dangGui) {
                return;
            }

            edtTinNhan.setText(cauHoi);
            guiTinNhan();
        });
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
            edtTinNhan.setError(
                    "Câu hỏi tối đa 2000 ký tự"
            );
            return;
        }

        SharedPreferences preferences =
                requireContext().getSharedPreferences(
                        "Kenglish",
                        Context.MODE_PRIVATE
                );

        String token = preferences.getString(
                "token",
                ""
        );

        if (token == null || token.trim().isEmpty()) {
            themTinNhan(
                    "Bạn cần đăng nhập để sử dụng Kai.",
                    false
            );
            return;
        }

        edtTinNhan.setText("");
        anBanPhim();

        themTinNhan(noiDung, true);

        dangGui = true;
        capNhatNutGui();

        // Hiển thị tin nhắn chờ.
        themTinNhan(
                "Kai đang suy nghĩ...",
                false
        );

        final int phienYeuCau = phienChat;

        AiChatRequest request =
                new AiChatRequest(noiDung);

        requestHienTai = apiService.chatKai(
                "Bearer " + token,
                request
        );

        requestHienTai.enqueue(
                new Callback<AiChatResponse>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<AiChatResponse> call,
                            @NonNull Response<AiChatResponse> response) {

                        if (!conHieuLuc(phienYeuCau)) {
                            return;
                        }

                        dangGui = false;
                        capNhatNutGui();

                        if (response.isSuccessful()) {

                            AiChatResponse body = response.body();

                            if (body != null
                                    && body.isSuccess()
                                    && body.getData() != null
                                    && body.getData().getTraLoi() != null
                                    && !body.getData()
                                    .getTraLoi().trim().isEmpty()) {

                                thayTinNhanCho(
                                        lamSachMarkdown(body.getData().getTraLoi().trim())
                                );
                                return;
                            }

                            thayTinNhanCho(
                                    "Kai chưa nhận được câu trả lời hợp lệ. "
                                            + "Bạn thử lại nhé."
                            );
                            return;
                        }

                        xuLyLoiHttp(response);
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<AiChatResponse> call,
                            @NonNull Throwable t) {

                        if (!conHieuLuc(phienYeuCau)) {
                            return;
                        }

                        dangGui = false;
                        capNhatNutGui();

                        if (call.isCanceled()) {
                            return;
                        }

                        if (t instanceof IOException) {
                            thayTinNhanCho(
                                    "Không thể kết nối đến máy chủ Kenglish. "
                                            + "Hãy kiểm tra mạng và backend nhé."
                            );
                        } else {
                            thayTinNhanCho(
                                    "Kai gặp lỗi khi xử lý câu trả lời. "
                                            + "Bạn thử lại sau nhé."
                            );
                        }
                    }
                }
        );
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

    private void xuLyLoiHttp(
            Response<AiChatResponse> response) {

        int maLoi = response.code();

        if (maLoi == 401 || maLoi == 403) {
            thayTinNhanCho(
                    "Phiên đăng nhập không hợp lệ hoặc đã hết hạn. "
                            + "Bạn hãy đăng nhập lại nhé."
            );
            return;
        }

        if (maLoi == 429) {
            thayTinNhanCho(
                    "Kai đang nhận quá nhiều yêu cầu hoặc "
                            + "đã hết hạn mức AI miễn phí. "
                            + "Bạn thử lại sau nhé."
            );
            return;
        }

        if (maLoi == 503) {
            thayTinNhanCho(
                    "Dịch vụ AI chưa được cấu hình. "
                            + "Vui lòng liên hệ quản trị viên."
            );
            return;
        }

        if (maLoi == 502 || maLoi >= 500) {
            thayTinNhanCho(
                    "Dịch vụ AI đang gặp sự cố. "
                            + "Bạn thử lại sau nhé."
            );
            return;
        }

        String thongBao = "Kai không thể xử lý yêu cầu này.";

        if (response.errorBody() != null) {
            try {
                String chiTiet = response.errorBody().string();

                if (chiTiet.contains("2000")) {
                    thongBao = "Câu hỏi vượt quá 2000 ký tự.";
                }
            } catch (IOException ignored) {
            }
        }

        thayTinNhanCho(thongBao);
    }

    private boolean conHieuLuc(int phienYeuCau) {
        return isAdded()
                && getView() != null
                && phienYeuCau == phienChat;
    }

    private void themTinNhan(
            String noiDung,
            boolean nguoiDung) {

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

        // Tin nhắn cuối là placeholder của Kai.
        danhSachTinNhan.set(
                viTri,
                new TinNhanKai(noiDung, false)
        );

        adapter.notifyItemChanged(viTri);
        cuonXuongCuoi();
    }

    private void cuonXuongCuoi() {

        if (recyclerTinNhan == null
                || danhSachTinNhan.isEmpty()) {
            return;
        }

        recyclerTinNhan.post(() -> {
            if (recyclerTinNhan.getAdapter() != null
                    && !danhSachTinNhan.isEmpty()) {

                recyclerTinNhan.scrollToPosition(
                        danhSachTinNhan.size() - 1
                );
            }
        });
    }

    private void capNhatGiaoDien() {

        boolean coTinNhan =
                !danhSachTinNhan.isEmpty();

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

        // Vô hiệu hóa phản hồi của cuộc trò chuyện cũ.
        phienChat++;

        if (requestHienTai != null) {
            requestHienTai.cancel();
            requestHienTai = null;
        }

        dangGui = false;
        capNhatNutGui();

        int soTinNhan = danhSachTinNhan.size();

        danhSachTinNhan.clear();

        if (soTinNhan > 0) {
            adapter.notifyItemRangeRemoved(
                    0,
                    soTinNhan
            );
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

        // Ngăn callback cập nhật View đã bị hủy.
        phienChat++;

        if (requestHienTai != null) {
            requestHienTai.cancel();
            requestHienTai = null;
        }

        dangGui = false;

        super.onDestroyView();
    }
}
