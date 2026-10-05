package com.example.kenglish.model;

public class MucBoTu {

    public static final int LOAI_FOLDER = 0;
    public static final int LOAI_BO_TU = 1;

    private final int loai;

    private final Folder folder;
    private final BoTuModel boTu;


    /*
     * Item Folder.
     */
    public MucBoTu(Folder folder) {

        this.loai = LOAI_FOLDER;

        this.folder = folder;

        this.boTu = null;
    }


    /*
     * Item Bộ từ.
     */
    public MucBoTu(BoTuModel boTu) {

        this.loai = LOAI_BO_TU;

        this.boTu = boTu;

        this.folder = null;
    }


    public int getLoai() {
        return loai;
    }


    public Folder getFolder() {
        return folder;
    }


    public BoTuModel getBoTu() {
        return boTu;
    }
}