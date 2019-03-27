package com.technostacks.almaktaba.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.technostacks.almaktaba.R;
import com.technostacks.almaktaba.util.Const;
import com.technostacks.almaktaba.util.GlobalData;
import com.technostacks.almaktaba.util.PolygonView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CropImageActivity extends AppCompatActivity implements View.OnClickListener {

    String TAG = CropImageActivity.class.getName();
    ImageView imageView;
    PolygonView polygonView;
    Bitmap tempBitmap;
    private String FROM = "";
    int currentPosition;
    FrameLayout fl_total;

    int height1;
    int width1;
    Map<Integer, PointF> pointFs;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.e("cropimageActivity", "OpenCV loaded successfully");
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        if (!OpenCVLoader.initDebug()) {
            Log.d("asd", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
        } else {
            Log.d("asd", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);

        polygonView = (PolygonView) findViewById(R.id.polygonView);
        imageView = (ImageView) findViewById(R.id.sourceImageView);
        fl_total = (FrameLayout) findViewById(R.id.fl_total);

        AdView mAdView = findViewById(R.id.crop_adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_cropImage);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ImageView ivCancel = (ImageView) toolbar.findViewById(R.id.iv_toolbar_crop_cancel);
        ImageView ivSave = (ImageView) toolbar.findViewById(R.id.iv_toolbar_crop_done);

        FROM = getIntent().getStringExtra(Const.FROM);

        currentPosition = getIntent().getIntExtra("position", 0);
        tempBitmap = GlobalData.getBitmapsListModel().get(currentPosition).getBitmap();

        imageView.setImageBitmap(tempBitmap);
        pointFs = getEdgePoints(tempBitmap);

        ivCancel.setOnClickListener(this);
        ivSave.setOnClickListener(this);
    }

    private LinkedHashMap<Integer, PointF> getEdgePoints(final Bitmap tempBitmap) {
        LinkedHashMap<Integer, PointF> orderedPoints = new LinkedHashMap<>();

        fl_total.post(new Runnable() {

            @Override
            public void run() {

                height1 = fl_total.getMeasuredHeight();
                width1 = fl_total.getMeasuredWidth();
                int imageHeight = imageView.getHeight();
                int imageWidth = imageView.getWidth();

                pointFs = getEdgePoints1(height1, width1, tempBitmap, imageHeight, imageWidth);
            }
        });

        return orderedPoints;
    }

    private LinkedHashMap<Integer, PointF> getEdgePoints1(int height, int width, Bitmap bitmap,
                                                          int imageHeight, int imageWidth) {

        LinkedHashMap<Integer, PointF> orderedPoints = new LinkedHashMap<>();

        int Height, Width;

        PointF point0, point1, point2, point3;

        if (height < bitmap.getHeight()) {
            Height = height;
            if (width < bitmap.getWidth()) {
                Width = width;
            } else {
                Width = bitmap.getWidth();
            }
        } else if (width < bitmap.getWidth()) {
            Width = width;
            if (height < bitmap.getHeight()) {
                Height = height;
            } else {
                Height = bitmap.getHeight();
            }
        } else {
            Height = bitmap.getHeight();
            Width = bitmap.getWidth();
        }

        if (Height > imageHeight)
            Height = imageHeight;

        if (Width > imageWidth)
            Width = imageWidth;

        point0 = new PointF(0, 0);
        point1 = new PointF(Width, 0);
        point2 = new PointF(0, Height);
        point3 = new PointF(Width, Height);

        orderedPoints.put(0, point0);
        orderedPoints.put(1, point1);
        orderedPoints.put(2, point2);
        orderedPoints.put(3, point3);

        polygonView.setPoints(orderedPoints);
        polygonView.setVisibility(View.VISIBLE);

        int padding = dpToPx(20);

        FrameLayout.LayoutParams layoutParams;
        layoutParams = new FrameLayout.LayoutParams(Width + padding, Height + padding);
        layoutParams.gravity = Gravity.CENTER;
        polygonView.setLayoutParams(layoutParams);

        return orderedPoints;
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.iv_toolbar_crop_cancel:
                onBackPressed();
                break;
            case R.id.iv_toolbar_crop_done:

                Map<Integer, PointF> points = polygonView.getPoints();
                List<Point> listPoint = new ArrayList<>();

                for (int i = 0; i < points.size(); i++) {
                    Point singlePoint = new Point(points.get(i).x, points.get(i).y);
                    listPoint.add(singlePoint);
                }
                Bitmap globalBitmap;

                globalBitmap = returnCroppedBitmap(GlobalData.getBitmapsListModel().get(currentPosition).getBitmap(), listPoint);
                GlobalData.getBitmapsListModel().get(currentPosition).setBitmap(globalBitmap);

                imageView.setImageBitmap(globalBitmap);

                if (globalBitmap.getWidth() > 1000 || globalBitmap.getHeight() > 1000) {
                    int nh = (int) (globalBitmap.getHeight() * (512.0 / globalBitmap.getWidth()));
                    globalBitmap = Bitmap.createScaledBitmap(globalBitmap, 512, nh, true);
                }

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                globalBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                Intent intent = new Intent();
                intent.putExtra("bitmap", byteArray);
                setResult(Const.CROP_IMAGE, intent);
                onBackPressed();
                break;
        }
    }

    private Bitmap returnCroppedBitmap(Bitmap bitmap, List<Point> listPoint) {


        Mat mat = new Mat();

        org.opencv.android.Utils.bitmapToMat(bitmap, mat);

        ArrayList<Point> listPoints = new ArrayList<>();
        listPoints.add(0, listPoint.get(0));
        listPoints.add(0, listPoint.get(1));
        listPoints.add(0, listPoint.get(3));
        listPoints.add(0, listPoint.get(2));

        Mat convertedMat = Converters.vector_Point2f_to_Mat(listPoints);


        Point[] points1 = listPoints.toArray(new Point[listPoints.size()]);


        MatOfPoint2f matOfPoint2f = new MatOfPoint2f(points1);

        RotatedRect rotatedRect = Imgproc.minAreaRect(matOfPoint2f);

        rotatedRect.points(points1);

        Rect rect = rotatedRect.boundingRect();

        Mat outputMat = warp(mat, convertedMat, rect);

        Bitmap croppedBitmap = Bitmap.createBitmap(outputMat.clone().width(), outputMat.clone().height(), Bitmap.Config.RGB_565);

        org.opencv.android.Utils.matToBitmap(outputMat, croppedBitmap);

        return croppedBitmap;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public Mat warp(Mat inputMat, Mat startM, Rect rect) {

        int resultWidth = rect.width;
        int resultHeight = rect.height;

        Point ocvPOut4, ocvPOut1, ocvPOut2, ocvPOut3;

        Mat outputMat = new Mat(resultWidth, resultHeight, CvType.CV_8UC4);


        ocvPOut1 = new Point(resultWidth, 0);
        ocvPOut2 = new Point(0, 0);
        ocvPOut3 = new Point(0, resultHeight);
        ocvPOut4 = new Point(resultWidth, resultHeight);

        List<Point> dest = new ArrayList<Point>();
        dest.add(ocvPOut4);
        dest.add(ocvPOut3);
        dest.add(ocvPOut2);
        dest.add(ocvPOut1);
        Mat endM = Converters.vector_Point2f_to_Mat(dest);

        Mat perspectiveTransform = Imgproc.getPerspectiveTransform(startM, endM);

        Imgproc.warpPerspective(inputMat, outputMat, perspectiveTransform, new Size(resultWidth, resultHeight), Imgproc.INTER_CUBIC);
        Core.flip(outputMat, outputMat, CvType.CV_8UC3);
        return outputMat;
    }
}
