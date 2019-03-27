package com.technostacks.almaktaba.activity;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.app.usage.ConfigurationStats;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.technostacks.almaktaba.R;
import com.technostacks.almaktaba.adapter.PostRecycleAdapter;
import com.technostacks.almaktaba.broadcasts.DownloadReceiver;
import com.technostacks.almaktaba.dialog.AlertDialogHelper;
import com.technostacks.almaktaba.dialog.DepartmentAlertDialog;
import com.technostacks.almaktaba.dialog.DocumentSelectionDialog;
import com.technostacks.almaktaba.dialog.DownloadFileNameDialog;
import com.technostacks.almaktaba.dialog.PostEditDialog;
import com.technostacks.almaktaba.listener.DriveFileListener;
import com.technostacks.almaktaba.listener.RecyclerItemClick;
import com.technostacks.almaktaba.listener.RecyclerPostItemClick;
import com.technostacks.almaktaba.model.CoursesResponseModel;
import com.technostacks.almaktaba.model.DocumentResponseModel;
import com.technostacks.almaktaba.util.Const;
import com.technostacks.almaktaba.util.GlobalData;
import com.technostacks.almaktaba.util.ProgressDialogHelper;
import com.technostacks.almaktaba.util.Utils;
import com.technostacks.almaktaba.webservices.NetworkHelper;
import com.technostacks.almaktaba.webservices.WebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

import static com.technostacks.almaktaba.AlMaktabaApplication.isDocUploaded;
import static com.technostacks.almaktaba.AlMaktabaApplication.isFirstDownload;
import static com.technostacks.almaktaba.activity.MainActivity.isGuestUser;

public class DocumentListingActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, RecyclerPostItemClick {

