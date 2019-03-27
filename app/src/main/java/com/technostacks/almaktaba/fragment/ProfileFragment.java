package com.technostacks.almaktaba.fragment;

import android.Manifest;
import android.app.Activity;
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
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;
import com.technostacks.almaktaba.R;
import com.technostacks.almaktaba.activity.MainActivity;
import com.technostacks.almaktaba.dialog.AlertDialogHelper;
import com.technostacks.almaktaba.dialog.ChooseFileDialog;
import com.technostacks.almaktaba.listener.RecyclerItemClick;
import com.technostacks.almaktaba.model.LoginResponseModel;
import com.technostacks.almaktaba.model.ResendCodeResponse;
import com.technostacks.almaktaba.model.User;
import com.technostacks.almaktaba.util.CircleImageView;
import com.technostacks.almaktaba.util.CircleTransform;
import com.technostacks.almaktaba.util.Const;
import com.technostacks.almaktaba.util.ProgressDialogHelper;
import com.technostacks.almaktaba.util.SharedPrefsUtils;
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
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;


public class ProfileFragment extends Fragment implements TextView.OnEditorActionListener {

    public static final String TAG = ProfileFragment.class.getSimpleName();
    @BindView(R.id.iv_profile_image)
    CircleImageView ivProfileImage;
    @BindView(R.id.iv_profile_add_image)
    ImageView ivProfileAddImage;
    @BindView(R.id.ed_profile_first_name)
    EditText edProfileFirstName;
    @BindView(R.id.ed_profile_last_name)
    EditText edProfileLastName;
    @BindView(R.id.ed_profile_email)
    EditText edProfileEmail;
    @BindView(R.id.ed_profile_mobile_number)
    EditText edProfileMobileNumber;
    @BindView(R.id.btn_profile_submit)
    Button btnProfileSubmit;
    private Context mContext;
    Uri mImageCaptureUri;
    Gson gson;

    public ProfileFragment() {

    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);

        gson = new Gson();

        getProfileDataApi();

        edProfileMobileNumber.setOnEditorActionListener(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void getProfileDataApi() {

        Utils.showProgressDialog(mContext,"");
        try {

            JSONObject jsonObject = new JSONObject();
            JSONObject conditionJson = new JSONObject();
            conditionJson.put(NetworkHelper.ID, Utils.getLoggedInUser(mContext).getId());
            conditionJson.put(NetworkHelper.STATUS, NetworkHelper.STATUS_ACTIVE);
            jsonObject.put(NetworkHelper.CONDITIONS, conditionJson);

            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            WebService.postWithAuth(mContext, NetworkHelper.API_GET_USER, stringEntity, NetworkHelper.CONTENT_TYPE_JSON, null, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    try {

                        String response = new String(responseBody);
                        Log.e(TAG, "response := " + response);
                        JSONObject resultJSON = new JSONObject(response);

                        if (resultJSON.has(NetworkHelper.CODE) && resultJSON.getInt(NetworkHelper.CODE) == NetworkHelper.CODE200) {

                            LoginResponseModel loginResponseModel = gson.fromJson(response, LoginResponseModel.class);

                            Utils.setLoggedInUser(loginResponseModel.getUsers().get(0));

                            User user = loginResponseModel.getUsers().get(0);
                            edProfileFirstName.setText(user.getFirstname());
                            edProfileLastName.setText(user.getLastname());
                            edProfileEmail.setText(user.getEmail());

                            if (user.getMobile() != null)
                                edProfileMobileNumber.setText(user.getMobile());
                            if (user.getProfileImage() != null)
                                Picasso.with(mContext).load(WebService.PROFILE_BASE_URL + user.getProfileImage()).transform(new CircleTransform()).fit().placeholder(R.drawable.ic_profile_placeholder).into(ivProfileImage);

                        } else {
                            AlertDialogHelper.showDialog(mContext, resultJSON.getString(NetworkHelper.MESSAGE), null);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                    Utils.hideProgressDialog();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Utils.hideProgressDialog();
                    error.printStackTrace();
                }
            });


        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
            Utils.hideProgressDialog();
        }

    }

    private void openDialog() {
        ChooseFileDialog chooseFileDialog = new ChooseFileDialog(mContext);
        chooseFileDialog.createChooseFileDialog(true,itemClick);
    }

    @OnClick({R.id.iv_profile_add_image, R.id.btn_profile_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_profile_add_image:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                } else {
                    openDialog();
                }

                break;
            case R.id.btn_profile_submit:

                validateProfileparams();

                break;
        }
    }


    private void validateProfileparams(){

        if (mImageCaptureUri == null && Utils.getLoggedInUser(mContext).getProfileImage() == null)
            Toast.makeText(mContext, getString(R.string.please_select_profile_image_to_upload), Toast.LENGTH_SHORT).show();
        else if (edProfileFirstName.getText().toString().trim().isEmpty())
            Toast.makeText(mContext, R.string.please_enter_first_name, Toast.LENGTH_SHORT).show();
        else if (edProfileLastName.getText().toString().trim().isEmpty())
            Toast.makeText(mContext, R.string.please_enter_last_name, Toast.LENGTH_SHORT).show();
        else if (edProfileEmail.getText().toString().trim().isEmpty())
            Toast.makeText(mContext, getString(R.string.please_enter_email), Toast.LENGTH_SHORT).show();
        else if (!Utils.isValidEmail(edProfileEmail.getText().toString().trim()))
            Toast.makeText(mContext, getString(R.string.enter_valid_email), Toast.LENGTH_SHORT).show();
        else if (edProfileMobileNumber.getText().toString().trim().isEmpty())
            Toast.makeText(mContext, R.string.enter_mobile_number, Toast.LENGTH_SHORT).show();
        else{
            Utils.hidekeyBoard(getActivity());
            uploadPhoto();
        }


    }

