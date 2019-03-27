package com.technostacks.almaktaba.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.technostacks.almaktaba.R;
import com.technostacks.almaktaba.dialog.AlertDialogHelper;
import com.technostacks.almaktaba.listener.RecyclerItemClick;
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

public class ChangePassActivity extends BaseActivity {

    private static final String TAG = ChangePassActivity.class.getSimpleName();
    private Context mContext;
    @BindView(R.id.ed_change_current_pass)
    EditText edChangeCurrentPass;
    @BindView(R.id.ed_change_new_pass)
    EditText edChangeNewPass;
    @BindView(R.id.tv_show_change_pwd)
    TextView tvShowChangePwd;
    @BindView(R.id.ed_change_confirm_pass)
    EditText edChangeConfirmPass;
    @BindView(R.id.tv_show_change_confirm_pwd)
    TextView tvShowChangeConfirmPwd;
    @BindView(R.id.btn_change_pass_submit)
    Button btnChangePassSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    //    setContentView(R.layout.activity_change_pass);
        mContext = this;
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_change_pass,container);
        ButterKnife.bind(this,view);

        setToolbarTitle(getString(R.string.change_password));
        ivMenu.setVisibility(View.GONE);
        tvForgotBack.setVisibility(View.VISIBLE);
        tvForgotBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hidekeyBoard(ChangePassActivity.this);
                onBackPressed();
            }
        });

    }

    @OnClick({R.id.tv_show_change_pwd, R.id.tv_show_change_confirm_pwd, R.id.btn_change_pass_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_show_change_pwd:

                if (!edChangeNewPass.getText().toString().isEmpty()){
                    if (tvShowChangePwd.getText().toString().equalsIgnoreCase(getString(R.string.show))){
                        tvShowChangePwd.setText(R.string.hide);
                        edChangeNewPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    }else {
                        tvShowChangePwd.setText(R.string.show);
                        edChangeNewPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    }
                }

                break;
            case R.id.tv_show_change_confirm_pwd:

                if (!edChangeConfirmPass.getText().toString().isEmpty()){
                    if (tvShowChangeConfirmPwd.getText().toString().equalsIgnoreCase(getString(R.string.hide))){
                        tvShowChangeConfirmPwd.setText(R.string.show);
                        edChangeConfirmPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    }else {
                        tvShowChangeConfirmPwd.setText(R.string.hide);
                        edChangeConfirmPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    }
                }

                break;
            case R.id.btn_change_pass_submit:

                if (edChangeCurrentPass.getText().toString().trim().isEmpty())
                    Toast.makeText(mContext, R.string.please_enter_current_password, Toast.LENGTH_SHORT).show();
                else if(edChangeNewPass.getText().toString().trim().isEmpty())
                    Toast.makeText(mContext, R.string.please_enter_new_password, Toast.LENGTH_SHORT).show();
                else if (edChangeConfirmPass.getText().toString().trim().isEmpty())
                    Toast.makeText(mContext, R.string.please_enter_confirm_password, Toast.LENGTH_SHORT).show();
                else if (!edChangeNewPass.getText().toString().trim().matches(edChangeConfirmPass.getText().toString().trim()))
                    Toast.makeText(this, R.string.password_not_matching, Toast.LENGTH_SHORT).show();
                else
                    callChangePassApi();

                break;
        }
    }

    private void callChangePassApi(){

        Utils.showProgressDialog(mContext,"");
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put(NetworkHelper.ID, Utils.getLoggedInUser(mContext).getId());
            jsonObject.put(NetworkHelper.CURRENT_PASS, edChangeCurrentPass.getText().toString().trim());
            jsonObject.put(NetworkHelper.NEW_PASS, edChangeNewPass.getText().toString().trim());

            Log.e(TAG, "json request = " + jsonObject);
            StringEntity stringEntity = new StringEntity(jsonObject.toString());

            WebService.postWithAuth(mContext,NetworkHelper.API_CHANGE_PASSWORD, stringEntity, NetworkHelper.CONTENT_TYPE_JSON, null,responseHandler);

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

                        AlertDialogHelper.showDialog(mContext, getString(R.string.password_has_been_changed), new RecyclerItemClick() {
                            @Override
                            public void onRecyclerItemClick(int position) {
                                onBackPressed();
                            }
                        });

                    }else {
                        AlertDialogHelper.showDialog(mContext, getString(R.string.current_password_does_not_match), null);
                    }

                }else {
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
}
