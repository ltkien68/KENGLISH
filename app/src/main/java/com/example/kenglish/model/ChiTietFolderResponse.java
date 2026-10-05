package com.example.kenglish.model;

import java.util.List;

public class ChiTietFolderResponse {

    private Folder folder;
    private List<BoTuModel> bo_tu;

    public Folder getFolder() {
        return folder;
    }

    public List<BoTuModel> getBoTu() {
        return bo_tu;
    }
}