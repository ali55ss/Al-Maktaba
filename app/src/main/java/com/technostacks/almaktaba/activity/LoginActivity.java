package com.technostacks.almaktaba.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.technostacks.almaktaba.R;
import com.technostacks.almaktaba.dialog.AlertDialogHelper;
import com.technostacks.almaktaba.listener.RecyclerItemClick;
import com.technostacks.almaktaba.model.FbLoginResponseModel;
import com.technostacks.almaktaba.model.LoginResponseModel;
import com.technostacks.almaktaba.util.Const;
import com.technostacks.almaktaba.util.SharedPrefsUtils;
import com.technostacks.almaktaba.util.Utils;
import com.technostacks.almaktaba.webservices.NetworkHelper;
import com.technostacks.almaktaba.webservices.WebService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

import static com.technostacks.almaktaba.util.Utils.printHashKey;

public class LoginActivity extends AppCompatActivity implements FacebookCallback<LoginResult>, GraphRequest.GraphJSONObjectCallback, TextView.OnEditorActionListener {

    public static final String TAG = LoginActivity.class.getSimpleName();
    public static final int GOOGLE_SIGN_IN = 111;
    LoginManager fbLoginBtn;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.iv_fb_login)
    ImageView ivFbLogin;
    @BindView(R.id.iv_google_login)
    ImageView ivGoogleLogin;
    @BindView(R.id.ed_user_email)
    EditText edEmail;
    @BindView(R.id.ed_user_password)
    EditText edPassword;
    @BindView(R.id.tv_signup)
    TextView tvSignUp;
    @BindView(R.id.sw_shipment_value_paid_selection)
    Switch swRememberMe;
    CallbackManager callbackManager;
    GoogleSignInClient mGoogleSignInClient;
    Gson gson;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        ButterKnife.bind(this);
        printHashKey(LoginActivity.this);
        mContext = this;
        gson = Utils.getGson();
        callbackManager = CallbackManager.Factory.create();
        fbLoginBtn = LoginManager.getInstance();
        //  fbLoginBtn.setReadPermissions(Arrays.asList("email","public_profile"));
        fbLoginBtn.registerCallback(callbackManager, this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut();

        if (SharedPrefsUtils.getStringPreference(mContext, Const.LOGIN_EMAIL) != null)
            edEmail.setText(SharedPrefsUtils.getStringPreference(mContext, Const.LOGIN_EMAIL));

        if (SharedPrefsUtils.getStringPreference(mContext, Const.LOGIN_PASSWORD) != null)
            edPassword.setText(SharedPrefsUtils.getStringPreference(mContext, Const.LOGIN_PASSWORD));
        Utils.hidekeyBoard(this);
        edPassword.setOnEditorActionListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick({R.id.btn_login, R.id.iv_fb_login, R.id.iv_google_login, R.id.tv_signup, R.id.tv_forgot_password,R.id.tv_guest})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_login:

                performLogin();

                break;
            case R.id.iv_fb_login:
                // fbLoginBtn.performClick();
                fbLoginBtn.logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "public_profile"));
                break;
            case R.id.iv_google_login:
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
                break;
            case R.id.tv_signup:
                startActivity(new Intent(this, SignUpActivity.class));
                break;
            case R.id.tv_forgot_password:
                startActivity(new Intent(this, ForgotPassActivity.class));
                break;
            case R.id.tv_guest:

                callGuestLoginApi();

                /*Intent intent = new Intent(mContext,MainActivity.class);
                intent.putExtra(Const.IS_GUEST_USER,true);
                startActivity(intent);*/
                break;
        }
    }

    @Override
    public void onSuccess(LoginResult loginResult) {

        Log.e(TAG, "access token = " + loginResult.getAccessToken().getToken());

        GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), this);
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, first_name, last_name, email,gender");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
    }

    @Override
    public void onCancel() {
        Log.e(TAG, "fb request cancelled!");
    }

    @Override
    public void onError(FacebookException error) {
        Log.e(TAG,"Facebook LoginError"+ error.toString());
        error.printStackTrace();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GOOGLE_SIGN_IN:
                // The Task returned from this call is always completed, no need to attach
                // a listener.
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);
                break;
            default:
                LoginManager.getInstance().logOut();
                callbackManager.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void onCompleted(JSONObject object, GraphResponse response) {
        Log.e(TAG, "Facebooklogin " + object.toString());
        FbLoginResponseModel fbLoginResponseModel = gson.fromJson(object.toString(), FbLoginResponseModel.class);
        Profile profile = Profile.getCurrentProfile();
        if (profile != null) {
            fbLoginResponseModel.setImage_url(profile.getProfilePictureUri(150, 150).toString());
        }
        callLoginApi(NetworkHelper.REGISTER_TYPE_FACEBOOK, fbLoginResponseModel);
    }


    private void performLogin() {

        if (edEmail.getText().toString().trim().isEmpty())
            Toast.makeText(this, getString(R.string.please_enter_email), Toast.LENGTH_SHORT).show();
        else if (!Utils.isValidEmail(edEmail.getText().toString().trim()))
            Toast.makeText(this, getString(R.string.enter_valid_email), Toast.LENGTH_SHORT).show();
        else if (edPassword.getText().toString().trim().isEmpty())
            Toast.makeText(this, getString(R.string.please_enter_password), Toast.LENGTH_SHORT).show();
        else {

            callLoginApi(NetworkHelper.REGISTER_TYPE_NORMAL, null);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.e(TAG, "account " + account.getDisplayName());
            FbLoginResponseModel fbLoginResponseModel = new FbLoginResponseModel();
            fbLoginResponseModel.setId(account.getId());
            fbLoginResponseModel.setEmail(account.getEmail());
            fbLoginResponseModel.setFirst_name(account.getDisplayName());

            callLoginApi(NetworkHelper.REGISTER_TYPE_GOOGLE, fbLoginResponseModel);
        } catch (ApiException e) {
            // The ApiException st  atus code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e(TAG, "signInResult:failed code=" + e.getStatusCode());

        }
    }

    private void callLoginApi(int login_type, FbLoginResponseModel loginResponseModel) {

        Utils.showProgressDialog(mContext,"");
    //    progress.show();
        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put(NetworkHelper.ADD_USER_ROLE_ID, NetworkHelper.ROLE_ID);
            switch (login_type) {
                case NetworkHelper.REGISTER_TYPE_NORMAL:

                    jsonObject.put(NetworkHelper.ADD_USER_EMAIL, edEmail.getText().toString().trim());
                    jsonObject.put(NetworkHelper.ADD_USER_PASSWORD, edPassword.getText().toString().trim());
                    jsonObject.put(NetworkHelper.ADD_USER_REGISTER_TYPE, NetworkHelper.REGISTER_TYPE_NORMAL);

                    break;
                case NetworkHelper.REGISTER_TYPE_FACEBOOK:

                    jsonObject.put(NetworkHelper.ADD_USER_REGISTER_TYPE, NetworkHelper.REGISTER_TYPE_FACEBOOK);

                    if (loginResponseModel.getEmail() != null)
                        jsonObject.put(NetworkHelper.ADD_USER_EMAIL, loginResponseModel.getEmail());
                    if (loginResponseModel.getFirst_name() != null)
                        jsonObject.put(NetworkHelper.LOGIN_FIRSTNAME, loginResponseModel.getFirst_name());
                    if (loginResponseModel.getLast_name() != null)
                        jsonObject.put(NetworkHelper.LOGIN_LASTNAME, loginResponseModel.getLast_name());
                    if (loginResponseModel.getImage_url() != null)
                        jsonObject.put(NetworkHelper.LOGIN_PROFILE_URL, loginResponseModel.getImage_url());
                    jsonObject.put(NetworkHelper.ADD_USER_PASSWORD, Calendar.getInstance().getTimeInMillis());

                    break;
                case NetworkHelper.REGISTER_TYPE_GOOGLE:

                    jsonObject.put(NetworkHelper.ADD_USER_REGISTER_TYPE, NetworkHelper.REGISTER_TYPE_GOOGLE);

                    if (loginResponseModel.getEmail() != null)
                        jsonObject.put(NetworkHelper.ADD_USER_EMAIL, loginResponseModel.getEmail());
                    if (loginResponseModel.getFirst_name() != null)
                        jsonObject.put(NetworkHelper.LOGIN_FIRSTNAME, loginResponseModel.getFirst_name());
                    if (loginResponseModel.getLast_name() != null)
                        jsonObject.put(NetworkHelper.LOGIN_LASTNAME, loginResponseModel.getLast_name());
                    if (loginResponseModel.getImage_url() != null)
                        jsonObject.put(NetworkHelper.LOGIN_PROFILE_URL, loginResponseModel.getImage_url());
                    jsonObject.put(NetworkHelper.ADD_USER_PASSWORD, Calendar.getInstance().getTimeInMillis());

                    break;
            }

            Log.e(TAG, "json request = " + jsonObject);

            StringEntity stringEntity = new StringEntity(jsonObject.toString());

            WebService.post(mContext, NetworkHelper.API_LOGIN, stringEntity, NetworkHelper.CONTENT_TYPE_JSON, responseHandler);

        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }


    private void callGuestLoginApi() {

        Utils.showProgressDialog(mContext,"");
    //    progress.show();
        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put(NetworkHelper.ADD_USER_ROLE_ID, NetworkHelper.ROLE_ID_GUEST);
            jsonObject.put(NetworkHelper.ADD_USER_EMAIL, "guestuser@gmail.com");
            jsonObject.put(NetworkHelper.ADD_USER_PASSWORD, "guest");
            jsonObject.put(NetworkHelper.ADD_USER_REGISTER_TYPE, NetworkHelper.REGISTER_TYPE_NORMAL);

            Log.e(TAG, "json request = " + jsonObject);

            StringEntity stringEntity = new StringEntity(jsonObject.toString());

            WebService.post(mContext, NetworkHelper.API_LOGIN, stringEntity, NetworkHelper.CONTENT_TYPE_JSON, responseHandler);

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

                if (jsonObject.has(NetworkHelper.CODE) && jsonObject.getInt(NetworkHelper.CODE) == NetworkHelper.CODE200) {

                    //  Toast.makeText(LoginActivity.this, jsonObject.getString(NetworkHelper.MESSAGE), Toast.LENGTH_SHORT).show();

                    LoginResponseModel loginResponseModel = gson.fromJson(response, LoginResponseModel.class);

                    Utils.hideProgressDialog();
                //    progress.hide();
                    if (loginResponseModel.getUsers().get(0).getStatus() == 1) {

                        SharedPrefsUtils.setStringPreference(mContext, Const.LOGIN_RESPONSE, response);
                        if (loginResponseModel.getUsers().get(0).getUserdepartments()!=null && !loginResponseModel.getUsers().get(0).getUserdepartments().isEmpty() ){

                            String departmentResponse = gson.toJson(loginResponseModel.getUsers().get(0).getUserdepartments().get(0));
                            SharedPrefsUtils.setStringPreference(mContext,Const.DEPARTMENT_RESPONSE,departmentResponse);
                        }

                        Intent intent = new Intent(mContext, MainActivity.class);

                        if (loginResponseModel.getUsers().get(0).getRoleId() == 3){
                            intent.putExtra(Const.IS_GUEST_USER,true);
                        } else{
                            SharedPrefsUtils.setBooleanPreference(mContext, Const.IS_LOGIN, true);
                            intent.putExtra(Const.IS_GUEST_USER,false);
                        }


                        if (swRememberMe.isChecked()) {
                            SharedPrefsUtils.setStringPreference(mContext, Const.LOGIN_EMAIL, edEmail.getText().toString().trim());
                            SharedPrefsUtils.setStringPreference(mContext, Const.LOGIN_PASSWORD, edPassword.getText().toString().trim());
                        } else {
                            SharedPrefsUtils.setStringPreference(mContext, Const.LOGIN_EMAIL, null);
                            SharedPrefsUtils.setStringPreference(mContext, Const.LOGIN_PASSWORD, null);
                        }

                        startActivity(intent);
                        finishAffinity();
                    } else {

                        if (loginResponseModel.getUsers().get(0).getVerificationCode()!=null && !loginResponseModel.getUsers().get(0).getVerificationCode().isEmpty()) {

                            AlertDialogHelper.showDialog(mContext, getString(R.string.please_verify_your_account), new RecyclerItemClick() {
                                @Override
                                public void onRecyclerItemClick(int position) {
                                    LoginResponseModel loginResponseModel = gson.fromJson(response, LoginResponseModel.class);
                                    Intent intent = new Intent(mContext, UserVerificationActivity.class);
                                    intent.putExtra(Const.LOGIN_RESPONSE, loginResponseModel);
                                    startActivity(intent);
                                }
                            });
                        }else {
                            AlertDialogHelper.showDialog(mContext,getString(R.string.user_inactivated),null);
                        }
                    }


                } else if (jsonObject.getInt(NetworkHelper.CODE) == 300) {

                    Utils.hideProgressDialog();
                //    progress.hide();
                    AlertDialogHelper.showDialog(mContext, getString(R.string.please_verify_your_account), new RecyclerItemClick() {
                        @Override
                        public void onRecyclerItemClick(int position) {
                            LoginResponseModel loginResponseModel = gson.fromJson(response, LoginResponseModel.class);
                            Intent intent = new Intent(mContext, UserVerificationActivity.class);
                            intent.putExtra(Const.LOGIN_RESPONSE, loginResponseModel);
                            startActivity(intent);
                        }
                    });

                } else {
                    AlertDialogHelper.showDialog(mContext, getString(R.string.invalid_username_password), null);
                    Utils.hideProgressDialog();
                //    progress.hide();
                }
            } catch (JSONException e) {
                Utils.hideProgressDialog();
            //    progress.hide();
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            Utils.hideProgressDialog();
        //    progress.hide();
            Log.e(TAG, "error : " + error.toString());
            error.printStackTrace();
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
    //    progress.destroyProgressObj();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
            Utils.hidekeyBoard(this);
            performLogin();
            return true;
        }
        return false;
    }
}
