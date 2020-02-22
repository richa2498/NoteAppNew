package com.rudrik.noteapp.adapters;

import android.graphics.Bitmap;

public class SliderItem {
    private Bitmap image;

    public SliderItem(Bitmap image) {
        this.image = image;
    }

    public Bitmap getImage() {
        return image;
    }
}
