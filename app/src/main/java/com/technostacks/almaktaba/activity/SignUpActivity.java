package com.technostacks.almaktaba.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.util.Util;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.technostacks.almaktaba.R;
import com.technostacks.almaktaba.listener.RecyclerItemClick;
import com.technostacks.almaktaba.dialog.AlertDialogHelper;
import com.technostacks.almaktaba.model.LoginResponseModel;
import com.technostacks.almaktaba.util.Const;
import com.technostacks.almaktaba.util.ProgressDialogHelper;
import com.technostacks.almaktaba.util.SharedPrefsUtils;
import com.technostacks.almaktaba.util.Utils;
import com.technostacks.almaktaba.webservices.NetworkHelper;
import com.technostacks.almaktaba.webservices.WebService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class SignUpActivity extends BaseActivity implements TextView.OnEditorActionListener {

    public static final String TAG = SignUpActivity.class.getSimpleName();
    @BindView(R.id.ed_signup_user_email)
    EditText edSignupUserEmail;
    @BindView(R.id.ed_signup_user_pwd)
    EditText edSignupUserPwd;
    @BindView(R.id.tv_show_signup_pwd)
    TextView tvShowSignupPwd;
    @BindView(R.id.ed_signup_user_confirm_pwd)
    EditText edSignupUserConfirmPwd;
    @BindView(R.id.tv_show_signup_confirm_pwd)
    TextView tvShowSignupConfirmPwd;
    @BindView(R.id.btn_signup)
    Button btnLogin;
    Gson gson;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        gson = Utils.getGson();
        ButterKnife.bind(this);
        mContext = this;

        edSignupUserConfirmPwd.setOnEditorActionListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick({R.id.tv_show_signup_pwd, R.id.tv_show_signup_confirm_pwd, R.id.btn_signup,R.id.tv_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_show_signup_pwd:
                if (!edSignupUserPwd.getText().toString().isEmpty()) {
                    if (tvShowSignupPwd.getText().toString().equalsIgnoreCase(getString(R.string.show))) {
                        tvShowSignupPwd.setText(R.string.hide);
                        edSignupUserPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    } else {
                        tvShowSignupPwd.setText(R.string.show);
                        edSignupUserPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    }
                }
                break;
            case R.id.tv_show_signup_confirm_pwd:
                if (!edSignupUserConfirmPwd.getText().toString().isEmpty()) {
                    if (tvShowSignupConfirmPwd.getText().toString().equalsIgnoreCase(getString(R.string.hide))) {
                        tvShowSignupConfirmPwd.setText(R.string.show);
                        edSignupUserConfirmPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    } else {
                        tvShowSignupConfirmPwd.setText(R.string.hide);
                        edSignupUserConfirmPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    }
                }
                break;
            case R.id.btn_signup:
                performSignUp();
                break;
            case R.id.tv_login:
                onBackPressed();
                break;
        }
    }

    private void performSignUp() {

        if (edSignupUserEmail.getText().toString().isEmpty())
            Toast.makeText(this, R.string.please_enter_email, Toast.LENGTH_SHORT).show();
        else if (!Utils.isValidEmail(edSignupUserEmail.getText().toString().trim()))
            Toast.makeText(this, getString(R.string.enter_valid_email), Toast.LENGTH_SHORT).show();
        else if (edSignupUserPwd.getText().toString().isEmpty())
            Toast.makeText(this, R.string.please_enter_password, Toast.LENGTH_SHORT).show();
        else if (edSignupUserConfirmPwd.getText().toString().isEmpty())
            Toast.makeText(this, R.string.please_enter_confirm_password, Toast.LENGTH_SHORT).show();
        else if (!edSignupUserConfirmPwd.getText().toString().trim().matches(edSignupUserPwd.getText().toString().trim()))
            Toast.makeText(this, R.string.password_not_matching, Toast.LENGTH_SHORT).show();
        else
            callSignupApi();

    }

    private void callSignupApi() {
        Utils.showProgressDialog(mContext,"");
        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put(NetworkHelper.ADD_USER_EMAIL, edSignupUserEmail.getText().toString().trim());
            jsonObject.put(NetworkHelper.ADD_USER_PASSWORD, edSignupUserPwd.getText().toString().trim());
            jsonObject.put(NetworkHelper.ADD_USER_REGISTER_TYPE, NetworkHelper.REGISTER_TYPE_NORMAL);
            jsonObject.put(NetworkHelper.ADD_USER_ROLE_ID, NetworkHelper.ROLE_ID);

            Log.e(TAG, "json request = " + jsonObject);
            StringEntity stringEntity = new StringEntity(jsonObject.toString());

            WebService.post(mContext, NetworkHelper.API_ADD_USER, stringEntity, NetworkHelper.CONTENT_TYPE_JSON,responseHandler);


        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            try {
                String response = new String(responseBody);
                Log.e(TAG, "response := " + response);
                JSONObject jsonObject = new JSONObject(response);
                Utils.hideProgressDialog();

                if (jsonObject.has(NetworkHelper.CODE)) {

                    if (jsonObject.getInt(NetworkHelper.CODE) == NetworkHelper.CODE200) {

                        AlertDialogHelper.showDialog(mContext, jsonObject.getString(NetworkHelper.MESSAGE), new RecyclerItemClick() {
                            @Override
                            public void onRecyclerItemClick(int position) {

                                LoginResponseModel loginResponseModel = gson.fromJson(response, LoginResponseModel.class);
                                Intent intent = new Intent(mContext, UserVerificationActivity.class);
                                intent.putExtra(Const.LOGIN_RESPONSE, loginResponseModel);
                                startActivity(intent);
                                // SignUpActivity.this.finish();
                            }
                        });

                    } else {
                        AlertDialogHelper.showDialog(mContext, getString(R.string.email_already_exists), null);
                    }
                } else {
                    AlertDialogHelper.showDialog(mContext, jsonObject.getString(NetworkHelper.MESSAGE), null);
                }

            } catch (JSONException e) {
                Utils.hideProgressDialog();
                e.printStackTrace();
            }
        }


        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            Utils.hideProgressDialog();
            Log.e(TAG, "error : " + error.toString());
            error.printStackTrace();
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
            Utils.hidekeyBoard(this);
            performSignUp();
            return true;
        }
        return false;
    }
}