    private void uploadPhoto() {

        Utils.showProgressDialog(mContext,"");
        try {
            RequestParams json = new RequestParams();
            InputStream inputStream = null;

            if (mImageCaptureUri != null) {
                inputStream = mContext.getContentResolver().openInputStream(mImageCaptureUri);

                if (inputStream != null) {
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                    File file = Utils.createFile(mContext, BitmapFactory.decodeStream(bufferedInputStream), "temp");
                    json.put(NetworkHelper.PROFILE_IMAGE, file);

                    Log.e(TAG, "request := " + json.toString());
                    Log.e(TAG, "requestJson := " + json);
                }

                WebService.post(mContext, NetworkHelper.API_UPLOAD_UNIVERSITY_LOGO, json, responseHandler);
            } else {
                callEditUserApi(Utils.getLoggedInUser(mContext).getProfileImage());
            }
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

                if (resultJSON.has("users") && resultJSON.get("users") instanceof JSONObject && resultJSON.getJSONObject("users").has(NetworkHelper.PROFILE_IMAGE)) {

                    File file = new File(mImageCaptureUri.getPath());
                    if (file.exists())
                        file.delete();

                    JSONObject user = resultJSON.getJSONObject("users");

                    callEditUserApi(user.getString(NetworkHelper.PROFILE_IMAGE));

                } else {
                    AlertDialogHelper.showDialog(mContext, getString(R.string.error_while_uploading_profile_image), null);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Utils.showProgressDialog(mContext,"");
            }

        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            error.printStackTrace();
            Utils.hideProgressDialog();
        }
    };


    private void callEditUserApi(String imagename) {

        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put(NetworkHelper.ID, Utils.getLoggedInUser(mContext).getId());
            jsonObject.put(NetworkHelper.LOGIN_FIRSTNAME, edProfileFirstName.getText().toString().trim());
            jsonObject.put(NetworkHelper.LOGIN_LASTNAME, edProfileLastName.getText().toString().trim());
            jsonObject.put(NetworkHelper.EMAIL, edProfileEmail.getText().toString().trim());
            jsonObject.put(NetworkHelper.PROFILE_IMAGE, imagename);
            jsonObject.put(NetworkHelper.PROFILE_CONTACT_NO, edProfileMobileNumber.getText().toString().trim());

            Log.e(TAG, "request := " + jsonObject.toString());
            StringEntity stringEntity = new StringEntity(jsonObject.toString());

            WebService.postWithAuth(mContext, NetworkHelper.API_EDIT_USER, stringEntity, NetworkHelper.CONTENT_TYPE_JSON, null, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    try {

                        String response = new String(responseBody);
                        Log.e(TAG, "response := " + response);
                        JSONObject resultJSON = new JSONObject(response);

                        Utils.hideProgressDialog();
                        if (resultJSON.has(NetworkHelper.CODE) && resultJSON.getInt(NetworkHelper.CODE) == NetworkHelper.CODE200) {

                            ResendCodeResponse resendCodeResponse = gson.fromJson(response, ResendCodeResponse.class);

                            User user = resendCodeResponse.getUsers();
                            ArrayList<User> users = new ArrayList<>();
                            users.add(user);


                            Utils.setLoggedInUser(user);

                            LoginResponseModel loginResponseModel = gson.fromJson(SharedPrefsUtils.getStringPreference(mContext, Const.LOGIN_RESPONSE), LoginResponseModel.class);
                            user.setUserdepartments(loginResponseModel.getUsers().get(0).getUserdepartments());
                            loginResponseModel.setUsers(users);

                            String updatedLoginResponse = gson.toJson(loginResponseModel);
                            SharedPrefsUtils.setStringPreference(mContext, Const.LOGIN_RESPONSE, updatedLoginResponse);

                            AlertDialogHelper.showDialog(mContext, getString(R.string.the_user_has_been_edited), new RecyclerItemClick() {
                                @Override
                                public void onRecyclerItemClick(int position) {
                                    startActivity(new Intent(mContext, MainActivity.class));
                                    ((MainActivity) mContext).finishAffinity();
                                }
                            });

                        } else {

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
            });

        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                openDialog();
            }
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mImageCaptureUri = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".provider", new File(Environment.getExternalStorageDirectory(), "tmp_avatar-" + System.currentTimeMillis() + ".jpg"));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, Const.OPEN_CAMERA_REQUESTCODE);
    }

    private void openGallery() {

        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, Const.OPEN_GALLERY_REQUESTCODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Const.OPEN_GALLERY_REQUESTCODE:

                if (resultCode == Activity.RESULT_OK) {
                    mImageCaptureUri = data.getData();
                    Log.e("Upload", "image uri = " + mImageCaptureUri.getPath());

                    Picasso.with(mContext).load(mImageCaptureUri).transform(new CircleTransform()).fit().placeholder(R.drawable.ic_profile_placeholder).into(ivProfileImage);
                }
                break;

            case Const.OPEN_CAMERA_REQUESTCODE:
                if (resultCode == Activity.RESULT_OK) {
                    Log.e("Upload", "image uri = " + mImageCaptureUri);

                      ivProfileImage.setImageURI(mImageCaptureUri);
                    /*if (mImageCaptureUri != null)
                        Picasso.with(mContext).load(mImageCaptureUri).transform(new CircleTransform()).placeholder(R.drawable.ic_profile_placeholder).fit().into(ivProfileImage);*/

                }
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {

            validateProfileparams();
            return true;
        }
        return false;
    }
}
