package com.technostacks.almaktaba.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.helpers.Constants;
import com.darsh.multipleimageselect.models.Image;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.google.android.gms.plus.Plus;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.technostacks.almaktaba.R;
import com.technostacks.almaktaba.dialog.AlertDialogHelper;
import com.technostacks.almaktaba.dialog.DepartmentAlertDialog;
import com.technostacks.almaktaba.dialog.DocumentSelectionDialog;
import com.technostacks.almaktaba.listener.DriveFileListener;
import com.technostacks.almaktaba.listener.RecyclerItemClick;
import com.technostacks.almaktaba.model.BitmapListModel;
import com.technostacks.almaktaba.model.CoursesResponseModel;
import com.technostacks.almaktaba.model.DocDataModel;
import com.technostacks.almaktaba.util.Const;
import com.technostacks.almaktaba.util.GlobalData;
import com.technostacks.almaktaba.util.ImageCompress;
import com.technostacks.almaktaba.util.ProgressDialogHelper;
import com.technostacks.almaktaba.util.Utils;
import com.technostacks.almaktaba.videoCompression.VideoCompressUtil;
import com.technostacks.almaktaba.webservices.NetworkHelper;
import com.technostacks.almaktaba.webservices.WebService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

import static com.technostacks.almaktaba.AlMaktabaApplication.isDocUploaded;

