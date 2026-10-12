package com.example.kenglish;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView thanhDieuHuong;
    private View nutNoiKai;
    private View khungKaiChat;

    private Fragment trangChu;
    private Fragment boTu;
    private Fragment luyenTap;
    private Fragment xepHang;
    private Fragment cuaHang;
    private Fragment caNhan;

    private Fragment manHinhHienTai;

    private static final String TAG_KAI = "KAI_CHAT";
    private static final String BACKSTACK_KAI = "kai_chat";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        anhXa();
        khoiTaoFragment(savedInstanceState);
        thietLapDieuHuong();
        thietLapMauThanhDieuHuong();
        thietLapKai();

        getSupportFragmentManager()
                .addOnBackStackChangedListener(
                        this::capNhatTrangThaiKai
                );

        capNhatTrangThaiKai();
    }

    private void anhXa() {
        thanhDieuHuong = findViewById(R.id.thanh_dieu_huong);
        nutNoiKai = findViewById(R.id.khung_nut_noi_kai);
        khungKaiChat = findViewById(R.id.khung_kai_chat);

        thanhDieuHuong.setLabelVisibilityMode(
                com.google.android.material.navigation.NavigationBarView
                        .LABEL_VISIBILITY_LABELED
        );

        thanhDieuHuong.setItemActiveIndicatorEnabled(true);

        thanhDieuHuong.setItemActiveIndicatorColor(
                ColorStateList.valueOf(
                        Color.parseColor("#EAF2FF")
                )
        );
    }

    private void khoiTaoFragment(Bundle savedInstanceState) {
        FragmentManager fm = getSupportFragmentManager();

        if (savedInstanceState == null) {
            trangChu = new TrangChu();
            boTu = new BoTu();
            luyenTap = new LuyenTap();
            xepHang = new XepHang();
            cuaHang = new CuaHang();
            caNhan = new CaNhan();

            fm.beginTransaction()
                    .add(R.id.khung_noi_dung, caNhan, "CA_NHAN")
                    .hide(caNhan)
                    .add(R.id.khung_noi_dung, cuaHang, "CUA_HANG")
                    .hide(cuaHang)
                    .add(R.id.khung_noi_dung, xepHang, "XEP_HANG")
                    .hide(xepHang)
                    .add(R.id.khung_noi_dung, luyenTap, "LUYEN_TAP")
                    .hide(luyenTap)
                    .add(R.id.khung_noi_dung, boTu, "BO_TU")
                    .hide(boTu)
                    .add(R.id.khung_noi_dung, trangChu, "TRANG_CHU")
                    .commit();

            manHinhHienTai = trangChu;

            thanhDieuHuong.setSelectedItemId(
                    R.id.menu_trang_chu
            );
            return;
        }

        trangChu = fm.findFragmentByTag("TRANG_CHU");
        boTu = fm.findFragmentByTag("BO_TU");
        luyenTap = fm.findFragmentByTag("LUYEN_TAP");
        xepHang = fm.findFragmentByTag("XEP_HANG");
        cuaHang = fm.findFragmentByTag("CUA_HANG");
        caNhan = fm.findFragmentByTag("CA_NHAN");

        if (trangChu != null && !trangChu.isHidden()) {
            manHinhHienTai = trangChu;
        } else if (boTu != null && !boTu.isHidden()) {
            manHinhHienTai = boTu;
        } else if (luyenTap != null && !luyenTap.isHidden()) {
            manHinhHienTai = luyenTap;
        } else if (xepHang != null && !xepHang.isHidden()) {
            manHinhHienTai = xepHang;
        } else if (cuaHang != null && !cuaHang.isHidden()) {
            manHinhHienTai = cuaHang;
        } else if (caNhan != null && !caNhan.isHidden()) {
            manHinhHienTai = caNhan;
        }
    }

    private void thietLapKai() {
        nutNoiKai.setOnClickListener(v -> moKai());
    }

    private void moKai() {
        FragmentManager fm = getSupportFragmentManager();

        if (fm.isStateSaved()) {
            return;
        }

        if (fm.findFragmentByTag(TAG_KAI) != null) {
            return;
        }

        khungKaiChat.setVisibility(View.VISIBLE);
        nutNoiKai.setVisibility(View.GONE);

        fm.beginTransaction()
                .setReorderingAllowed(true)
                .add(
                        R.id.khung_kai_chat,
                        new KaiChat(),
                        TAG_KAI
                )
                .addToBackStack(BACKSTACK_KAI)
                .commit();
    }

    private void capNhatTrangThaiKai() {
        Fragment kai = getSupportFragmentManager()
                .findFragmentByTag(TAG_KAI);

        boolean dangMoKai = kai != null
                && kai.isAdded()
                && !kai.isRemoving();

        khungKaiChat.setVisibility(
                dangMoKai ? View.VISIBLE : View.GONE
        );

        nutNoiKai.setVisibility(
                dangMoKai ? View.GONE : View.VISIBLE
        );
    }

    private void dongKai() {
        FragmentManager fm = getSupportFragmentManager();

        if (fm.isStateSaved()) {
            return;
        }

        Fragment kai = fm.findFragmentByTag(TAG_KAI);

        if (kai == null) {
            capNhatTrangThaiKai();
            return;
        }

        fm.popBackStackImmediate(
                BACKSTACK_KAI,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
        );

        capNhatTrangThaiKai();
    }

    private void thietLapDieuHuong() {
        thanhDieuHuong.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.menu_trang_chu) {
                chuyenManHinh(trangChu);
                return true;
            }

            if (id == R.id.menu_bo_tu) {
                chuyenManHinh(boTu);
                return true;
            }

            if (id == R.id.menu_luyen_tap) {
                chuyenManHinh(luyenTap);
                return true;
            }

            if (id == R.id.menu_xep_hang) {
                chuyenManHinh(xepHang);
                return true;
            }

            if (id == R.id.menu_cua_hang) {
                chuyenManHinh(cuaHang);
                return true;
            }

            if (id == R.id.menu_ca_nhan) {
                chuyenManHinh(caNhan);
                return true;
            }

            return false;
        });
    }

    private void chuyenManHinh(Fragment manHinhMoi) {
        if (manHinhMoi == null) {
            return;
        }

        FragmentManager fm = getSupportFragmentManager();

        if (fm.isStateSaved()) {
            return;
        }

        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStackImmediate(
                    null,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
            );
        }

        capNhatTrangThaiKai();

        if (manHinhMoi == manHinhHienTai) {
            return;
        }

        if (manHinhHienTai == null) {
            return;
        }

        fm.beginTransaction()
                .hide(manHinhHienTai)
                .show(manHinhMoi)
                .commit();

        manHinhHienTai = manHinhMoi;
    }

    private void thietLapMauThanhDieuHuong() {
        int[][] trangThai = new int[][]{
                new int[]{android.R.attr.state_checked},
                new int[]{-android.R.attr.state_checked}
        };

        int[] mauIcon = new int[]{
                Color.parseColor("#245AA8"),
                Color.parseColor("#91A0B5")
        };

        int[] mauChu = new int[]{
                Color.parseColor("#245AA8"),
                Color.parseColor("#91A0B5")
        };

        thanhDieuHuong.setItemIconTintList(
                new ColorStateList(trangThai, mauIcon)
        );

        thanhDieuHuong.setItemTextColor(
                new ColorStateList(trangThai, mauChu)
        );

        thanhDieuHuong.setItemIconSize(dpToPx(22));

        thanhDieuHuong.setItemTextAppearanceActive(
                R.style.KenglishBottomNavTextActive
        );

        thanhDieuHuong.setItemTextAppearanceInactive(
                R.style.KenglishBottomNavTextInactive
        );
    }

    private int dpToPx(int dp) {
        return Math.round(
                dp * getResources().getDisplayMetrics().density
        );
    }

    public void chuyenDenLuyenTap(int boTuId, String tenBoTu) {
        if (!(luyenTap instanceof LuyenTap) || boTuId <= 0) {
            return;
        }

        ((LuyenTap) luyenTap).chonBoTuTuManHinhKhac(
                boTuId,
                tenBoTu
        );

        if (thanhDieuHuong.getSelectedItemId()
                == R.id.menu_luyen_tap) {

            chuyenManHinh(luyenTap);
        } else {
            thanhDieuHuong.setSelectedItemId(
                    R.id.menu_luyen_tap
            );
        }
    }
}
