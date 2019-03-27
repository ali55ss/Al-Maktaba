package com.technostacks.almaktaba.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.github.barteksc.pdfviewer.PDFView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;
import com.technostacks.almaktaba.R;
import com.technostacks.almaktaba.dialog.AlertDialogHelper;
import com.technostacks.almaktaba.listener.RecyclerItemClick;
import com.technostacks.almaktaba.model.DocDataModel;
import com.technostacks.almaktaba.util.Const;
import com.technostacks.almaktaba.util.GlobalData;
import com.technostacks.almaktaba.util.ProgressDialogHelper;
import com.technostacks.almaktaba.util.Utils;
import com.technostacks.almaktaba.videoCompression.VideoCompressUtil;
import com.technostacks.almaktaba.webservices.NetworkHelper;
import com.technostacks.almaktaba.webservices.WebService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

import static com.technostacks.almaktaba.AlMaktabaApplication.isDocUploaded;

public class PreviewActivity extends BaseActivity {

    public static final String TAG = PreviewActivity.class.getSimpleName();
    @BindView(R.id.preview_pdfview)
    PDFView previewWebview;
    @BindView(R.id.btn_upload_now)
    Button btnUploadNow;
    private Context mContext;
    private String file_name;
    File file;
    Uri uri;
    ProgressDialogHelper uploadVideoProgress;
    String thumbFileName, docFileName, docFilePath;
    DocDataModel docDataModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_preview);
        mContext = this;
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_preview, container);
        ButterKnife.bind(this, view);

        setToolbarTitle(getString(R.string.preview));
        ivMenu.setVisibility(View.GONE);
        tvForgotBack.setVisibility(View.VISIBLE);
        tvForgotBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hidekeyBoard(PreviewActivity.this);
                onBackPressed();
            }
        });

        if (getIntent().getExtras() != null) {
            file_name = getIntent().getStringExtra(Const.TEMP_FILE_NAME);
            docDataModel = getIntent().getParcelableExtra(Const.DOC_DATA);
        }


        docFilePath = Const.TEMP_FOLDER_PATH + "/" + Const.DIRECTORY_NAME + "/" + file_name;
        uploadVideoProgress = new ProgressDialogHelper((Activity) mContext, getString(R.string.uploading));
        file = new File(docFilePath);

        uri = Uri.fromFile(file);
        Log.e(TAG, "uri = " + uri.getPath());

        /*WebSettings settings = previewWebview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        previewWebview.setInitialScale(1);
        settings.setBuiltInZoomControls(true);
        settings.setSaveFormData(true);
        settings.setSupportZoom(true);
        settings.setDisplayZoomControls(false);

        previewWebview.setWebViewClient(new MyWebViewClient());*/


        previewWebview.fromUri(uri)
                .enableSwipe(true)
                .enableDoubletap(true)
                .enableAnnotationRendering(false)
                .load();
        //previewWebview.load("file://"+file.getPath()); // "file:///"+android.os.Environment.getExternalStorageDirectory().toString()+"/"+Const.DIRECTORY_NAME+"/"+file_name


    }

    @OnClick(R.id.btn_upload_now)
    public void onViewClicked() {
        /*Intent webIntent = new Intent(Intent.ACTION_VIEW,uri);
        webIntent.setDataAndType(uri,"application/pdf");
        startActivity(webIntent);*/


        if (GlobalData.getBitmapsListModel() != null && GlobalData.getBitmapsListModel().size() > 0)
            uploadDocThumb(GlobalData.getBitmapsListModel().get(0).getBitmap());
        else if (GlobalData.getBitmapsListModel() != null && GlobalData.getBitmap() != null)
            uploadDocThumb(GlobalData.getBitmap());
        else {
            Bitmap bmp = getThumbOfDrivePdf();
            if (bmp != null)
                uploadDocThumb(bmp);
        }

    }

    private Bitmap getThumbOfDrivePdf() {

        PdfiumCore pdfiumCore = new PdfiumCore(this);

        try {

            ParcelFileDescriptor fd = getContentResolver().openFileDescriptor(Uri.fromFile(new File(docFilePath)), "r");
            PdfDocument pdfDocument = pdfiumCore.newDocument(fd);
            pdfiumCore.openPage(pdfDocument, 0);
            int width = pdfiumCore.getPageWidthPoint(pdfDocument, 0);
            int height = pdfiumCore.getPageHeightPoint(pdfDocument, 0);
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            pdfiumCore.renderPageBitmap(pdfDocument, bmp, 0, 0, 0, width, height);

            pdfiumCore.closeDocument(pdfDocument); // important!
            return bmp;
        } catch (Exception e) {
            e.printStackTrace();

        }

        return null;
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        //show the web page in webview but not in web browser
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.e(TAG, "shouldOverrideUrlLoading = " + url);
            view.loadUrl(url);
            return false;
        }
    }

    private void uploadDocThumb(Bitmap bitmap) {
        Utils.showProgressDialog(mContext,"");
        try {
            File file = Utils.createFile(mContext, bitmap, "temp");

            RequestParams requestParams = new RequestParams();

            requestParams.put(NetworkHelper.DOC_DOCUMENTS, file);

            Log.e(TAG, "request := " + requestParams.toString());

            WebService.post(mContext, NetworkHelper.API_UPLOAD_UNIVERSITY_LOGO, requestParams, responseHandler);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            try {

                String response = new String(responseBody);
                Log.e(TAG, "response := " + response);
                JSONObject resultJSON = new JSONObject(response);

                if (resultJSON.has("users") && resultJSON.get("users") instanceof JSONObject) {

                    JSONObject document = resultJSON.getJSONObject("users");

                    if (document.has(NetworkHelper.DOC_DOCUMENTS))
                        thumbFileName = document.getString(NetworkHelper.DOC_DOCUMENTS);
                    Log.e(TAG, "doc name = " + thumbFileName);

                    if (docFilePath != null) {

                        Utils.hideProgressDialog();
                        uploadVideoProgress.show();

                        GlobalData.resetBitmap();

                        File file = new File(docFilePath);
                        long fileSizeInMB = Utils.getFileSize(file);
                        if (fileSizeInMB >= 25){
                            uploadVideoProgress.hide();
                            AlertDialogHelper.showDialog(mContext,getString(R.string.document_size_bigger_than_25),null);
                        }else {
                            uploadDocument(file);
                        }

                    }

                } else {
                    AlertDialogHelper.showDialog(mContext, "Something went wrong!", null);
                    Utils.hideProgressDialog();
                }


            } catch (JSONException e) {
                e.printStackTrace();
                Utils.hideProgressDialog();
            }

        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            if (responseBody != null) {
                Log.e(TAG, "response = " + new String(responseBody));
            }
            Utils.hideProgressDialog();
            Utils.handleResponseErrors(mContext,statusCode);

            if (error instanceof SocketTimeoutException || error instanceof TimeoutException )
                AlertDialogHelper.showDialog(mContext,"Requested Timeout, Please try again later.",null);

        }
    };


    private void uploadDocument(File file) {

        try {
            RequestParams requestParams = new RequestParams();
            requestParams.put(NetworkHelper.DOC_DOCUMENTS, file);

            Log.e(TAG, "request := " + requestParams.toString());

            WebService.post(mContext, NetworkHelper.API_UPLOAD_UNIVERSITY_LOGO, requestParams, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    try {

                        String response = new String(responseBody);
                        Log.e(TAG, "response := " + response);
                        JSONObject resultJSON = new JSONObject(response);

                        if (resultJSON.has("users") && resultJSON.get("users") instanceof JSONObject) {

                            JSONObject document = resultJSON.getJSONObject("users");

                            if (document.has(NetworkHelper.DOC_DOCUMENTS))
                                docFileName = document.getString(NetworkHelper.DOC_DOCUMENTS);

                            uploadVideoProgress.hide();
                            callUploadDocumentApi();

                        } else {
                            uploadVideoProgress.hide();
                            AlertDialogHelper.showDialog(mContext, resultJSON.getString(NetworkHelper.MESSAGE), null);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        uploadVideoProgress.hide();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    if (responseBody != null) {
                        Log.e(TAG, "response = " + new String(responseBody));
                    }
                    uploadVideoProgress.hide();
                    Utils.handleResponseErrors(mContext,statusCode);

                    if (error instanceof SocketTimeoutException || error instanceof TimeoutException )
                        AlertDialogHelper.showDialog(mContext,"Requested Timeout, Please try again later.",null);

                }

                @Override
                public void onProgress(long bytesWritten, long totalSize) {
                    super.onProgress(bytesWritten, totalSize);

                    long progressPercentage = (long) 100 * bytesWritten / totalSize;
                    ProgressDialog progressDialog = uploadVideoProgress.getProgressDialog();
                    progressDialog.setProgress((int) progressPercentage);

                }
            });

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            uploadVideoProgress.hide();
        }

    }


    private void callUploadDocumentApi() {

        Utils.showProgressDialog(mContext,"");

        try {
            JSONObject jsonObject = new JSONObject();

            if (docDataModel != null) {
                jsonObject.put(NetworkHelper.DOC_DEPARTMENTCOURSE_ID, docDataModel.getDepartmentCourseId());
                jsonObject.put(NetworkHelper.DOC_TITLE, docDataModel.getDocTitle());
                jsonObject.put(NetworkHelper.DOC_DESCRIPTION, docDataModel.getDocDescription());
            }
            jsonObject.put(NetworkHelper.USER_ID, Utils.getLoggedInUser(mContext).getId());
            jsonObject.put(NetworkHelper.DOC_FRONT_IMAGE, thumbFileName);
            jsonObject.put(NetworkHelper.DOC_FILENAME, docFileName);
            jsonObject.put(NetworkHelper.DOC_MIME_TYPE, Const.MIME_TYPE_PDF);

            Log.e(TAG, "json request = " + jsonObject);
            StringEntity stringEntity = new StringEntity(jsonObject.toString());

            WebService.postWithAuth(mContext, NetworkHelper.API_ADD_DOCUMENT, stringEntity, NetworkHelper.CONTENT_TYPE_JSON, null, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    try {

                        String response = new String(responseBody);
                        Log.e(TAG, "response = " + response);
                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.has(NetworkHelper.CODE) && jsonObject.getInt(NetworkHelper.CODE) == NetworkHelper.CODE200) {

                            AlertDialogHelper.showDialog(mContext, getString(R.string.document_has_been_saved), new RecyclerItemClick() {
                                @Override
                                public void onRecyclerItemClick(int position) {
                                    setResult(RESULT_OK);
                                    isDocUploaded = true;
                                    finish();
                                }
                            });

                        } else {
                            AlertDialogHelper.showDialog(mContext, getString(R.string.document_could_not_be_saved_try_again), null);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Utils.hideProgressDialog();

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    if (responseBody != null) {
                        Log.e(TAG, "response = " + new String(responseBody));
                    }
                    Utils.hideProgressDialog();
                    Utils.handleResponseErrors(mContext,statusCode);
                }
            });

        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
            Utils.hideProgressDialog();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (file.exists())
            file.delete();

    }
}
