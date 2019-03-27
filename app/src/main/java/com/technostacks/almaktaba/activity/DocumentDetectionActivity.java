package com.technostacks.almaktaba.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPoolAdapter;
import com.leo.simplearcloader.SimpleArcLoader;
import com.technostacks.almaktaba.R;
import com.technostacks.almaktaba.activity.customview.CameraPreview;
import com.technostacks.almaktaba.activity.customview.DrawView;
import com.technostacks.almaktaba.model.BitmapListModel;
import com.technostacks.almaktaba.model.CameraData;
import com.technostacks.almaktaba.model.DocDataModel;
import com.technostacks.almaktaba.model.MatData;
import com.technostacks.almaktaba.util.Const;
import com.technostacks.almaktaba.util.GlobalData;
import com.technostacks.almaktaba.util.ImageCompress;
import com.technostacks.almaktaba.util.OpenCVHelper;
import com.technostacks.almaktaba.util.Utils;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static com.technostacks.almaktaba.AlMaktabaApplication.isDocUploaded;
import static com.technostacks.almaktaba.util.GlobalData.isAutoDetect;
import static com.technostacks.almaktaba.util.GlobalData.isCapturing;
import static com.technostacks.almaktaba.util.GlobalData.isFirstTimeForAuto;
import static com.technostacks.almaktaba.util.GlobalData.isFirstTimeForManual;
import static com.technostacks.almaktaba.util.GlobalData.iscameraFlashOn;
import static org.opencv.core.CvType.CV_32S;
import static org.opencv.imgproc.Imgproc.approxPolyDP;