    public static final String TAG = DocumentListingActivity.class.getSimpleName();
    public static final int DOC_UPLOAD_REQUESTCODE = 40;
    public static final int DOC_REPORT_REQUESTCODE = 41;
    @BindView(R.id.rcv_posts)
    RecyclerView rcvPosts;
    @BindView(R.id.swipe_posts)
    SwipeRefreshLayout swipePosts;
    @BindView(R.id.fab_add_posts)
    FloatingActionButton fabAddPosts;
    @BindView(R.id.fab_doc_guest_user)
    FloatingActionButton fabGuest;
    @BindView(R.id.ll_empty_posts)
    LinearLayout llEmptyPosts;
    private Context mContext;
    Gson gson;
    PostRecycleAdapter adapter;
    int currentPosition;
    File sourceFile;
    private ArrayList<DocumentResponseModel.Document> documentsList;
    CoursesResponseModel.Departmentcourse departmentcourse;
    DownloadReceiver downloadReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
    //    setContentView(R.layout.activity_document_listing);
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_document_listing,container);
        ButterKnife.bind(this,view);

        if (getIntent().getExtras()!=null){
            departmentcourse = getIntent().getExtras().getParcelable(Const.COURSE_DATA);
        }

        rcvPosts.setLayoutManager(new LinearLayoutManager(mContext));
        rcvPosts.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));

        gson = Utils.getGson();

        setToolbarTitle(getString(R.string.documents));
        ivMenu.setVisibility(View.GONE);
        tvForgotBack.setVisibility(View.VISIBLE);
        tvForgotBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hidekeyBoard(DocumentListingActivity.this);
                onBackPressed();
            }
        });

        callDocumentsListingApi(true);
        documentsList = new ArrayList<>();
        adapter = new PostRecycleAdapter(mContext,documentsList,this);
        rcvPosts.setAdapter(adapter);
        swipePosts.setOnRefreshListener(this);

        if(isGuestUser){
            fabGuest.setVisibility(View.VISIBLE);
            fabAddPosts.setVisibility(View.GONE);
        } else{
            fabAddPosts.setVisibility(View.VISIBLE);
            fabGuest.setVisibility(View.GONE);
        }

        sourceFile = new File(Const.FOLDER_PATH, getString(R.string.app_name));
        if (!sourceFile.exists())
            sourceFile.mkdir();

        downloadReceiver = new DownloadReceiver();
    }


    @OnClick({R.id.fab_add_posts,R.id.fab_doc_guest_user})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fab_add_posts:
                Intent intent = new Intent(mContext,UploadDocumentActivity.class);
                intent.putExtra(Const.COURSE_DATA,departmentcourse);
                startActivityForResult(intent,DOC_UPLOAD_REQUESTCODE);
                break;
            case R.id.fab_doc_guest_user:
                Utils.clearLoggedInPreferences();
                startActivity(new Intent(mContext,LoginActivity.class));
                break;
        }
    }

    private void callDocumentsListingApi(boolean showProgress){

        if (showProgress){
            Utils.showProgressDialog(mContext,"");
        }


        try {
            JSONObject jsonObject = new JSONObject();
            JSONObject conditionJson = new JSONObject();
            conditionJson.put(NetworkHelper.DOC_IS_DELETED,0);
            conditionJson.put(NetworkHelper.DOC_DEPARTMENTCOURSE_ID,departmentcourse.getId());
            jsonObject.put(NetworkHelper.CONDITIONS,conditionJson);
            jsonObject.put(NetworkHelper.CONTAIN,new JSONArray().put(NetworkHelper.USERS));
            jsonObject.put(NetworkHelper.GET,NetworkHelper.GET_ALL);
            JSONObject orderJson = new JSONObject();
            orderJson.put(NetworkHelper.DOC_ID,NetworkHelper.ORDER_DESC);
            jsonObject.put(NetworkHelper.ORDER,orderJson);

            Log.e(TAG,"json string "+jsonObject);
            StringEntity stringEntity = new StringEntity(jsonObject.toString());

            WebService.postWithAuth(mContext,NetworkHelper.API_LIST_DOCUMENTS,stringEntity,NetworkHelper.CONTENT_TYPE_JSON,swipePosts,responseHandler);

        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void onRefresh() {
//        adapter.notifyDataSetChanged();
        callDocumentsListingApi(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case DOC_UPLOAD_REQUESTCODE:
                if (resultCode == RESULT_OK){
                    callDocumentsListingApi(true);
                }
                break;
        }
    }

    AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            try {

                String response = new String(responseBody);
                Log.e(TAG, "response = " + response);
                JSONObject jsonObject = new JSONObject(response);

                if (jsonObject.has(NetworkHelper.CODE) && jsonObject.getInt(NetworkHelper.CODE) == NetworkHelper.CODE200) {

                    DocumentResponseModel documentResponseModel = gson.fromJson(response,DocumentResponseModel.class);
                    documentsList.clear();
                    documentsList = (ArrayList<DocumentResponseModel.Document>) documentResponseModel.getDocuments();

                    handleDocResponse();

                }else {
                    handleDocResponse();
                    AlertDialogHelper.showDialog(mContext, jsonObject.getString(NetworkHelper.MESSAGE), null);
                }
                handleProgressVisibility();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            if (responseBody != null) {
                Log.e(TAG, "response = " + new String(responseBody));
            }
            handleDocResponse();
            handleProgressVisibility();
        }
    };

    private void handleDocResponse() {

        if (documentsList != null && !documentsList.isEmpty()) {

            adapter.setPostData(documentsList);
            rcvPosts.setVisibility(View.VISIBLE);
            llEmptyPosts.setVisibility(View.GONE);
        } else {
            rcvPosts.setVisibility(View.GONE);
            llEmptyPosts.setVisibility(View.VISIBLE);
        }

    }


    private void handleProgressVisibility(){

        if (swipePosts.isRefreshing())
            swipePosts.setRefreshing(false);

            Utils.hideProgressDialog();

    }

    @Override
    public void onRecyclerItemClick(int postAction, final int position) {

        currentPosition = position;
        switch(postAction){
            case Const.POST_REPORT_CLICK:
                Intent intent = new Intent(mContext,TransparentReportActivity.class);
                intent.putExtra(Const.DOCUMENT_RESPONSE,documentsList.get(position));
                startActivityForResult(intent,DOC_REPORT_REQUESTCODE);
                break;
            case Const.POST_DOWNLOAD_CLICK:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED))
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                else
                    showDocumentDownloadConfirmationDialog();

                break;
            case Const.POST_EDIT_CLICK:
                PostEditDialog postEditDialog;
                if (Utils.getLoggedInUser(mContext).getId() == documentsList.get(position).getUsers().getId())
                    postEditDialog = new PostEditDialog(mContext,true);
                else
                    postEditDialog = new PostEditDialog(mContext,false);

                postEditDialog.showPostEditDialog(new RecyclerItemClick() {
                    @Override
                    public void onRecyclerItemClick(int editPosition) {
                        switch (editPosition){
                            case 1:
                                showDeleteConfirmationDialog();
                                break;
                            case 2:
                                if (documentsList.get(position).getMimeType().equalsIgnoreCase(Const.MIME_TYPE_MOV)|| documentsList.get(position).getMimeType().equalsIgnoreCase(Const.MIME_TYPE_MP4))
                                    sharePost( WebService.VIDEO_URL +documentsList.get(position).getFilename());
                                else
                                    sharePost(WebService.DOCUMENT_BASE_URL +documentsList.get(position).getFilename());

                                break;
                        }
                    }
                });

                break;
            case 0:
                if (documentsList.get(position).getMimeType().equalsIgnoreCase(Const.MIME_TYPE_MP4) || documentsList.get(position).getMimeType().equalsIgnoreCase(Const.MIME_TYPE_MOV))
                    playVideo(WebService.VIDEO_URL+documentsList.get(position).getFilename());
                else if (documentsList.get(position).getMimeType().equalsIgnoreCase(Const.MIME_TYPE_PDF)){

                    Intent webIntent = new Intent(Intent.ACTION_VIEW,Uri.parse(WebService.DOCUMENT_BASE_URL+documentsList.get(position).getFilename()));
                    startActivity(webIntent);

                }

                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED ) {
                showDocumentDownloadConfirmationDialog();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isDocUploaded){
            callDocumentsListingApi(true);
            isDocUploaded = false;
        }
        registerReceiver(downloadReceiver,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }

    private void showDeleteConfirmationDialog(){
        DepartmentAlertDialog departmentAlertDialog = new DepartmentAlertDialog(mContext);
        departmentAlertDialog.createDepartmentChooserDialog(getString(R.string.are_you_sure_want_to_delete), "", "", new RecyclerItemClick() {
            @Override
            public void onRecyclerItemClick(int position) {
                if (position == 1){
                    callDeletePostApi();
                }
            }
        });
    }

    private void showDocumentDownloadConfirmationDialog(){

        DepartmentAlertDialog departmentAlertDialog = new DepartmentAlertDialog(mContext);
        departmentAlertDialog.createDepartmentChooserDialog(getString(R.string.are_you_sure_want_to_download), getString(R.string.download), getString(R.string.no), new RecyclerItemClick() {
            @Override
            public void onRecyclerItemClick(int position) {
                if (position == 1){
                    openDocumentNameDialog();
                }
            }
        });
    }
    private void openDocumentNameDialog(){
        DownloadFileNameDialog dialog = new DownloadFileNameDialog(mContext);
        dialog.showDialog(new DriveFileListener() {
            @Override
            public void getFileName(String name) {
                callDocumentDownloadUrl(name);
            }
        });
    }
    private void playVideo(String url){

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(url), "video/*");
        startActivity(intent);
    }

    private void sharePost(String url){

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Al Maktaba : "+url);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Send To"));
    }

    private void callDocumentDownloadUrl(String fileName) {

        /*downloadProgress = new ProgressDialogHelper(this,getString(R.string.downloading));
        downloadProgress.show();*/

        String fileUrl;

        if (documentsList.get(currentPosition).getMimeType().equalsIgnoreCase(Const.MIME_TYPE_MP4) || documentsList.get(currentPosition).getMimeType().equalsIgnoreCase(Const.MIME_TYPE_MOV))
            fileUrl = WebService.VIDEO_URL + documentsList.get(currentPosition).getFilename();
        else
            fileUrl = WebService.DOCUMENT_BASE_URL + documentsList.get(currentPosition).getFilename();

            /*WebService.get(mContext, fileUrl, new RequestParams(), progress, new FileAsyncHttpResponseHandler(this) {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                if (file != null) {
                    Log.e(TAG, "response := " + file);

                }

                downloadProgress.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {

                if (file != null) {
                    Log.e(TAG, "file name " + file.getName());
                    Log.e(TAG, "file name " + file.getPath());

                    File destination = new File(sourceFile.getAbsolutePath(), documentsList.get(currentPosition).getFilename());
                    try {
                       Boolean isSaved = copyFile(file, destination);
                       if (isSaved)
                           Toast.makeText(DocumentListingActivity.this, R.string.file_downloaded_successfully, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                downloadProgress.dismiss();
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);

                long progressPercentage = (long) 100 * bytesWritten / totalSize;
                Log.e(TAG, "progress = " + progressPercentage);
                Log.e(TAG, "bytesWritten = " + bytesWritten);
                Log.e(TAG, "totalsize = " + totalSize);
                ProgressDialog progressDialog = downloadProgress.getProgressDialog();
                progressDialog.setProgress((int) progressPercentage);
            }
        });*/

        Toast.makeText(mContext, "Downloading in progress..", Toast.LENGTH_SHORT).show();
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle(getString(R.string.app_name));
        request.setDescription("Downloading.. " +fileName+"_"+documentsList.get(currentPosition).getFilename());
        request.setAllowedOverRoaming(false);
        request.setVisibleInDownloadsUi(true);

        request.setDestinationInExternalPublicDir(getString(R.string.app_name),fileName+"_"+ documentsList.get(currentPosition).getFilename());
        long id =  downloadManager.enqueue(request);

        GlobalData.addDownloaId(id);

        if (isFirstDownload){
            isFirstDownload = false;
            AlertDialogHelper.showDialog(mContext,"Downloading is in progress, You can find it in 'Al Maktaba' folder once completed.",null);
        }



    }

    public Boolean copyFile(File sourceFile, File destFile)
            throws IOException {
        if (!destFile.exists()) {
            destFile.createNewFile();

            FileChannel source = null;
            FileChannel destination = null;
            try {
                source = new FileInputStream(sourceFile).getChannel();
                destination = new FileOutputStream(destFile).getChannel();
                destination.transferFrom(source, 0, source.size());
            } finally {
                if (source != null)
                    source.close();
                if (destination != null)
                    destination.close();
            }
            return true;
        }
        return false;
    }

    private void callDeletePostApi(){

        Utils.showProgressDialog(mContext,"");

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(NetworkHelper.ID,documentsList.get(currentPosition).getId());
            jsonObject.put(NetworkHelper.DOC_DELETE_BY,documentsList.get(currentPosition).getUserId());

            Log.e(TAG, "json request := " + jsonObject.toString());
            StringEntity stringEntity = new StringEntity(jsonObject.toString());

            WebService.postWithAuth(mContext, NetworkHelper.API_DELETE_DOCUMENT, stringEntity, NetworkHelper.CONTENT_TYPE_JSON, swipePosts, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    try {
                        String response = new String(responseBody);
                        Log.e(TAG, "response := " + response);
                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.has(NetworkHelper.CODE) && jsonObject.getInt(NetworkHelper.CODE) == NetworkHelper.CODE200) {

                            AlertDialogHelper.showDialog(mContext, getString(R.string.the_document_has_been_deleted), null);
                            documentsList.remove(currentPosition);
                            adapter.notifyDataSetChanged();
                        }else {
                            AlertDialogHelper.showDialog(mContext, jsonObject.getString(NetworkHelper.MESSAGE), null);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Utils.hideProgressDialog();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    if (responseBody!=null)
                        Log.e(TAG, "response := " + new String(responseBody));
                    Utils.hideProgressDialog();
                }
            });

        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(downloadReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();

    }
}
