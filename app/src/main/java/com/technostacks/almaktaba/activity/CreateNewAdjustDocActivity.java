package com.technostacks.almaktaba.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.technostacks.almaktaba.QuickAction.QuickActionView;
import com.technostacks.almaktaba.R;
import com.technostacks.almaktaba.adapter.AddDoc_QuickAction_Adapter;
import com.technostacks.almaktaba.adapter.CustomPagerAdapter;
import com.technostacks.almaktaba.model.BitmapListModel;
import com.technostacks.almaktaba.model.DocDataModel;
import com.technostacks.almaktaba.util.Const;
import com.technostacks.almaktaba.util.FilterClass;
import com.technostacks.almaktaba.util.GlobalData;
import com.technostacks.almaktaba.util.ProgressDialogHelper;
import com.technostacks.almaktaba.util.Utils;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.technostacks.almaktaba.AlMaktabaApplication.isDocUploaded;
import static com.technostacks.almaktaba.util.Const.REQUEST_SCAN_DOC;
import static com.technostacks.almaktaba.util.Const.RESULT_SCAN_DOC;
import static com.technostacks.almaktaba.util.GlobalData.bitmap;

public class CreateNewAdjustDocActivity extends AppCompatActivity {

    String TAG = CreateNewAdjustDocActivity.class.getName();
    Context mContext;
    @BindView(R.id.tv_toolbar_cancel)
    ImageView tvToolbarCancel;
    /*@BindView(R.id.imageview)
    ImageView imageview;*/
    /*@BindView(R.id.rl_Addnew)
    RelativeLayout rlAddnew;*/
    @BindView(R.id.ll_toolbar_cancel)
    LinearLayout llToolbarCancel;
    @BindView(R.id.txt_Title)
    TextView txtTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.viewpager_home)
    ViewPager viewPager;
    @BindView(R.id.txt_PageCount)
    TextView txtPageCount;
    @BindView(R.id.ll_bottom_navigation_scan)
    LinearLayout llBottomNavigationScan;
    @BindView(R.id.ll_bottom_navigation_filter)
    LinearLayout llBottomNavigationFilter;
    @BindView(R.id.ll_bottom_navigation_crop)
    LinearLayout llBottomNavigationCrop;
    @BindView(R.id.ll_bottom_navigation_rotate)
    LinearLayout llBottomNavigationRotate;
    @BindView(R.id.ll_bottom_navigation_delete)
    LinearLayout llBottomNavigationDelete;
    @BindView(R.id.bottom_nav_edit_doc)
    LinearLayout bottom_nav_temp_home;
    @BindView(R.id.tv_toolbar_preview)
    TextView tvPreview;
    @BindView(R.id.create_new_adView)
    AdView adView;
    private int currentPosition = 0;
    ArrayList<BitmapListModel> bitmapArrayList, grayBitmapList, blackWhiteBitmapList;
    private CustomPagerAdapter adapter;
    DocDataModel docDataModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_adjust_doc);
        ButterKnife.bind(this);
        mContext = CreateNewAdjustDocActivity.this;

        bitmapArrayList = new ArrayList<>();
        grayBitmapList = new ArrayList<>();
        blackWhiteBitmapList = new ArrayList<>();

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                adView.setVisibility(View.VISIBLE);
            }
        });

        if (getIntent().getExtras()!=null)
            docDataModel = getIntent().getParcelableExtra(Const.DOC_DATA);

        getGlobalBitmaps();

        adapter = new CustomPagerAdapter(this, false,
                bitmapArrayList, false, pagerItemClick);


        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                currentPosition = position;
                if (position != bitmapArrayList.size()) {
                    txtPageCount.setText("" + (position + 1) + "/" + bitmapArrayList.size());
                    txtPageCount.setVisibility(View.VISIBLE);
                } else {
                    txtPageCount.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                if (position != bitmapArrayList.size()) {
                    bottom_nav_temp_home.setVisibility(View.VISIBLE);
                    txtPageCount.setVisibility(View.VISIBLE);
                } else {
                    bottom_nav_temp_home.setVisibility(View.GONE);
                    txtPageCount.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        txtTitle.setText(Utils.getDocName());

    }

    private void getGlobalBitmaps(){
        for (int i = 0; i < GlobalData.getBitmapsListModel().size(); i++) {

            bitmapArrayList.add(new BitmapListModel(GlobalData.getBitmapsListModel().get(i).getBitmap(),
                    GlobalData.getBitmapsListModel().get(i).getBitmapUri(),
                    GlobalData.getBitmapsListModel().get(i).isFromGallary(),
                    GlobalData.getBitmapsListModel().get(i).getNameOfImage()));

        }
    }
    @Override
    public void onResume() {
        super.onResume();

        if (isDocUploaded)
             finish();

        if (!OpenCVLoader.initDebug()) {
            Log.d("TAG", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
        } else {
            Log.d("TAG", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i("TAG", "OpenCV loaded successfully");
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    CustomPagerAdapter.PagerItemClick pagerItemClick = new CustomPagerAdapter.PagerItemClick() {
        @Override
        public void itemClick() {

            Log.e("TAG", "bitmapArrayList.size()  " + bitmapArrayList.size());

//            Intent intent3 = new Intent(CreateNewAdjustDocActivity.this, DocumentDetectionActivity1.class);
//            intent3.putExtra("FROM", "OLD");
//            intent3.putExtra("position", bitmapArrayList.size());
//            startActivityForResult(intent3, REQUEST_SCAN_DOC);

        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.e("import", "onbackpressed called !!!!!");
        GlobalData.resetBitmapListModel();
    }


    @OnClick({R.id.tv_toolbar_cancel, R.id.ll_bottom_navigation_scan, R.id.ll_bottom_navigation_filter, R.id.ll_bottom_navigation_crop, R.id.ll_bottom_navigation_rotate, R.id.ll_bottom_navigation_delete,R.id.tv_toolbar_preview})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_toolbar_cancel:
                onBackPressed();
                break;
            case R.id.ll_bottom_navigation_scan:

                Intent intentNewScan = new Intent(mContext, DocumentDetectionActivity.class);
                intentNewScan.putExtra(Const.FROM, "OLD");
                intentNewScan.putExtra(Const.POSITION, GlobalData.getBitmapsListModel().size());
                startActivityForResult(intentNewScan, REQUEST_SCAN_DOC);

                break;
            case R.id.ll_bottom_navigation_filter:

                showQuickAction(view);

                break;
            case R.id.ll_bottom_navigation_crop:

                final Intent intent = new Intent(CreateNewAdjustDocActivity.this, CropImageActivity.class);
                intent.putExtra(Const.FROM, TAG);
                intent.putExtra("position", currentPosition);
                startActivityForResult(intent, Const.CROP_IMAGE);

                break;
            case R.id.ll_bottom_navigation_rotate:

                rotatePdfPage(bitmapArrayList);
                break;
            case R.id.ll_bottom_navigation_delete:
                deletePdfPage(bitmapArrayList);
                break;
            case R.id.tv_toolbar_preview:

               // createPdfFromNative();
                createPdf();
                /*Intent intent1 = new Intent(mContext,PreviewActivity.class);
                startActivity(intent1);*/
                break;
        }
    }


    private void deletePdfPage(List<BitmapListModel> bitmapArrayList) {

        if (GlobalData.getBitmapsListModel().size() > 1) {
            GlobalData.getBitmapsListModel().remove(currentPosition);
            bitmapArrayList.remove(currentPosition);
            adapter.notifyDataSetChanged();
        } else {
            GlobalData.resetBitmapListModel();
            bitmapArrayList.clear();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == Const.CROP_IMAGE) {
            if (data != null) {
                byte[] byteArray = data.getByteArrayExtra("bitmap");
                bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                bitmapArrayList.get(currentPosition).setBitmap(bitmap);
                adapter.notifyDataSetChanged();
            }
        } else if (requestCode == REQUEST_SCAN_DOC && resultCode == RESULT_OK) {

            updateNewScanData(bitmapArrayList.size());

        }
    }

    private void rotatePdfPage(List<BitmapListModel> bitmapArrayList) {

        Bitmap bitmap = ConfigureBitmap(currentPosition);
        Bitmap rotatedbitmap = bitmapArrayList.get(currentPosition).getBitmap();

        if (bitmap.getWidth() > 1000 || bitmap.getHeight() > 1000) {
            int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
            bitmap = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
        }
        ////  Bitmap bitmap = GlobalData.getBitmapsList().get(currentPosition);
        Matrix matrix = new Matrix();

        matrix.postRotate(90);

        bitmapArrayList.get(currentPosition).setBitmap(Bitmap.createBitmap(rotatedbitmap, 0, 0, rotatedbitmap.getWidth(), rotatedbitmap.getHeight(), matrix, true));
        GlobalData.getBitmapsListModel().get(currentPosition).setBitmap(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true));
        adapter.notifyDataSetChanged();

    }

    public void showQuickAction(View view) {

        final QuickActionView qa = QuickActionView.Builder(view);
        qa.setAdapter(new AddDoc_QuickAction_Adapter(this));
        qa.setNumColumns((1));
        qa.setOnClickListener(new DialogInterface.OnClickListener() {

            @Override
            public void onClick(final DialogInterface dialog, int which) {
                dialog.dismiss();
                switch (which) {

                    //No Filter
                    case 0:

                        bitmap = ConfigureBitmap(currentPosition);
                        bitmapArrayList.get(currentPosition).setBitmap(bitmap);
                        adapter.notifyDataSetChanged();
                        break;

                    //Black & White
                    case 1:
                        bitmap = ConfigureBitmap(currentPosition);
                        Bitmap bmpDynamic = FilterClass.setDefaultValues(bitmap);
                        bitmapArrayList.get(currentPosition).setBitmap(bmpDynamic);
                        adapter.notifyDataSetChanged();
                        break;

                    //Gray
                    case 2:
                        bitmap = ConfigureBitmap(currentPosition);
                        final Bitmap bmpFilterBandW = FilterClass.convertColorIntoBlackAndWhiteImage(bitmap);
                        bitmapArrayList.get(currentPosition).setBitmap(bmpFilterBandW);
                        adapter.notifyDataSetChanged();
                        break;

                    //Color
                    case 3:

                        Utils.showProgressDialog(mContext,"");

                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                bitmap = ConfigureBitmap(currentPosition);
                                final Bitmap bmpFilterColor = FilterClass.adjustedContrast(bitmap,50);
                                bitmapArrayList.get(currentPosition).setBitmap(bmpFilterColor);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        Utils.hideProgressDialog();
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        }).start();

                        break;

                    /*//Magic Color
                    case 4:
                        bitmap = ConfigureBitmap(currentPosition);
                        final Bitmap bmpFilterMagic = FilterClass.convertBitmapToContrast(bitmap);
                        bitmapArrayList.get(currentPosition).setBitmap(bmpFilterMagic);
                        adapter.notifyDataSetChanged();
                        break;*/
                }

            }
        });
        qa.show();
    }

    public Bitmap ConfigureBitmap(int currentPosition) {
        Bitmap bitmap = null;

        if (GlobalData.getBitmapsListModel().get(currentPosition).getBitmap() != null)
            bitmap = GlobalData.getBitmapsListModel().get(currentPosition).getBitmap();
        else {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(CreateNewAdjustDocActivity.this.getContentResolver(), GlobalData.getBitmapsListModel().get(currentPosition).getBitmapUri());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    private void updateNewScanData(int size) {

        Log.e(TAG, "size " + size);
        Log.e(TAG, "GlobalData " + GlobalData.getBitmapsListModel().size());

        // means no one new image are added
        if (GlobalData.getBitmapsListModel().size() <= size) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {

                bitmapArrayList.clear();
                getGlobalBitmaps();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });

            }
        }).start();

    }

    /*private void createPdfFromNative(){

        progress.show();
        new Thread(new Runnable() {
            @Override
            public void run() {

        try {

            File pdfFolder = new File(Const.TEMP_FOLDER_PATH,Const.DIRECTORY_NAME);
            if (!pdfFolder.exists()) {
                pdfFolder.mkdir();
                Log.e(TAG, "Pdf Directory created");
            }
            String dirpath = Const.TEMP_FOLDER_PATH +"/"+Const.DIRECTORY_NAME;

            File file = new File( dirpath ,txtTitle.getText().toString().trim());
            file.createNewFile();

            PdfDocument document = new PdfDocument();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            for (int i=0;i<bitmapArrayList.size();i++){

              //  Bitmap bitmap = Utils.compressImage(bitmapArrayList.get(i).getBitmap());

                Bitmap bmp = bitmapArrayList.get(i).getBitmap();

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
                Bitmap bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));

                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), i).setContentRect(new Rect(0,0,bitmap.getWidth(), bitmap.getHeight())).create();
                PdfDocument.Page page = document.startPage(pageInfo);
                
                Canvas canvas = page.getCanvas();
                Paint paint = new Paint();
                canvas.drawPaint(paint);

                paint.setColor(Color.BLUE);
                canvas.drawBitmap(bitmap, 10, 10, null);
                document.finishPage(page);

                document.writeTo(new FileOutputStream(file.getAbsolutePath()));

            }

            document.close();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progress.hide();
                    Intent intent = new Intent(mContext,PreviewActivity.class);
                    intent.putExtra(Const.TEMP_FILE_NAME,txtTitle.getText().toString().trim());
                    intent.putExtra(Const.DOC_DATA,docDataModel);
                    startActivity(intent);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
            progress.hide();
        }
            }
        }).start();
    }*/

    private void createPdf(){

        Utils.showProgressDialog(mContext,"");
        new Thread(new Runnable() {
            @Override
            public void run() {


                try {

                    String dirpath = Const.TEMP_FOLDER_PATH +"/"+Const.DIRECTORY_NAME;

                    File dir = new File(dirpath);

                    if (!dir.exists())
                        dir.mkdir();

                    File file = new File( dirpath ,txtTitle.getText().toString().trim());
                    file.createNewFile();

                    Document document = new Document();
                    PdfWriter.getInstance(document, new FileOutputStream(file.getAbsolutePath()));
                    document.open();

                    for (int i=0;i<bitmapArrayList.size();i++){

                      //  Bitmap bitmap = Utils.compressImage(bitmapArrayList.get(i).getBitmap());
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        document.newPage();
                        bitmapArrayList.get(i).getBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();

                        Image img = Image.getInstance(byteArray);
                        float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                                - document.rightMargin() - 0) / img.getWidth()) * 100;
                        img.scalePercent(scaler);
                        img.setAlignment(Image.ALIGN_CENTER );
                        document.add(img);
                    }

                    document.close();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Utils.hideProgressDialog();
                            Intent intent = new Intent(mContext,PreviewActivity.class);
                            intent.putExtra(Const.TEMP_FILE_NAME,txtTitle.getText().toString().trim());
                            intent.putExtra(Const.DOC_DATA,docDataModel);
                            startActivity(intent);
                        }
                    });

                } catch (DocumentException | IOException e) {
                    e.printStackTrace();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Utils.hideProgressDialog();
                        }
                    });
                }
            }
        }).start();
    }
}