public class DocumentDetectionActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = DocumentDetectionActivity.class.getSimpleName();
    private Context mContext;
    private PublishSubject<CameraData> subject = PublishSubject.create();
    private int counter = 0;
    private static final int rotationRight = 0;
    private static final int rotationLeft = 1;
    private int isRotated;
    ImageView ic_suggesion_scan;
    private int docCounter = 15;
    private CameraPreview cameraPreview;
    RelativeLayout RL_Auto_Progress;
    private SimpleArcLoader iv_camera_Loder;

    //top bar
    private TextView tv_FitDocumentIntoScreen;
    private ImageView ivAuto;
    private ImageView ivcamera, ivFlash;

    //Info screen
    CountDownTimer countDownTimer;
    //Bottom bar
    RelativeLayout rl_SendNext;
    ImageView iv_scanPreview, iv_scanPreview1, iv_scanPreview2;
    private TextView tvScannedNumber;

    TextView text_countDon;
    ProgressBar progress_countdown, progress_done_image, progress_done_Next;
    RelativeLayout RL_CountDown_Progress;

    ArrayList<Uri> uriList = new ArrayList<Uri>();
    private String FROM = "";
 //   private int POS = 0;
    boolean isManual = false;
    public static boolean isDocDetect = false;
    CountDownTimer cdt;
    DocDataModel docDataModel;

    static {
        if (!OpenCVLoader.initDebug()) {
            Log.v(TAG, "init OpenCV");
        }
    }

    private void getintent() {

        Intent intent = getIntent();
        FROM = intent.getStringExtra(Const.FROM);
    //    POS = intent.getIntExtra("position", 0);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (getIntent().getExtras()!=null)
            docDataModel = getIntent().getParcelableExtra(Const.DOC_DATA);

        isAutoDetect = false;

    }

    private void initVewview() {

        setContentView(R.layout.activity_document_detection_surface_view_one);

        // reference
        mContext = this;

        // get Intent fro pass other activities
        getintent();

        try {
            cameraPreview = (CameraPreview) findViewById(R.id.camera_preview);
            cameraPreview.setCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    CameraData cameraData = new CameraData();
                    cameraData.data = data;
                    cameraData.camera = camera;
                    subject.onNext(cameraData);
                }
            });
            cameraPreview.setOnClickListener(v -> cameraPreview.focus());

            // imgFlash.setOnClickListener(view -> cameraPreview.flash());
            DrawView drawView = (DrawView) findViewById(R.id.draw_layout);
            subject.concatMap(cameraData -> OpenCVHelper.getRgbMat(new MatData(), cameraData.data, cameraData.camera))
                    .concatMap(matData -> OpenCVHelper.resize(matData, 400, 400))
                    .map(matData -> {
                        matData.resizeRatio = (float) matData.oriMat.height() / matData.resizeMat.height();
                        matData.cameraRatio = (float) cameraPreview.getHeight() / matData.oriMat.height();
                        return matData;
                    }).concatMap(this::detectRect)
                    .compose(mainAsync())
                    .subscribe(new Action1<MatData>() {
                        @Override
                        public void call(MatData matData) {

                            if (!isAutoDetect && isManual && !isCapturing) {
                                isManual = false;
                                DocumentDetectionActivity.this.extractDocument(matData);

                            } else if (isAutoDetect) {

                                if (drawView != null) {

                                    if (!isCapturing) {

                                        if (matData.cameraPath != null) {
                                            drawView.setMatData(matData);
                                            drawView.setPath(matData.cameraPath);
                                            if (isDocDetect) {
                                                counter++;
                                                if (counter > docCounter) {
                                                    counter = 0;
                                                    DocumentDetectionActivity.this.extractDocument(matData);
                                                }
                                            } else {
                                                counter = 0;
                                            }
                                        } else {
                                            drawView.setPath(null);
                                            counter = 0;
                                        }
                                        drawView.invalidate();
                                    } else {
                                        drawView.setPath(null);
                                        counter = 0;
                                    }
                                }
                            } else {
                                if (drawView != null) {
                                    drawView.setPath(null);
                                    drawView.drawTextCentered("");
                                    drawView.invalidate();
                                }
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        //init view
        initView();

        if(FROM.equalsIgnoreCase("NEW"))
            GlobalData.resetBitmapListModel();
        else
            updateCounter();
    }

    private void initView() {

        try {
            ivcamera = (ImageView) findViewById(R.id.iv_camera);
            iv_camera_Loder = (SimpleArcLoader) findViewById(R.id.iv_camera_Loder_Arc);

            ImageView iv_cancel = (ImageView) findViewById(R.id.iv_cancel);
            ivFlash = (ImageView) findViewById(R.id.iv_flash);


            rl_SendNext = (RelativeLayout) findViewById(R.id.rl_SendNext);
            tvScannedNumber = (TextView) findViewById(R.id.tvScannedNumber);
            iv_scanPreview = (ImageView) findViewById(R.id.iv_scanPreview);
            iv_scanPreview1 = (ImageView) findViewById(R.id.iv_scanPreview1);
            iv_scanPreview2 = (ImageView) findViewById(R.id.iv_scanPreview2);
            ivAuto = (ImageView) findViewById(R.id.iv_auto);


            text_countDon = (TextView) findViewById(R.id.text_countDon);
            progress_countdown = (ProgressBar) findViewById(R.id.progress_countdown);
            progress_done_image = (ProgressBar) findViewById(R.id.progress_done_image);
            progress_done_Next = (ProgressBar) findViewById(R.id.progress_done_Next);
            RL_CountDown_Progress = (RelativeLayout) findViewById(R.id.RL_CountDown_Progress);
            RL_Auto_Progress = (RelativeLayout) findViewById(R.id.RL_Auto_Progress);

            iv_cancel.setOnClickListener(this);
            rl_SendNext.setOnClickListener(this);
            ivFlash.setOnClickListener(this);
            ivcamera.setOnClickListener(this);
            ivAuto.setOnClickListener(this);


            Drawable progressDrawable = progress_done_image.getProgressDrawable().mutate();
            progressDrawable.setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.SRC_IN);
            progress_done_image.setProgressDrawable(progressDrawable);

            tv_FitDocumentIntoScreen = (TextView) findViewById(R.id.tv_FitDocumentIntoScreen);
            ic_suggesion_scan = (ImageView) findViewById(R.id.ic_suggesion_scan);

            handleAutoDetection();

            if (isFirstTimeForManual) {

                Bitmap manualBmp = compressDrawableBitmap(true);

                ic_suggesion_scan.setImageBitmap(manualBmp);
                ic_suggesion_scan.setVisibility(View.VISIBLE);

                if (countDownTimer != null) {
                    countDownTimer.cancel();
                    countDownTimer = null;
                }
                countDownTimer = new CountDownTimer(5000, 100) {

                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        isFirstTimeForManual = false;
                        ic_suggesion_scan.setVisibility(View.GONE);
                    }
                };
                countDownTimer.start();
            }

            ic_suggesion_scan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ic_suggesion_scan.setVisibility(View.GONE);

                    if (isFirstTimeForManual) {
                        isFirstTimeForManual = false;
                    } else if (isFirstTimeForAuto) {
                        isFirstTimeForAuto = false;
                    }

                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                        countDownTimer = null;
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iv_auto:

                if (isFirstTimeForAuto) {

                    Bitmap scanBmp = compressDrawableBitmap(true);
                    ic_suggesion_scan.setImageBitmap(scanBmp);
                    ic_suggesion_scan.setVisibility(View.VISIBLE);

                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                        countDownTimer = null;
                    }
                    countDownTimer = new CountDownTimer(5000, 100) {

                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            isFirstTimeForAuto = false;
                            ic_suggesion_scan.setVisibility(View.GONE);
                        }
                    };
                    countDownTimer.start();
                }

                isAutoDetect = !isAutoDetect;
                handleAutoDetection();
                break;

            case R.id.iv_camera:

                if (!isCapturing) {
                    ivcamera.setEnabled(false);
                    isManual = true;
                }
                break;
            case R.id.iv_cancel:
                onBackPressed();
                break;

            case R.id.iv_flash:

                if (iscameraFlashOn == 2)
                    iscameraFlashOn = 0;
                else
                    iscameraFlashOn++;

                handleFlashMode();
                break;

            case R.id.rl_SendNext:
                rl_SendNext.setEnabled(false);
                if (!GlobalData.getBitmapsListModel().isEmpty()) {

                    if (!isCapturing) {
                        isCapturing = true;
                        progress_done_Next.setVisibility(View.VISIBLE);
                        Log.e(TAG, "FROM " + FROM);
                        Log.e(TAG, "isAutoDetect " + isAutoDetect);
                    //    Log.e(TAG, "POS " + POS);

                        if (FROM.equalsIgnoreCase("NEW")) {

                                Intent intent = new Intent(mContext, CreateNewAdjustDocActivity.class);
                                intent.putExtra(Const.DOC_DATA,docDataModel);
                                startActivity(intent);
                                finish();
                                rl_SendNext.setEnabled(true);

                        }else if (FROM.equalsIgnoreCase("OLD")){
                            setResult(RESULT_OK);
                            finish();
                        }

                        progress_done_Next.setVisibility(View.GONE);

                    } else {
                        rl_SendNext.setEnabled(true);
                    }
                } else {
                    Toast.makeText(mContext, "Please scan image", Toast.LENGTH_SHORT).show();
                    rl_SendNext.setEnabled(true);
                }
                break;
        }
    }


    private Bitmap compressDrawableBitmap(boolean isScanDrawable){
        Drawable myDrawable;
        if (isScanDrawable)
            myDrawable = getResources().getDrawable(R.drawable.ic_suggetion_scan);
        else
            myDrawable = getResources().getDrawable(R.drawable.ic_suggesion_manual);

        Bitmap anImage      = ((BitmapDrawable) myDrawable).getBitmap();
        byte[] data = ImageCompress.compressImage("",anImage);

        return BitmapFactory.decodeByteArray(data,0,data.length);
    }
    public void handleFlashMode() {

        Log.e(TAG, "iscameraFlashOn " + iscameraFlashOn);

        if (cameraPreview != null) {

            if (iscameraFlashOn == 0) {
                cameraPreview.flash(0);
                ivFlash.setImageResource(R.drawable.ic_flash_on);
            } else if (iscameraFlashOn == 1) {
                cameraPreview.flash(1);
                ivFlash.setImageResource(R.drawable.ic_flash_off);
            } else {
                cameraPreview.flash(2);
                ivFlash.setImageResource(R.drawable.ic_flash_auto);
            }

        }
    }

    public void handleAutoDetection() {

        if (isAutoDetect) {
            ivcamera.setVisibility(View.GONE);
            iv_camera_Loder.setVisibility(View.VISIBLE);
            ivAuto.setImageResource(R.drawable.ic_automatic_select);


        } else {

            ivcamera.setVisibility(View.VISIBLE);
            iv_camera_Loder.setVisibility(View.GONE);
            ivAuto.setImageResource(R.drawable.ic_manual);
            iscameraFlashOn = 0;
        }
        handleFlashMode();
    }

    private void extractDocument(MatData matData) {

        if (GlobalData.getBitmapsListModel().size() < 10) {

            isCapturing = true;

            if (isAutoDetect) {

                List<Point> originalPoints = new ArrayList<>();

                Mat mat = matData.oriMat;
                for (int counter = 0; counter < matData.points.size(); counter++) {
                    Point point = matData.points.get(counter);
                    point.x = point.x * matData.resizeRatio;
                    point.y = point.y * matData.resizeRatio;
                    originalPoints.add(point);
                }

                MatOfPoint2f thisContour2f = new MatOfPoint2f();
                MatOfPoint approxContour = new MatOfPoint();
                MatOfPoint2f approxContour2f = new MatOfPoint2f();
                thisContour2f.fromList(originalPoints);

                approxPolyDP(thisContour2f, approxContour2f, Imgproc.arcLength(thisContour2f, true) * 0.009, true);
                approxContour2f.convertTo(approxContour, CV_32S);

                MatOfPoint2f rotatedMat = new MatOfPoint2f();
                rotatedMat.fromList(originalPoints);
                RotatedRect boundingBox = Imgproc.minAreaRect(rotatedMat);
                if (boundingBox.size.width < boundingBox.size.height) {
                    isRotated = rotationLeft;
                } else {
                    isRotated = 2;
                }

                double[] temp_double;
                temp_double = approxContour2f.get(0, 0);
                Point p1 = new Point(temp_double[0], temp_double[1]);

                temp_double = approxContour2f.get(1, 0);
                Point p2 = new Point(temp_double[0], temp_double[1]);

                temp_double = approxContour2f.get(2, 0);
                Point p3 = new Point(temp_double[0], temp_double[1]);

                temp_double = approxContour2f.get(3, 0);
                Point p4 = new Point(temp_double[0], temp_double[1]);

                List<Point> source = new ArrayList<>();
                source.add(p1);
                source.add(p2);
                source.add(p3);
                source.add(p4);

                Mat startM = Converters.vector_Point2f_to_Mat(source);
                Mat result = warp(mat, startM, boundingBox.boundingRect());
                Bitmap cropedBitmap = Bitmap.createBitmap(result.clone().width(), result.clone().height(), Bitmap.Config.ARGB_8888);
                org.opencv.android.Utils.matToBitmap(result, cropedBitmap);
                mat.release();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        RL_Auto_Progress.setVisibility(View.GONE);
                        RL_CountDown_Progress.setVisibility(View.VISIBLE);

                        HashMap<TextView, CountDownTimer> counters = new HashMap<TextView, CountDownTimer>();
                        cdt = counters.get(text_countDon);
                        if (cdt != null) {
                            cdt.cancel();
                            cdt = null;
                        }
                        text_countDon.setText("3");
                        cdt = new CountDownTimer(4000, 50) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                int seconds = 0;
                                if (millisUntilFinished > DateUtils.SECOND_IN_MILLIS) {
                                    seconds = (int) (millisUntilFinished / DateUtils.SECOND_IN_MILLIS);
                                }
                                Log.e(TAG, "seconds " + "" + seconds);
                                text_countDon.setText(String.valueOf(seconds));

                                if (seconds == 3) {
                                    progress_countdown.setProgress(25);
                                } else if (seconds == 2) {
                                    progress_countdown.setProgress(50);
                                } else if (seconds == 1) {
                                    progress_countdown.setProgress(75);
                                } else if (seconds == 0) {
                                    progress_countdown.setProgress(100);
                                }
                            }

                            @Override
                            public void onFinish() {
                                try {
                                    text_countDon.setText("0");
                                    progress_countdown.setProgress(100);

                                    RL_Auto_Progress.setVisibility(View.VISIBLE);
                                    RL_CountDown_Progress.setVisibility(View.GONE);

                                    byte[] data = ImageCompress.compressImage("",cropedBitmap);
                                    Bitmap compressedBmp = BitmapFactory.decodeByteArray(data,0,data.length);

                                    BitmapListModel bitmapListModel = new BitmapListModel(compressedBmp, null, false, Utils.getDate());
                                    GlobalData.setBitmapListModel(bitmapListModel);
                                    isCapturing = false;
                                    counter = 0;
                                    updateCounter();
                                    cdt = null;

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        counters.put(text_countDon, cdt);
                        cdt.start();
                    }
                });

            } else {

                HashMap<ProgressBar, CountDownTimer> counters = new HashMap<ProgressBar, CountDownTimer>();
                cdt = counters.get(progress_done_image);
                if (cdt != null) {
                    cdt.cancel();
                    cdt = null;
                }
                progress_done_image.setVisibility(View.VISIBLE);
                progress_done_image.setProgress(0);

                cdt = new CountDownTimer(1000, 1) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        long seconds = 0;

                        Log.e(TAG, "millisUntilFinished " + "" + millisUntilFinished);
                        seconds = 1000 - millisUntilFinished;

                        Log.e(TAG, "seconds " + "" + seconds);

                        progress_done_image.setProgress((int) seconds);

                    }

                    @Override
                    public void onFinish() {
                        try {

                            Bitmap bitmap = Bitmap.createBitmap(matData.oriMat.clone().width(), matData.oriMat.clone().height(), Bitmap.Config.ARGB_8888);
                            org.opencv.android.Utils.matToBitmap(matData.oriMat, bitmap);

                            byte[] data = ImageCompress.compressImage("",bitmap);
                            Bitmap compressedBmp = BitmapFactory.decodeByteArray(data,0,data.length);

                            BitmapListModel bitmapListModel = new BitmapListModel(compressedBmp, null, false, Utils.getDate());
                            GlobalData.setBitmapListModel(bitmapListModel);
                            isCapturing = false;
                            counter = 0;
                            ivcamera.setEnabled(true);
                            updateCounter();
                            progress_done_image.setProgress(1000);
                            progress_done_image.setVisibility(View.GONE);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                counters.put(progress_done_image, cdt);
                cdt.start();
            }
        } else {
            isCapturing = false;
            counter = 0;
            Toast.makeText(mContext, "You have already selected 10 images!", Toast.LENGTH_LONG).show();
        }
    }


    private void updateCounter() {

        Log.d("tag", "bitmpalist = " + GlobalData.getBitmapsListModel().size());
    //    Log.d("tag", "POS = " + POS);

        if (GlobalData.getBitmapsListModel() != null) {
            Log.d("tag", "bitmpalist = " + GlobalData.getBitmapsListModel().size());
            tvScannedNumber.setText(String.valueOf(GlobalData.getBitmapsListModel().size()));
            rl_SendNext.setVisibility(View.VISIBLE);
            tv_FitDocumentIntoScreen.setVisibility(View.GONE);

            if (GlobalData.getBitmapsListModel().size() == 1) {
                iv_scanPreview.setImageBitmap(GlobalData.getBitmapsListModel().get(GlobalData.getBitmapsListModel().size() - 1).getBitmap());
                iv_scanPreview1.setVisibility(View.GONE);
                iv_scanPreview2.setVisibility(View.GONE);

            } else if (GlobalData.getBitmapsListModel().size() == 2) {
                iv_scanPreview.setVisibility(View.VISIBLE);
                iv_scanPreview1.setVisibility(View.VISIBLE);
                iv_scanPreview.setImageBitmap(GlobalData.getBitmapsListModel().get(GlobalData.getBitmapsListModel().size() - 1).getBitmap());
                iv_scanPreview1.setImageBitmap(GlobalData.getBitmapsListModel().get(GlobalData.getBitmapsListModel().size() - 2).getBitmap());
                iv_scanPreview2.setVisibility(View.GONE);

            } else if (GlobalData.getBitmapsListModel().size() > 2) {
                iv_scanPreview.setVisibility(View.VISIBLE);
                iv_scanPreview1.setVisibility(View.VISIBLE);
                iv_scanPreview2.setVisibility(View.VISIBLE);
                iv_scanPreview.setImageBitmap(GlobalData.getBitmapsListModel().get(GlobalData.getBitmapsListModel().size() - 1).getBitmap());
                iv_scanPreview1.setImageBitmap(GlobalData.getBitmapsListModel().get(GlobalData.getBitmapsListModel().size() - 2).getBitmap());
                iv_scanPreview2.setImageBitmap(GlobalData.getBitmapsListModel().get(GlobalData.getBitmapsListModel().size() - 3).getBitmap());
            }

        } else {

            tvScannedNumber.setText("0");
            rl_SendNext.setVisibility(View.GONE);
            tv_FitDocumentIntoScreen.setVisibility(View.VISIBLE);
        }

    }

    public Mat warp(Mat inputMat, Mat startM, Rect rect) {

        int resultWidth = rect.width;
        int resultHeight = rect.height;

        Point ocvPOut4, ocvPOut1, ocvPOut2, ocvPOut3;
        ocvPOut1 = new Point(0, 0);
        ocvPOut2 = new Point(0, resultHeight);
        ocvPOut3 = new Point(resultWidth, resultHeight);
        ocvPOut4 = new Point(resultWidth, 0);

        switch (isRotated) {
            case rotationRight:
                ocvPOut1 = new Point(resultWidth, resultHeight);
                ocvPOut2 = new Point(resultWidth, 0);
                ocvPOut3 = new Point(0, 0);
                ocvPOut4 = new Point(0, resultHeight);
                break;
            case rotationLeft:
                ocvPOut1 = new Point(resultWidth, 0);
                ocvPOut2 = new Point(0, 0);
                ocvPOut3 = new Point(0, resultHeight);
                ocvPOut4 = new Point(resultWidth, resultHeight);
                break;
            default:
                ocvPOut1 = new Point(0, 0);
                ocvPOut2 = new Point(0, resultHeight);
                ocvPOut3 = new Point(resultWidth, resultHeight);
                ocvPOut4 = new Point(resultWidth, 0);
                break;
        }

        Mat outputMat = new Mat(resultWidth, resultHeight, CvType.CV_8UC4);

        List<Point> dest = new ArrayList<Point>();
        dest.add(ocvPOut1);
        dest.add(ocvPOut2);
        dest.add(ocvPOut3);
        dest.add(ocvPOut4);
        Mat endM = Converters.vector_Point2f_to_Mat(dest);

        Mat perspectiveTransform = Imgproc.getPerspectiveTransform(startM, endM);

        Imgproc.warpPerspective(inputMat, outputMat, perspectiveTransform, new Size(resultWidth, resultHeight), Imgproc.INTER_CUBIC);

        return outputMat;
    }

    private Observable<MatData> detectRect(MatData mataData) {
        return Observable.just(mataData)
                .concatMap(OpenCVHelper::getMonochromeMat)
                .concatMap(OpenCVHelper::getContoursMat)
                .concatMap(OpenCVHelper::getPath);
    }

    private static <T> Observable.Transformer<T, T> mainAsync() {
        return obs -> obs.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onPause() {
        super.onPause();
        if (cameraPreview != null)
            cameraPreview.stop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isDocUploaded)
            finish();
        initVewview();
        isCapturing = false;
    }

    @Override
    public void onBackPressed() {
        Log.e(TAG, "FROM " + FROM);
        if (FROM.equalsIgnoreCase("NEW")) {
            GlobalData.resetBitmapListModel();
        }

        finish();
    }

}
