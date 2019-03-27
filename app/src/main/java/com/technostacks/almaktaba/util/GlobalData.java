package com.technostacks.almaktaba.util;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import com.technostacks.almaktaba.model.BitmapListModel;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by techno-110 on 23/12/16.
 */
public class GlobalData implements Serializable {

    public static GlobalData instance;
    public static List<BitmapListModel> bitmapListModels;
    public static boolean isAutoDetect = false, isCapturing = false;
    public static boolean isFirstTimeForManual = true, isFirstTimeForAuto = true;
    public static int iscameraFlashOn = 0;
    public static Bitmap bitmap;
    public static ArrayList<Long> downloadIdList;

    public static void setBitmap(Bitmap bitmap) {
        GlobalData.bitmap = bitmap;
    }

    public GlobalData() {
        bitmapListModels = new ArrayList<>();
    }

    public static GlobalData getInstance() {
        if (instance == null)
            instance = new GlobalData();
        return instance;
    }

    public static void addDownloaId(long id){
        if (downloadIdList == null)
            downloadIdList = new ArrayList<>();

        downloadIdList.add(id);
    }

    public static ArrayList<Long> getDownloadIdList(){
        if (downloadIdList!=null && !downloadIdList.isEmpty())
            return downloadIdList;
        return null;
    }

    public static Bitmap getBitmap() {
        return bitmap;
    }

    public static void setBitmapListModel(BitmapListModel bitmapListModel) {
        bitmapListModels.add(bitmapListModel);
    }


    public static List<BitmapListModel> getBitmapsListModel() {
        return bitmapListModels;
    }

    public static void deleteBitmapsFromList(int pos) {
        bitmapListModels.remove(pos);
    }

    public static void resetBitmapListModel() {
        bitmapListModels.clear();
    }

    public static void resetBitmap(){
        bitmap = null;
    }
}
