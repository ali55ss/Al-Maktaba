package com.technostacks.almaktaba.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.util.Util;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.technostacks.almaktaba.R;
import com.technostacks.almaktaba.listener.RecyclerItemClick;
import com.technostacks.almaktaba.dialog.AlertDialogHelper;
import com.technostacks.almaktaba.util.ProgressDialogHelper;
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

public class ForgotPassActivity extends AppCompatActivity {

    public static final String TAG = ForgotPassActivity.class.getSimpleName();
    @BindView(R.id.tv_forgot_back)
    TextView tvForgotBack;
    @BindView(R.id.ed_forgot_user_email)
    EditText edForgotUserEmail;
    @BindView(R.id.btn_reset_pass)
    Button btnSignup;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);
        ButterKnife.bind(this);
        mContext = this;

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick({R.id.tv_forgot_back, R.id.btn_reset_pass})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_forgot_back:
                onBackPressed();
                break;
            case R.id.btn_reset_pass:

                if (edForgotUserEmail.getText().toString().isEmpty())
                    Toast.makeText(this, getString(R.string.please_enter_email), Toast.LENGTH_SHORT).show();
                else if (!Utils.isValidEmail(edForgotUserEmail.getText().toString().trim()))
                    Toast.makeText(this, getString(R.string.enter_valid_email), Toast.LENGTH_SHORT).show();
                else {
                    Utils.showProgressDialog(mContext,"");

                    try {
                        JSONObject jsonObject = new JSONObject();

                        jsonObject.put(NetworkHelper.ADD_USER_EMAIL,edForgotUserEmail.getText().toString().trim());
                        Log.e(TAG, "json request = " + jsonObject);
                        StringEntity stringEntity = new StringEntity(jsonObject.toString());

                        WebService.post(mContext,NetworkHelper.API_FORGOT_PASSWORD,stringEntity,NetworkHelper.CONTENT_TYPE_JSON, responseHandler);

                    } catch (JSONException | UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            try {

                String response = new String(responseBody);
                Log.e(TAG, "response := " + response);
                JSONObject jsonObject = new JSONObject(response);

                if (jsonObject.has(NetworkHelper.CODE)){

                    if (jsonObject.getInt(NetworkHelper.CODE) == NetworkHelper.CODE200) {

                        AlertDialogHelper.showDialog(mContext, getString(R.string.email_send_successfully), new RecyclerItemClick() {
                            @Override
                            public void onRecyclerItemClick(int position) {

                            }
                        });
                    }else {
                        AlertDialogHelper.showDialog(mContext,jsonObject.getString(NetworkHelper.MESSAGE),null);
                    }
                }else{
                    AlertDialogHelper.showDialog(mContext,jsonObject.getString(NetworkHelper.MESSAGE),null);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Utils.hideProgressDialog();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            Log.e(TAG, "response := " + error.getMessage());
            error.printStackTrace();
            Utils.hideProgressDialog();
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
    }
}
