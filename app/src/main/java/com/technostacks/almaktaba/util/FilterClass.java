package com.technostacks.almaktaba.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.MediaStore;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import java.io.IOException;
import static org.opencv.android.Utils.bitmapToMat;

/**
 * Created by techno-110 on 15/3/17.
 */

public class FilterClass {

    /*static {
        System.loadLibrary("AndroidImageFilter");
    }*/

    public static Bitmap convertBitmapToBinary(Bitmap bmpOriginal) {

        Bitmap binaryBitmap = bmpOriginal.copy(
                Bitmap.Config.ARGB_8888, true);

        Canvas canvas = new Canvas(binaryBitmap);

        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);

        float m = 255f;
        float t = -255 * 128f;
        ColorMatrix threshold = new ColorMatrix(new float[]{
                m, 0, 0, 1, t,
                0, m, 0, 1, t,
                0, 0, m, 1, t,
                0, 0, 0, 1, 0
        });
        colorMatrix.postConcat(threshold);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(binaryBitmap, 0, 0, paint);
        return binaryBitmap;
    }

    /*public static Bitmap convertBitmapToCool(Bitmap bitmap) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        int[] returnPixels = NativeFilterFunc.averageSmooth(pixels, width, height, 10);
        Bitmap returnBitmap = Bitmap.createBitmap(returnPixels, width, height, Bitmap.Config.ARGB_8888);
        return returnBitmap;
    }


    public static Bitmap convertBitmapToEnhance(Bitmap bitmap) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        int[] returnPixels = NativeFilterFunc.hdrFilter(pixels, width, height);
        Bitmap returnBitmap = Bitmap.createBitmap(returnPixels, width, height, Bitmap.Config.ARGB_8888);
        return returnBitmap;
    }

    public static Bitmap convertBitmapToDynamic(Bitmap bitmap) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        int[] returnPixels = NativeFilterFunc.sketchFilter(pixels, width, height);
        Bitmap returnBitmap = Bitmap.createBitmap(returnPixels, width, height, Bitmap.Config.ARGB_8888);
        return returnBitmap;
    }

    public static Bitmap convertBitmapToContrast(Bitmap bitmap) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        int[] returnPixels = NativeFilterFunc.softGlow(pixels, width, height, 0.9);
        Bitmap returnBitmap = Bitmap.createBitmap(returnPixels, width, height, Bitmap.Config.ARGB_8888);
        return returnBitmap;
    }*/

    public static Bitmap setDefaultValues(Bitmap bmp) {

        Mat srcMat = new Mat();
        bitmapToMat(bmp, srcMat, true);
        final Bitmap bitmap = Bitmap.createBitmap(srcMat.clone().width(), srcMat.clone().height(), Bitmap.Config.ARGB_8888);
        Imgproc.cvtColor(srcMat, srcMat, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(srcMat, srcMat, new Size(3, 3), 0.9);
        Imgproc.adaptiveThreshold(srcMat, srcMat, 250, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 9, 13);
        org.opencv.android.Utils.matToBitmap(srcMat, bitmap, true);

        return bitmap;
    }
//    public static native int[] sharpenFilter(int[] pixels, int width, int height);

    public static Bitmap convertBitmapToWarm(Bitmap bmpOriginal) {

        Bitmap sepiaBitmap = bmpOriginal.copy(
                Bitmap.Config.ARGB_8888, true);

        Canvas canvas = new Canvas(sepiaBitmap);

        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);

        ColorMatrix colorScale = new ColorMatrix();
        colorScale.setScale(1, 1, 0.8f, 1);
        colorMatrix.postConcat(colorScale);

        ColorMatrix cm = new ColorMatrix(
                new float[]{1, 0, 0, 0, 0,
                        0, 1, 0, 0, 0,
                        0, 0, (float) 0.8, 0, 0,
                        0, 0, 0, 1, 0
                });

        cm.postConcat(colorMatrix);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(sepiaBitmap, 0, 0, paint);
        return sepiaBitmap;
    }


    public static Bitmap convertBitmapToGray(Uri bitmapUri, Context context) {

        Bitmap bmpGray = null;
        try {
            Bitmap bmpOriginal = MediaStore.Images.Media.getBitmap(context.getContentResolver(), bitmapUri);

            if (bmpOriginal.getWidth() > 1280 || bmpOriginal.getHeight() > 1280) {
                int nh = (int) (bmpOriginal.getHeight() * (512.0 / bmpOriginal.getWidth()));
                bmpOriginal = Bitmap.createScaledBitmap(bmpOriginal, 512, nh, true);
            }

            bmpGray = bmpOriginal.copy(Bitmap.Config.ARGB_8888, true);
            ColorMatrix colorMatrix = new ColorMatrix();
            colorMatrix.setSaturation(0);

            Canvas canvas = new Canvas(bmpGray);

            ColorMatrix cm = new ColorMatrix(new float[]{-1, 0, 0, 0, 255,
                    0, -1, 0, 0, 255,
                    0, 0, -1, 0, 255,
                    0, 0, 0, 1, 0});

            cm.postConcat(colorMatrix);
            Paint paint = new Paint();
            paint.setColorFilter(new ColorMatrixColorFilter(cm));
            canvas.drawBitmap(bmpGray, 0, 0, paint);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return bmpGray;
    }

    public static Bitmap convertBitmapToGray(Bitmap bmpOriginal) {

        Bitmap bmpGray = bmpOriginal.copy(
                Bitmap.Config.ARGB_8888, true);
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);

        Canvas canvas = new Canvas(bmpGray);

        ColorMatrix cm = new ColorMatrix(new float[]{-1, 0, 0, 0, 255,
                0, -1, 0, 0, 255,
                0, 0, -1, 0, 255,
                0, 0, 0, 1, 0});

        cm.postConcat(colorMatrix);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmpGray, 0, 0, paint);
        return bmpGray;
    }

    public static Bitmap convertColorIntoBlackAndWhiteImage(Bitmap orginalBitmap) {


        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);

        ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(
                colorMatrix);

        Bitmap blackAndWhiteBitmap = orginalBitmap.copy(
                Bitmap.Config.ARGB_8888, true);

        Paint paint = new Paint();
        paint.setColorFilter(colorMatrixFilter);

        Canvas canvas = new Canvas(blackAndWhiteBitmap);
        canvas.drawBitmap(blackAndWhiteBitmap, 0, 0, paint);

        return blackAndWhiteBitmap;
    }

    public static Bitmap convertColorIntoBlackAndWhiteImage(Uri bitmapUri, Context context) {

        Bitmap bmpBlackWhite = null;

        try {
            Bitmap bmpOriginal = MediaStore.Images.Media.getBitmap(context.getContentResolver(), bitmapUri);
            if (bmpOriginal.getWidth() > 1280 || bmpOriginal.getHeight() > 1280) {
                int nh = (int) (bmpOriginal.getHeight() * (512.0 / bmpOriginal.getWidth()));
                bmpOriginal = Bitmap.createScaledBitmap(bmpOriginal, 512, nh, true);
            }

            ColorMatrix colorMatrix = new ColorMatrix();
            colorMatrix.setSaturation(0);

            ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(
                    colorMatrix);

            bmpBlackWhite = bmpOriginal.copy(
                    Bitmap.Config.ARGB_8888, true);

            Paint paint = new Paint();
            paint.setColorFilter(colorMatrixFilter);

            Canvas canvas = new Canvas(bmpBlackWhite);
            canvas.drawBitmap(bmpBlackWhite, 0, 0, paint);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpBlackWhite;
    }

    public static Bitmap adjustedContrast(Bitmap src, double value) {
        // image size
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap

        // create a mutable empty bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());

        // create a canvas so that we can draw the bmOut Bitmap from source bitmap
        Canvas c = new Canvas();
        c.setBitmap(bmOut);

        // draw bitmap to bmOut from src bitmap so we can modify it
        c.drawBitmap(src, 0, 0, new Paint(Color.BLACK));


        // color information
        int A, R, G, B;
        int pixel;
        // get contrast value
        double contrast = Math.pow((100 + value) / 100, 2);

        // scan through all pixels
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                // apply filter contrast for every channel R, G, B
                R = Color.red(pixel);
                R = (int) (((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if (R < 0) {
                    R = 0;
                } else if (R > 255) {
                    R = 255;
                }

                G = Color.green(pixel);
                G = (int) (((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if (G < 0) {
                    G = 0;
                } else if (G > 255) {
                    G = 255;
                }

                B = Color.blue(pixel);
                B = (int) (((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if (B < 0) {
                    B = 0;
                } else if (B > 255) {
                    B = 255;
                }

                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }
        return bmOut;
    }
}
