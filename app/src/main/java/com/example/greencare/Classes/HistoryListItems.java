package com.example.greencare.Classes;

import android.graphics.Bitmap;

public class HistoryListItems {
    private String result;
    private Bitmap bitmap;
    private String stage;

    public HistoryListItems(String result, Bitmap bitmap, String stage) {
        this.result = result;
        this.bitmap = bitmap;
        this.stage = stage;
    }
    public String getResult() {
        return result;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public String getStage() {
        return stage;
    }




}
