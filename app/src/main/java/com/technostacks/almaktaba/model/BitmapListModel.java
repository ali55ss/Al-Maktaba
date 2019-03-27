package com.technostacks.almaktaba.model;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by techno-110 on 3/4/17.
 */
public class BitmapListModel {

    Bitmap bitmap;
    Uri bitmapUri;
    boolean isFromGallary;
    String nameOfImage = "";

    public BitmapListModel(Bitmap bitmap, Uri bitmapUri, boolean isFromGallary, String nameOfImage) {
        this.bitmap = bitmap;
        this.bitmapUri = bitmapUri;
        this.isFromGallary = isFromGallary;
        this.nameOfImage = nameOfImage;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Uri getBitmapUri() {
        return bitmapUri;
    }

    public void setBitmapUri(Uri bitmapUri) {
        this.bitmapUri = bitmapUri;
    }

    public boolean isFromGallary() {
        return isFromGallary;
    }

    public void setFromGallary(boolean fromGallary) {
        isFromGallary = fromGallary;
    }

    public String getNameOfImage() {
        return nameOfImage;
    }

    public void setNameOfImage(String nameOfImage) {
        this.nameOfImage = nameOfImage;
    }

    @Override
    public String toString() {
        return "BitmapListModel{" +
                "bitmap=" + bitmap +
                ", bitmapUri='" + bitmapUri + '\'' +
                ", isFromGallary=" + isFromGallary +
                ", nameOfImage=" + nameOfImage +
                '}';
    }
}
