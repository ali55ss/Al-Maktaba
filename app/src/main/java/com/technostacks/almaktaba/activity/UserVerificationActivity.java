package com.technostacks.almaktaba.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.util.Util;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.technostacks.almaktaba.R;
import com.technostacks.almaktaba.dialog.AlertDialogHelper;
import com.technostacks.almaktaba.listener.RecyclerItemClick;
import com.technostacks.almaktaba.model.LoginResponseModel;
import com.technostacks.almaktaba.model.ResendCodeResponse;
import com.technostacks.almaktaba.model.User;
import com.technostacks.almaktaba.util.Const;
import com.technostacks.almaktaba.util.ProgressDialogHelper;
import com.technostacks.almaktaba.util.SharedPrefsUtils;
import com.technostacks.almaktaba.util.Utils;
import com.technostacks.almaktaba.webservices.NetworkHelper;
import com.technostacks.almaktaba.webservices.WebService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class UserVerificationActivity extends AppCompatActivity {

    public static final String TAG = UserVerificationActivity.class.getSimpleName();
    @BindView(R.id.tv_verify_email)
    TextView tvVerifyEmail;
    @BindView(R.id.ed_otp_1)
    EditText edOtp1;
    @BindView(R.id.ed_otp_2)
    EditText edOtp2;
    @BindView(R.id.ed_otp_3)
    EditText edOtp3;
    @BindView(R.id.ed_otp_4)
    EditText edOtp4;
    @BindView(R.id.ll_otp)
    LinearLayout llOtp;
    @BindView(R.id.btn_verify_submit)
    Button btnVerifySubmit;
    @BindView(R.id.tv_resend_verification_code)
    TextView tvResendVerificationCode;
    private Context mContext;
    LoginResponseModel signupResponse;
    User user;
    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_verification);
        ButterKnife.bind(this);
        mContext = this;

        if (getIntent().getExtras()!=null){
            signupResponse = getIntent().getParcelableExtra(Const.LOGIN_RESPONSE);
            user = signupResponse.getUsers().get(0);
        }


//        AlertDialogHelper.showDialog(mContext,"Verification code : "+user.getVerificationCode(),null);
        gson = new Gson();
        tvVerifyEmail.setText(user.getEmail());
        edOtp1.addTextChangedListener(textwatcher1);
        edOtp2.addTextChangedListener(textwatcher2);
        edOtp3.addTextChangedListener(textwatcher3);
        edOtp4.addTextChangedListener(textwatcher4);
    }

    @OnClick({R.id.btn_verify_submit, R.id.tv_resend_verification_code})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_verify_submit:

                checkOtp();

                break;
            case R.id.tv_resend_verification_code:
                callResendOtpApi();
                break;
        }
    }

    TextWatcher textwatcher1 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }
        @Override
        public void afterTextChanged(Editable s) {

            if (s.toString().length() == 1)
                edOtp2.requestFocus();

        }
    };
    TextWatcher textwatcher2 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            if (charSequence.toString().length() == 0)
                edOtp1.requestFocus();
        }
        @Override
        public void afterTextChanged(Editable s) {

            if (s.toString().length() == 1)
                edOtp3.requestFocus();
        }
    };
    TextWatcher textwatcher3 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            if (charSequence.toString().length() == 0)
                edOtp2.requestFocus();
        }
        @Override
        public void afterTextChanged(Editable s) {

            if (s.toString().length() == 1)
                edOtp4.requestFocus();
        }
    };
    TextWatcher textwatcher4 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (charSequence.toString().length() == 0)
                edOtp3.requestFocus();
        }
        @Override
        public void afterTextChanged(Editable s) {

            if (s.toString().length() == 1){

                Utils.hidekeyBoard(UserVerificationActivity.this);
                checkOtp();
            }

        }
    };

    private void callResendOtpApi(){

        Utils.showProgressDialog(mContext,"");
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(NetworkHelper.ID,user.getId());

            Log.e(TAG,"request json = "+jsonObject);
            WebService.post(mContext, NetworkHelper.API_RESEND_CODE, new StringEntity(jsonObject.toString()), NetworkHelper.CONTENT_TYPE_JSON,new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    try {

                        String response = new String(responseBody);
                        Log.e(TAG, "response := " + response);
                        JSONObject resultJSON = new JSONObject(response);
                        Utils.hideProgressDialog();
                        if (resultJSON.has(NetworkHelper.CODE) && resultJSON.getInt(NetworkHelper.CODE) == NetworkHelper.CODE200) {

                            ResendCodeResponse resendCodeResponse = gson.fromJson(response,ResendCodeResponse.class);

                            user = resendCodeResponse.getUsers();
                            ArrayList<User> users = new ArrayList<>();
                            users.add(user);
                            signupResponse.setUsers(users);

//                            AlertDialogHelper.showDialog(mContext,"Verification code : "+user.getVerificationCode(),null);

                        }else {
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

    private void checkOtp(){

        if (!edOtp1.getText().toString().isEmpty() && !edOtp2.getText().toString().isEmpty() && !edOtp3.getText().toString().isEmpty() && !edOtp4.getText().toString().isEmpty() ){

            String otp = edOtp1.getText().toString().trim()+edOtp2.getText().toString().trim()+edOtp3.getText().toString().trim()+edOtp4.getText().toString().trim();
            Log.e(TAG,"otp = "+otp);
            if (otp.equalsIgnoreCase(user.getVerificationCode())){
                callUpdateUserApi();
            }else {
                Toast.makeText(mContext, "Wrong verification code!", Toast.LENGTH_SHORT).show();
            }
        }else
            Toast.makeText(mContext, "Please enter verification code!", Toast.LENGTH_SHORT).show();


    }
    private void callUpdateUserApi(){

        Utils.showProgressDialog(mContext,"");
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(NetworkHelper.ID,user.getId());
            jsonObject.put(NetworkHelper.VERIFICATION_CODE,"");
            jsonObject.put(NetworkHelper.STATUS,1);

            StringEntity stringEntity = new StringEntity(jsonObject.toString());

            WebService.post(mContext, NetworkHelper.API_EDIT_USER, stringEntity, NetworkHelper.CONTENT_TYPE_JSON, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    try {
                        String response = new String(responseBody);
                        Log.e(TAG, "response := " + response);
                        JSONObject resultJSON = new JSONObject(response);

                        Utils.hideProgressDialog();
                        if (resultJSON.has(NetworkHelper.CODE) && resultJSON.getInt(NetworkHelper.CODE) == NetworkHelper.CODE200) {

                            String jsonResponse = gson.toJson(signupResponse);
                            SharedPrefsUtils.setStringPreference(mContext,Const.LOGIN_RESPONSE,jsonResponse);

                            if (signupResponse.getUsers().get(0).getUserdepartments()!=null && !signupResponse.getUsers().get(0).getUserdepartments().isEmpty() ){

                                String departmentResponse = gson.toJson(signupResponse.getUsers().get(0).getUserdepartments().get(0));
                                SharedPrefsUtils.setStringPreference(mContext,Const.DEPARTMENT_RESPONSE,departmentResponse);
                            }

                            SharedPrefsUtils.setBooleanPreference(mContext, Const.IS_LOGIN,true);
                            AlertDialogHelper.showDialog(mContext, "Verified Successfully!", new RecyclerItemClick() {
                                @Override
                                public void onRecyclerItemClick(int position) {
                                    startActivity(new Intent(mContext, MainActivity.class));
                                    ((UserVerificationActivity)mContext).finishAffinity();
                                }
                            });

                        }else {

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
}