public class UploadDocumentActivity extends BaseActivity implements RecyclerItemClick, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = UploadDocumentActivity.class.getSimpleName();
    public static final int REQUEST_CODE_PICK_VIDEO = 30;
    public static final int REQUEST_DRIVE_OPENER = 20;
    public static final int REQUEST_DRIVE_RESOLUTION = 21;
    private static final int REQ_SIGN_IN_REQUIRED = 55664;
    @BindView(R.id.ed_upload_course_code)
    EditText edUploadCourseCode;
    @BindView(R.id.ti_upload_course_code)
    TextInputLayout tiUploadCourseCode;
    @BindView(R.id.ed_upload_course_name)
    EditText edUploadCourseName;
    @BindView(R.id.ti_upload_course_name)
    TextInputLayout tiUploadCourseName;
    @BindView(R.id.ed_document_title)
    EditText edDocumentTitle;
    @BindView(R.id.ti_document_title)
    TextInputLayout tiDocumentTitle;
    @BindView(R.id.tv_document_details)
    EditText edDocumentDetails;
    @BindView(R.id.btn_choose_docs)
    Button btnChooseDocs;
    private Context mContext;
    CoursesResponseModel.Departmentcourse departmentcourse;
    GoogleApiClient mGoogleApiClient;
    String accountName, token;
    ProgressDialogHelper uploadVideoProgress;
    String filePath, thumbFileName, videoFileName, outputfilePath;
    DocDataModel docDataModel;
    String extension = null, fileName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //    setContentView(R.layout.activity_upload_document);
        mContext = this;
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_upload_document, container);
        ButterKnife.bind(this, view);

        if (getIntent().getExtras() != null) {
            departmentcourse = getIntent().getExtras().getParcelable(Const.COURSE_DATA);
        }

        uploadVideoProgress = new ProgressDialogHelper((Activity) mContext, getString(R.string.uploading));
        setToolbarTitle(getString(R.string.upload_docs));
        ivMenu.setVisibility(View.GONE);
        tvForgotBack.setVisibility(View.VISIBLE);
        tvForgotBack.setOnClickListener(v -> {
            Utils.hidekeyBoard(UploadDocumentActivity.this);
            onBackPressed();
        });

        File pdfFolder = new File(Const.TEMP_FOLDER_PATH, Const.DIRECTORY_NAME);
        if (!pdfFolder.exists()) {
            pdfFolder.mkdir();
            Log.e(TAG, "Pdf Directory created");
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(com.google.android.gms.drive.Drive.API)
                .addScope(com.google.android.gms.drive.Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addApi(Plus.API)
                .addOnConnectionFailedListener(this)
                .build();

        edUploadCourseCode.setText(departmentcourse.getCourses().getCourseCode());
        edUploadCourseName.setText(departmentcourse.getCourses().getCourseName());
    }

    @OnClick({R.id.btn_choose_docs})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.btn_choose_docs:

                if (edDocumentTitle.getText().toString().isEmpty())
                    tiDocumentTitle.setError(getString(R.string.please_enter_document_title));
                else if (edDocumentDetails.getText().toString().isEmpty()) {
                    tiDocumentTitle.setError(null);
                    Toast.makeText(mContext, R.string.please_write_document_details, Toast.LENGTH_SHORT).show();
                } else {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                    } else {
                        docDataModel = new DocDataModel(departmentcourse.getId(), edDocumentTitle.getText().toString().trim(), edDocumentDetails.getText().toString().trim());
                        openDialog();
                    }
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                openDialog();
            }
        } else if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getDriveContent();
            }
        }
    }

    private void getDriveContent() {
        accountName = Plus.AccountApi.getAccountName(mGoogleApiClient);
        com.google.android.gms.drive.Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(driveContentsCallback);
    }

    private void openDialog() {
        DocumentSelectionDialog dialog = new DocumentSelectionDialog(mContext);
        dialog.ShowRideSelectionDialog(this);
    }

    @Override
    public void onRecyclerItemClick(int position) {
        switch (position) {
            case 1:
                Intent intentdetect = new Intent(mContext, DocumentDetectionActivity.class);
                intentdetect.putExtra(Const.FROM, "NEW");
                intentdetect.putExtra("position", 0);
                intentdetect.putExtra(Const.DOC_DATA, docDataModel);
                startActivity(intentdetect);
                break;
            case 2:

                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                intent.setType("video/*");
                startActivityForResult(intent, REQUEST_CODE_PICK_VIDEO);

                break;
            case 3:

                Intent imagePickerIntent = new Intent(this, AlbumSelectActivity.class);
                startActivityForResult(imagePickerIntent, Constants.REQUEST_CODE);

                /*Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                photoPickerIntent.setType("image*//*");
                startActivityForResult(photoPickerIntent, Const.OPEN_GALLERY_REQUESTCODE);*/

                break;
            case 4:

                /*intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                startActivityForResult(intent, 10);*/
                //  signOut();
                Utils.showProgressDialog(mContext,"");
                if (!mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                } else {
                    mGoogleApiClient.disconnect();
                    mGoogleApiClient.connect();
                   // com.google.android.gms.drive.Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(driveContentsCallback);

                }

                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (ContextCompat.checkSelfPermission(mContext, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[]{Manifest.permission.GET_ACCOUNTS}, 1);
        } else {
            getDriveContent();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    final ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback = new ResultCallback<DriveApi.DriveContentsResult>() {
        @Override
        public void onResult(DriveApi.DriveContentsResult result) {

            Utils.hideProgressDialog();
            if (result.getStatus().isSuccess()) {
                Log.e(TAG, "drive id :- " + result.getDriveContents().getDriveId());
                Log.e(TAG, "accountName :- " + accountName);
                OpenFileFromGoogleDrive();
                new RetrieveTokenTask().execute(accountName);
            }
        }
    };

    public void OpenFileFromGoogleDrive() {
        IntentSender intentSender = com.google.android.gms.drive.Drive.DriveApi.newOpenFileActivityBuilder()
                .setMimeType(new String[]{"image/png", "image/jpeg", "text/plain", "image/svg+xml", "application/pdf", "audio/mpeg", " video/mp4"})  // {"image/png", "image/jpeg", "text/plain", "image/svg+xml", "application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "audio/mpeg", " video/mp4"})
                .build(mGoogleApiClient);
        try {
            startIntentSenderForResult(intentSender, REQUEST_DRIVE_OPENER, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            Log.w(TAG, "Unable to send intent", e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_DRIVE_OPENER:
                if (resultCode == RESULT_OK) {
                    Log.e(TAG, "data = " + data);
                    DriveId mFileId = (DriveId) data.getParcelableExtra(OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);

                    Log.e(TAG, " drive open file id = " + mFileId);
                    DriveFile driveFile = mFileId.asDriveFile();
                    /*Task<DriveContents> openFileTask =
                            getDriveResourceClient().openFile(file, DriveFile.MODE_READ_ONLY);*/

                    //    DriveFile googleDriveFile = com.google.android.gms.drive.Drive.DriveApi.getFile(mGoogleApiClient, mFileId);

                    /*String downloadUrl = "https://drive.google.com/file/d/" + mFileId.getResourceId() + "/view";
                    Intent intent = new Intent(new Intent(mContext, WebViewActivity.class));
                    intent.putExtra(Const.DOC_URL, downloadUrl);
                    startActivity(intent);

                    Log.e(TAG, "downloadUrl = " + downloadUrl);*/

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            DriveResource.MetadataResult mdRslt = driveFile.getMetadata(mGoogleApiClient).await();
                            if (mdRslt != null && mdRslt.getStatus().isSuccess()) {
                                final String fileNamefromDrive = mdRslt.getMetadata().getTitle();

                                Log.i(TAG, "file name " + mdRslt.getMetadata().getTitle());

                                String filenameArray[] = mdRslt.getMetadata().getTitle().split("\\.");
                                extension = filenameArray[filenameArray.length - 1];

                                Log.i(TAG, "file extension " + extension);

                                String downloadUrl = "https://www.googleapis.com/drive/v3/files/" + mFileId.getResourceId() + "?alt=media";

                                Log.e(TAG, "downloadUrl = " + downloadUrl);
                            /*Intent webIntent = new Intent(Intent.ACTION_VIEW,Uri.parse(downloadUrl));
                            startActivity(webIntent);*/

                                // downloadUrl = "https://www.googleapis.com/drive/v3/files/" + mFileId.getResourceId() + "?alt=media";

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Utils.downloadGoogleDriveFile(mContext, downloadUrl, fileNamefromDrive, accountName, token, fileListener);
                                    }
                                });

                            }

                        }
                    }).start();
                }
                break;
            case REQUEST_DRIVE_RESOLUTION:

                /*if (!mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                } else {
                    accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    com.google.android.gms.drive.Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(driveContentsCallback);
                }*/

                break;
            case REQUEST_CODE_PICK_VIDEO:
                if (resultCode == RESULT_OK) {
                    Log.e(TAG, "video uri = " + data.getData());

                    if (data.getData() != null) {

                        DepartmentAlertDialog departmentAlertDialog = new DepartmentAlertDialog(mContext);
                        departmentAlertDialog.createDepartmentChooserDialog(getString(R.string.are_you_sure_you_want_to_upload), getString(R.string.upload_now), getString(R.string.no), new RecyclerItemClick() {
                            @Override
                            public void onRecyclerItemClick(int position) {
                                if (position == 1){
                                    filePath = getPath(data.getData());
                                    Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.MINI_KIND);
                                    uploadVideoThumb(bitmap);
                                }
                            }
                        });
                    }
                }

                break;

            case Constants.REQUEST_CODE:

                if (resultCode == RESULT_OK) {
                    Utils.showProgressDialog(mContext,"");

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            GlobalData.resetBitmapListModel();
                            ArrayList<Image> images = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
                            ArrayList<BitmapListModel> bitmapList = new ArrayList<>();

                            for (int i = 0; i < images.size(); i++) {

                                // Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse("file://" + images.get(i).path));
                                try {
                                    Bitmap bmp = getBitmap(getContentResolver(), Uri.parse("file://" + images.get(i).path));

                                    /*ByteArrayOutputStream out = new ByteArrayOutputStream();
                                    bmp.compress(Bitmap.CompressFormat.PNG, 0, out);*/

                                    byte[] data = ImageCompress.compressImage(Uri.parse("file://" + images.get(i).path).getPath(),bmp);
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                  //  Bitmap bitmap = Utils.compressImage(bmp);
                                    BitmapListModel bitmapListModel = new BitmapListModel(bitmap, Uri.parse("file://" + images.get(i).path), true, null);
                                    bitmapList.add(bitmapListModel);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                            GlobalData.getBitmapsListModel().addAll(bitmapList);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    Utils.hideProgressDialog();
                                    Intent intent = new Intent(mContext, CreateNewAdjustDocActivity.class);
                                    intent.putExtra(Const.DOC_DATA, docDataModel);
                                    startActivity(intent);
                                }
                            });
                        }
                    }).start();
                }

                break;

            case 10:

                Uri uri = data.getData();

                if (uri != null) {
                    Log.e(TAG, "doc uri = " + uri);

                    /*Intent intent = new Intent(mContext,WebViewActivity.class);
                    intent.putExtra(Const.DOC_URL,path);
                    startActivityForResult(intent,0);*/

                    Intent intent = new Intent(mContext,WebViewActivity.class);
                    intent.putExtra(Const.DOC_URL,uri.toString());
                    startActivity(intent);

                }


                /*Intent webIntent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(webIntent);*/

                break;
        }
    }

    public static final Bitmap getBitmap(ContentResolver cr, Uri url)
            throws FileNotFoundException, IOException {
        InputStream input = cr.openInputStream(url);
        Bitmap bitmap = BitmapFactory.decodeStream(input);
        input.close();
        return bitmap;
    }

    DriveFileListener fileListener = new DriveFileListener() {
        @Override
        public void getFileName(String name) {

            if (name!=null){

            if (extension != null) {

                Intent intent = new Intent(mContext, PreviewActivity.class);
                if (extension.equalsIgnoreCase(Const.MIME_TYPE_PDF)) {

                    intent.putExtra(Const.TEMP_FILE_NAME, name);
                    intent.putExtra(Const.DOC_DATA, docDataModel);
                    startActivity(intent);

                } else if (extension.equalsIgnoreCase(Const.EXT_JPG) || extension.equalsIgnoreCase(Const.EXT_PNG)) {
                    createPdf(name);
                } else if (extension.equalsIgnoreCase(Const.EXT_TXT)) {
                    createPdfFromTxt(name);
                } /*else if (extension.equalsIgnoreCase(Const.EXT_DOC) || extension.equalsIgnoreCase(Const.EXT_DOCX)) {

                    Utils.showProgressDialog(mContext,"");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            try {

                                File srcfile = new File(Const.dirpath, name);
                                com.aspose.words.Document document = new com.aspose.words.Document(srcfile.getAbsolutePath());
                                fileName = Utils.getFileNameOnly(name);
                                File file = new File(Const.dirpath, fileName + "." + Const.MIME_TYPE_PDF);
                                document.save(file.getAbsolutePath());

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Utils.hideProgressDialog();
                                        Intent intent = new Intent(mContext, PreviewActivity.class);
                                        intent.putExtra(Const.TEMP_FILE_NAME, fileName + "." + Const.MIME_TYPE_PDF);
                                        intent.putExtra(Const.DOC_DATA, docDataModel);
                                        startActivity(intent);
                                    }
                                });
                            } catch (Exception e) {
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
                }*/
                //convertWordToPdf(name);

            }
            }else {
                AlertDialogHelper.showDialog(mContext,"File not found!",null);
            }
        }
    };

    private class RetrieveTokenTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String accountName = params[0];
            String scopes = "oauth2:profile email";
            String token = null;
            try {
                token = GoogleAuthUtil.getToken(getApplicationContext(), accountName, scopes);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            } catch (UserRecoverableAuthException e) {
                startActivityForResult(e.getIntent(), REQ_SIGN_IN_REQUIRED);
            } catch (GoogleAuthException e) {
                Log.e(TAG, e.getMessage());
            }
            return token;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e(TAG, "token :- " + s);
            token = s;
        }
    }

    private void uploadVideoThumb(Bitmap bitmap) {
        Utils.showProgressDialog(mContext,"");
        try {
            byte[] bytes = ImageCompress.compressImage("",bitmap);
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            File file = Utils.createFile(mContext, bmp, "temp");

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

                    if (filePath != null) {

                        Utils.hideProgressDialog();
                        uploadVideoProgress.show();

                        VideoCompressUtil videoCompressUtil = new VideoCompressUtil(mContext, filePath);
                        videoCompressUtil.compressVideo(compressCallback);
                    }

                } else {
                    Utils.hideProgressDialog();
                    AlertDialogHelper.showDialog(mContext, "Something went wrong!", null);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Utils.hideProgressDialog();
            }

        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            if (responseBody != null)
                Log.e(TAG, "error = " + new String(responseBody));
            Utils.hideProgressDialog();
            Utils.handleResponseErrors(mContext,statusCode);

            if (error instanceof SocketTimeoutException || error instanceof TimeoutException )
                AlertDialogHelper.showDialog(mContext,"Requested Timeout, Please try again later.",null);
        }
    };

    private void signOut() {
        try {
            if (mGoogleApiClient.isConnected()) {
                Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                mGoogleApiClient.disconnect();
                mGoogleApiClient.connect();
            } else
                mGoogleApiClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {

        if (!result.hasResolution()) {
            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(this, result.getErrorCode(), 0).show();
            return;
        }

        try {
            result.startResolutionForResult(this, REQUEST_DRIVE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
        }
    }

    VideoCompressUtil.CompressCallback compressCallback = new VideoCompressUtil.CompressCallback() {
        @Override
        public void onCompressionResult(String outputFilePath, boolean compressionStatus) {
            Log.e(TAG, "compressCallback file path = " + outputFilePath);
            outputfilePath = outputFilePath;

            Log.e(TAG,"compressed = "+compressionStatus);

            if (compressionStatus){
                File file = new File(outputFilePath);
                long fileSizeInMB = Utils.getFileSize(file);
                if (fileSizeInMB>=50){
                    uploadVideoProgress.hide();
                    AlertDialogHelper.showDialog(mContext,getString(R.string.video_size_is_bigger_than_50),null);
                }else
                    uploadVideo(file);
            }else {
                uploadVideoProgress.hide();
                AlertDialogHelper.showDialog(mContext,getString(R.string.video_size_is_bigger_than_50),null);
            }

        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        if (isDocUploaded)
            finish();
    }

    private void uploadVideo(File file) {

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
                                videoFileName = document.getString(NetworkHelper.DOC_DOCUMENTS);

                            uploadVideoProgress.hide();
                            callUploadVideoApi();

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

    private void callUploadVideoApi() {
        Utils.showProgressDialog(mContext,"");

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(NetworkHelper.DOC_DEPARTMENTCOURSE_ID, departmentcourse.getId());
            jsonObject.put(NetworkHelper.USER_ID, Utils.getLoggedInUser(mContext).getId());
            jsonObject.put(NetworkHelper.DOC_TITLE, edDocumentTitle.getText().toString().trim());
            jsonObject.put(NetworkHelper.DOC_DESCRIPTION, edDocumentDetails.getText().toString().trim());
            jsonObject.put(NetworkHelper.DOC_FRONT_IMAGE, thumbFileName);
            jsonObject.put(NetworkHelper.DOC_FILENAME, videoFileName);
            jsonObject.put(NetworkHelper.DOC_MIME_TYPE, Const.MIME_TYPE_MP4);

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

                            if (outputfilePath != null && !outputfilePath.isEmpty()) {
                                File file = new File(outputfilePath);
                                if (file.exists())
                                    file.delete();
                            }

                            AlertDialogHelper.showDialog(mContext, getString(R.string.document_has_been_saved), new RecyclerItemClick() {
                                @Override
                                public void onRecyclerItemClick(int position) {
                                    setResult(RESULT_OK);
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

                    if (error instanceof SocketTimeoutException || error instanceof TimeoutException )
                        AlertDialogHelper.showDialog(mContext,"Requested Timeout, Please try again later.",null);
                }
            });

        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
            Utils.hideProgressDialog();
        }

    }

    private void createPdf(String name) {
        Utils.showProgressDialog(mContext,"");
        new Thread(new Runnable() {
            @Override
            public void run() {


                File imagefile = new File(Const.dirpath, name);

                try {

                    fileName = Utils.getFileNameOnly(name);

                    File file = new File(Const.dirpath, fileName + "." + Const.MIME_TYPE_PDF);
                    file.createNewFile();

                    Document document = new Document();
                    PdfWriter.getInstance(document, new FileOutputStream(file.getAbsolutePath()));
                    document.open();

                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    Bitmap bitmap = BitmapFactory.decodeFile(imagefile.getAbsolutePath(), bmOptions);

                    GlobalData.setBitmap(bitmap);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    document.newPage();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 80, stream);
                    byte[] byteArray = stream.toByteArray();

                    com.itextpdf.text.Image img = com.itextpdf.text.Image.getInstance(byteArray);
                    float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                            - document.rightMargin() - 0) / img.getWidth()) * 100;
                    img.scalePercent(scaler);
                    img.setAlignment(com.itextpdf.text.Image.ALIGN_CENTER | com.itextpdf.text.Image.ALIGN_TOP);

                    document.add(img);

                    document.close();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Utils.hideProgressDialog();
                            Intent intent = new Intent(mContext, PreviewActivity.class);
                            intent.putExtra(Const.TEMP_FILE_NAME, fileName + "." + Const.MIME_TYPE_PDF);
                            intent.putExtra(Const.DOC_DATA, docDataModel);
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


    /*private void convertWordToPdf(String src) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    File srcfile = new File(Const.dirpath, src);

                    fileName = Utils.getFileNameOnly(src);

                    File file = new File(Const.dirpath, fileName + "." + Const.MIME_TYPE_PDF);
                    file.createNewFile();

                    //create file inputstream object to read data from file
                    FileInputStream fs = new FileInputStream(srcfile.getAbsolutePath());
                    //create document object to wrap the file inputstream object
                    XWPFDocument doc = new XWPFDocument(fs);
                    //72 units=1 inch
                    Document pdfdoc = new Document(PageSize.A4, 72, 72, 72, 72);
                    //create a pdf writer object to write text to mypdf.pdf file
                    PdfWriter pwriter = PdfWriter.getInstance(pdfdoc, new FileOutputStream(file.getAbsolutePath()));
                    //specify the vertical space between the lines of text
                    pwriter.setInitialLeading(20);
                    //get all paragraphs from word docx
                    List<XWPFParagraph> plist = doc.getParagraphs();

                    //open pdf document for writing
                    pdfdoc.open();
                    for (int i = 0; i < plist.size(); i++) {
                        //read through the list of paragraphs
                        XWPFParagraph pa = plist.get(i);
                        //get all run objects from each paragraph
                        List<XWPFRun> runs = pa.getRuns();
                        //read through the run objects
                        for (int j = 0; j < runs.size(); j++) {
                            XWPFRun run = runs.get(j);
                            //get pictures from the run and add them to the pdf document
                            List<XWPFPicture> piclist = run.getEmbeddedPictures();
                            //traverse through the list and write each image to a file
                            Iterator<XWPFPicture> iterator = piclist.iterator();
                            while (iterator.hasNext()) {
                                XWPFPicture pic = iterator.next();
                                XWPFPictureData picdata = pic.getPictureData();
                                byte[] bytepic = picdata.getData();

                                com.itextpdf.text.Image imag = com.itextpdf.text.Image.getInstance(bytepic);
                                pdfdoc.add(imag);

                            }
                            //get color code
                            int color = getCode(run.getColor());
                            //construct font object
                            Font f = null;
                            if (run.isBold() && run.isItalic())
                                f = FontFactory.getFont(FontFactory.TIMES_ROMAN, run.getFontSize(), Font.BOLDITALIC, new BaseColor(color));
                            else if (run.isBold())
                                f = FontFactory.getFont(FontFactory.TIMES_ROMAN, run.getFontSize(), Font.BOLD, new BaseColor(color));
                            else if (run.isItalic())
                                f = FontFactory.getFont(FontFactory.TIMES_ROMAN, run.getFontSize(), Font.ITALIC, new BaseColor(color));
                            else if (run.isStrike())
                                f = FontFactory.getFont(FontFactory.TIMES_ROMAN, run.getFontSize(), Font.STRIKETHRU, new BaseColor(color));
                            else
                                f = FontFactory.getFont(FontFactory.TIMES_ROMAN, run.getFontSize(), Font.NORMAL, new BaseColor(color));
                            //construct unicode string
                            String text = run.getText(-1);
                            byte[] bs;
                            if (text != null) {
                                bs = text.getBytes();
                                String str = new String(bs, "UTF-8");
                                //add string to the pdf document
                                Chunk chObj1 = new Chunk(str, f);
                                pdfdoc.add(chObj1);
                            }

                        }
                        //output new line
                        pdfdoc.add(new Chunk(Chunk.NEWLINE));
                    }
                    //close pdf document
                    pdfdoc.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }*/

    public static int getCode(String code) {
        int colorCode;
        if (code != null)
            colorCode = Long.decode("0x" + code).intValue();
        else
            colorCode = Long.decode("0x000000").intValue();
        return colorCode;
    }

    private void createPdfFromTxt(String fileName) {

        Utils.showProgressDialog(mContext,"");

        new Thread(new Runnable() {
            @Override
            public void run() {

                String destFile = null;
                File file = new File(Const.dirpath, fileName);

                FileInputStream iStream = null;
                DataInputStream in = null;
                InputStreamReader is = null;
                BufferedReader br = null;
                try {

                    Document pdfDoc = new Document();

                    destFile = Utils.getFileNameOnly(fileName);

                    String text_file_name = destFile + "." + Const.MIME_TYPE_PDF;
                    PdfWriter writer = PdfWriter.getInstance(pdfDoc, new FileOutputStream(new File(Const.dirpath, text_file_name).getAbsolutePath()));
                    pdfDoc.open();
                    pdfDoc.setMarginMirroring(true);
                    pdfDoc.setMargins(36, 72, 108, 180);
                    pdfDoc.topMargin();
                    Font normal_font = new Font();
                    Font bold_font = new Font();
                    bold_font.setStyle(Font.BOLD);
                    bold_font.setSize(10);
                    normal_font.setStyle(Font.NORMAL);
                    normal_font.setSize(10);
                    pdfDoc.add(new Paragraph("\n"));
                    if (file.exists()) {
                        iStream = new FileInputStream(file);
                        in = new DataInputStream(iStream);
                        is = new InputStreamReader(in);
                        br = new BufferedReader(is);
                        String strLine;
                        while ((strLine = br.readLine()) != null) {
                            Paragraph para = new Paragraph(strLine + "\n", normal_font);
                            para.setAlignment(Element.ALIGN_JUSTIFIED);
                            pdfDoc.add(para);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Utils.hideProgressDialog();
                                Intent intent = new Intent(mContext, PreviewActivity.class);
                                intent.putExtra(Const.TEMP_FILE_NAME, text_file_name);
                                intent.putExtra(Const.DOC_DATA, docDataModel);
                                startActivity(intent);
                            }
                        });

                    } else {
                        Log.e(TAG, "file does not exist");
                        Utils.hideProgressDialog();
                        return;
                    }
                    pdfDoc.close();
                } catch (Exception e) {
                    Log.e(TAG, "FileUtility.covertEmailToPDF(): exception = " + e.getMessage());
                    Utils.hideProgressDialog();
                } finally {

                    try {
                        if (br != null) {
                            br.close();
                        }
                        if (is != null) {
                            is.close();
                        }
                        if (in != null) {
                            in.close();
                        }
                        if (iStream != null) {
                            iStream.close();
                        }
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Utils.hideProgressDialog();
                    }

                }
            }
        }).start();

    }
}
