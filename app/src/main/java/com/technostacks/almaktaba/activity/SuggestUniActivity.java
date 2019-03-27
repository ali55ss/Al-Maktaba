package com.technostacks.almaktaba.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;
import com.technostacks.almaktaba.R;
import com.technostacks.almaktaba.dialog.AlertDialogHelper;
import com.technostacks.almaktaba.dialog.ChooseFileDialog;
import com.technostacks.almaktaba.listener.RecyclerItemClick;
import com.technostacks.almaktaba.util.Const;
import com.technostacks.almaktaba.util.ProgressDialogHelper;
import com.technostacks.almaktaba.util.Utils;
import com.technostacks.almaktaba.webservices.NetworkHelper;
import com.technostacks.almaktaba.webservices.WebService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class SuggestUniActivity extends BaseActivity {

    public static final String TAG = SuggestUniActivity.class.getSimpleName();
    @BindView(R.id.ed_suggest_uni_name)
    EditText edSuggestUniName;
    @BindView(R.id.ti_suggest_uni_name)
    TextInputLayout tiSuggestUniName;
    @BindView(R.id.btn_suggest_submit)
    Button btnSuggestSubmit;
    @BindView(R.id.iv_suggest_uni_logo)
    ImageView ivUniLogo;
    @BindView(R.id.iv_add_suggest_uni)
    ImageView ivUni;
    @BindView(R.id.rl_upload_photo_container)
    RelativeLayout photoContainer;
    @BindView(R.id.ed_suggest_course_code)
    EditText edSuggestCourseCode;
    @BindView(R.id.ti_suggest_course_code)
    TextInputLayout tiSuggestCourseCode;
    @BindView(R.id.ed_suggest_course_name)
    EditText edSuggestCourseName;
    @BindView(R.id.ti_suggest_course_name)
    TextInputLayout tiSuggestCourseName;
    @BindView(R.id.ll_course_container)
    LinearLayout llCourseContainer;
    private Context mContext;
    Uri mImageCaptureUri;
    int suggestScreen, uniId,collegeId,departmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_suggest_uni);
        mContext = this;

        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_suggest_uni, container);
        ButterKnife.bind(mContext, view);

        if (getIntent().getExtras() != null) {
            suggestScreen = getIntent().getExtras().getInt(Const.SUGGEST_SCREEN);
            uniId = getIntent().getExtras().getInt(Const.UNI_ID);
            collegeId = getIntent().getExtras().getInt(Const.COLLEGE_ID);
            departmentId = getIntent().getExtras().getInt(DepartmentsActivity.DEPARTMENT_ID);

        }

        switch (suggestScreen) {
            case Const.SUGGEST_UNI:
                setToolbarTitle(getString(R.string.suggest_university));
                tiSuggestUniName.setHint(getString(R.string.university_name));
                photoContainer.setVisibility(View.VISIBLE);

                break;
            case Const.SUGGEST_COLLEGE:
                setToolbarTitle(getString(R.string.suggest_college));
                tiSuggestUniName.setHint(getString(R.string.enter_college_name));

                break;
            case Const.SUGGEST_DEPARTMENT:

                setToolbarTitle(getString(R.string.suggest_department));
                tiSuggestUniName.setHint(getString(R.string.enter_department_name));

                break;
            case Const.SUGGEST_COURSE:
                setToolbarTitle(getString(R.string.suggest_course));
                llCourseContainer.setVisibility(View.VISIBLE);
                tiSuggestUniName.setVisibility(View.GONE);

                break;
        }

        ivMenu.setVisibility(View.GONE);
        tvForgotBack.setVisibility(View.VISIBLE);
        tvForgotBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hidekeyBoard(SuggestUniActivity.this);
                onBackPressed();
            }
        });

    }


    @OnClick({R.id.btn_suggest_submit, R.id.iv_add_suggest_uni})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_suggest_submit:

                switch (suggestScreen) {
                    case Const.SUGGEST_UNI:
                        if (edSuggestUniName.getText().toString().isEmpty())
                            tiSuggestUniName.setError(getString(R.string.please_enter_university_name));
                        else if (mImageCaptureUri == null)
                            Toast.makeText(mContext, R.string.please_select_image_to_upload, Toast.LENGTH_SHORT).show();
                        else {
                            UploadUniPhoto();
                        }

                        break;
                    case Const.SUGGEST_COLLEGE:

                        if (edSuggestUniName.getText().toString().isEmpty())
                            tiSuggestUniName.setError(getString(R.string.please_enter_college_name));
                        else
                            callSuggestCollegeApi();
                        break;
                    case Const.SUGGEST_DEPARTMENT:

                        if (edSuggestUniName.getText().toString().isEmpty())
                            tiSuggestUniName.setError(getString(R.string.please_enter_department_name));
                        else
                            callSuggestionDepartmentApi();

                        break;
                    case Const.SUGGEST_COURSE:

                        if (edSuggestCourseCode.getText().toString().isEmpty()){
                            tiSuggestCourseName.setError(null);
                            tiSuggestCourseCode.setError(getString(R.string.please_enter_course_code));
                        } else if (edSuggestCourseName.getText().toString().isEmpty()){
                            tiSuggestCourseCode.setError(null);
                            tiSuggestCourseName.setError(getString(R.string.please_enter_course_name));

                        } else {
                            tiSuggestCourseCode.setError(null);
                            tiSuggestCourseName.setError(null);
                            callSuggestCourseApi();
                        }

                        break;
                }


                break;
            case R.id.iv_add_suggest_uni:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                } else {
                    openDialog();
                }

                break;
        }
    }

    private void callSuggestCourseApi(){

        Utils.showProgressDialog(mContext,"");

        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put(NetworkHelper.SUGGESTION_TYPE, NetworkHelper.SUGGESTION_TYPE_COURSE);
            jsonObject.put(NetworkHelper.ADD_COURSE_CODE, edSuggestCourseCode.getText().toString().trim());
            jsonObject.put(NetworkHelper.ADD_UNI_NAME, edSuggestCourseName.getText().toString().trim());
            jsonObject.put(NetworkHelper.USER_ID, Utils.getLoggedInUser(mContext).getId());
            jsonObject.put(NetworkHelper.TYPE_ID,departmentId);

            Log.e(TAG, "json req = " + jsonObject.toString());

            StringEntity stringEntity = new StringEntity(jsonObject.toString());

            WebService.postWithAuth(mContext, NetworkHelper.API_SUGGEST_UNIVERSITY, stringEntity, NetworkHelper.CONTENT_TYPE_JSON, null  ,addSuggestionresponseHandler);

        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    }
    private void callSuggestionDepartmentApi() {

        Utils.showProgressDialog(mContext,"");

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put(NetworkHelper.SUGGESTION_TYPE, NetworkHelper.SUGGESTION_TYPE_DEPARTMENT);
            jsonObject.put(NetworkHelper.ADD_UNI_NAME, edSuggestUniName.getText().toString().trim());
            jsonObject.put(NetworkHelper.USER_ID, Utils.getLoggedInUser(mContext).getId());
            jsonObject.put(NetworkHelper.TYPE_ID,collegeId);

            Log.e(TAG, "json req = " + jsonObject.toString());

            StringEntity stringEntity = new StringEntity(jsonObject.toString());

            WebService.postWithAuth(mContext, NetworkHelper.API_SUGGEST_UNIVERSITY, stringEntity, NetworkHelper.CONTENT_TYPE_JSON, null  ,addSuggestionresponseHandler);

        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    private void callSuggestCollegeApi() {

        Utils.showProgressDialog(mContext,"");
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put(NetworkHelper.SUGGESTION_TYPE, NetworkHelper.SUGGESTION_TYPE_COLLEGE);
            jsonObject.put(NetworkHelper.ADD_UNI_NAME, edSuggestUniName.getText().toString().trim());
            jsonObject.put(NetworkHelper.USER_ID, Utils.getLoggedInUser(mContext).getId());
            jsonObject.put(NetworkHelper.TYPE_ID, uniId);

            Log.e(TAG, "json req = " + jsonObject.toString());

            StringEntity stringEntity = new StringEntity(jsonObject.toString());

            WebService.postWithAuth(mContext, NetworkHelper.API_SUGGEST_UNIVERSITY, stringEntity, NetworkHelper.CONTENT_TYPE_JSON, null,addSuggestionresponseHandler);

        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    private void openDialog() {
        ChooseFileDialog chooseFileDialog = new ChooseFileDialog(this);
        chooseFileDialog.createChooseFileDialog(false,itemClick);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                openDialog();
            }
        }
    }

    RecyclerItemClick itemClick = new RecyclerItemClick() {
        @Override
        public void onRecyclerItemClick(int position) {
            switch (position) {
                case 0:
                    openCamera();
                    break;
                case 1:
                    openGallery();
                    break;
            }
        }
    };

    private void openGallery() {

        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, Const.OPEN_GALLERY_REQUESTCODE);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mImageCaptureUri = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".provider", new File(Environment.getExternalStorageDirectory(), "tmp-" + System.currentTimeMillis() + ".jpg"));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, Const.OPEN_CAMERA_REQUESTCODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Const.OPEN_GALLERY_REQUESTCODE:

                if (resultCode == RESULT_OK) {
                    mImageCaptureUri = data.getData();
                    Log.e("Upload", "image uri = " + mImageCaptureUri.getPath());

                    Picasso.with(mContext).load(mImageCaptureUri).fit().into(ivUniLogo);
                }
                break;

            case Const.OPEN_CAMERA_REQUESTCODE:
                if (resultCode == RESULT_OK) {
                    Log.e("Upload", "image uri = " + mImageCaptureUri);

                    if (mImageCaptureUri != null)
                        Picasso.with(mContext).load(mImageCaptureUri).fit().into(ivUniLogo);

                }
                break;
        }
    }

    private void UploadUniPhoto() {

        Utils.showProgressDialog(mContext,"");
        try {
            RequestParams json = new RequestParams();
            InputStream inputStream = null;

            inputStream = mContext.getContentResolver().openInputStream(mImageCaptureUri);
            if (inputStream != null) {
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                File file = Utils.createFile(mContext, BitmapFactory.decodeStream(bufferedInputStream), "temp");
                json.put(NetworkHelper.UNIVERSITY_IMAGE, file);

                Log.e(TAG, "request := " + json.toString());
            }

            WebService.post(mContext,NetworkHelper.API_UPLOAD_UNIVERSITY_LOGO, json,responseHandler);

            Log.e(TAG, "json := " + json);
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

                if (resultJSON.has("users") && resultJSON.get("users") instanceof JSONObject && resultJSON.getJSONObject("users").has(NetworkHelper.UNIVERSITY_IMAGE)) {

                    File file = new File(mImageCaptureUri.getPath());
                    if (file.exists())
                        file.delete();

                    JSONObject user = resultJSON.getJSONObject("users");

                    callAddUniversityApi(user.getString(NetworkHelper.UNIVERSITY_IMAGE));

                } else {
                    AlertDialogHelper.showDialog(SuggestUniActivity.this, "Something went wrong!", null);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Utils.hideProgressDialog();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            error.printStackTrace();
            Utils.hideProgressDialog();
        }
    };

    private void callAddUniversityApi(String fileName) {

        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put(NetworkHelper.SUGGESTION_TYPE, NetworkHelper.SUGGESTION_TYPE_UNI);
            jsonObject.put(NetworkHelper.TYPE_ID, NetworkHelper.TYPE_ID_UNI);
            jsonObject.put(NetworkHelper.ADD_UNI_NAME, edSuggestUniName.getText().toString().trim());
            jsonObject.put(NetworkHelper.ADD_UNI_LOGO, fileName);
            jsonObject.put(NetworkHelper.USER_ID, Utils.getLoggedInUser(mContext).getId());

            Log.e(TAG, "request := " + jsonObject.toString());
            StringEntity stringEntity = new StringEntity(jsonObject.toString());

            WebService.postWithAuth(mContext, NetworkHelper.API_SUGGEST_UNIVERSITY, stringEntity, NetworkHelper.CONTENT_TYPE_JSON, null ,addSuggestionresponseHandler);

        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    AsyncHttpResponseHandler addSuggestionresponseHandler = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            try {

                String response = new String(responseBody);
                Log.e(TAG, "response := " + response);
                JSONObject resultJSON = new JSONObject(response);

                if (resultJSON.has(NetworkHelper.CODE) && resultJSON.getInt(NetworkHelper.CODE) == NetworkHelper.CODE200) {

                    Utils.hideProgressDialog();
                    AlertDialogHelper.showDialog(mContext, getString(R.string.suggestion_has_been_saved), new RecyclerItemClick() {
                        @Override
                        public void onRecyclerItemClick(int position) {
                            onBackPressed();
                        }
                    });


                } else {

                    Utils.hideProgressDialog();
                    AlertDialogHelper.showDialog(mContext, resultJSON.getString(NetworkHelper.MESSAGE), null);

                }

            } catch (JSONException e) {
                e.printStackTrace();
                Utils.hideProgressDialog();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            Utils.hideProgressDialog();
            error.printStackTrace();
        }

    };

}
