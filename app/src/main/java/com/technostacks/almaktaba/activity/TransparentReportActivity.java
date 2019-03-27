package com.technostacks.almaktaba.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.util.Util;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.technostacks.almaktaba.R;
import com.technostacks.almaktaba.dialog.AlertDialogHelper;
import com.technostacks.almaktaba.dialog.ReportTypeDialog;
import com.technostacks.almaktaba.listener.RecyclerItemClick;
import com.technostacks.almaktaba.model.DocumentResponseModel;
import com.technostacks.almaktaba.util.Const;
import com.technostacks.almaktaba.util.ProgressDialogHelper;
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

public class TransparentReportActivity extends AppCompatActivity {

    public static final String TAG = TransparentReportActivity.class.getSimpleName();
    @BindView(R.id.iv_report_cancel)
    ImageView ivReportCancel;
    @BindView(R.id.ed_report_type)
    EditText edReportType;
    @BindView(R.id.ti_report_type)
    TextInputLayout tiReportType;
    @BindView(R.id.ed_document_details)
    EditText edDocumentDetails;
    @BindView(R.id.btn_submit_report)
    Button btnSubmitReport;
    @BindView(R.id.rl_report_type)
    RelativeLayout rlReportType;
    private Context mContext;
    ArrayList<String> listReportTypes;
    Gson gson;
    private DocumentResponseModel.Document document;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_report_document);
        ButterKnife.bind(this);
        mContext = this;

        if (getIntent().getExtras()!=null)
            document = getIntent().getParcelableExtra(Const.DOCUMENT_RESPONSE);

        listReportTypes = new ArrayList<>();
        listReportTypes.add(mContext.getString(R.string.not_related_to_this_course));
        listReportTypes.add(mContext.getString(R.string.contains_advertisement));
        listReportTypes.add(mContext.getString(R.string.other));

        gson = Utils.getGson();

    }

    @OnClick({ R.id.btn_submit_report,R.id.iv_report_cancel,R.id.ed_report_type})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_submit_report:
                if (edDocumentDetails.getText().toString().isEmpty())
                    tiReportType.setError(getString(R.string.please_select_report_type));
                else if (edReportType.getText().toString().isEmpty()){
                    tiReportType.setError(null);
                    Toast.makeText(mContext, getString(R.string.please_write_document_details), Toast.LENGTH_SHORT).show();
                }else{
                    tiReportType.setError(null);
                    callDocumentReportApi();
                }

                break;
            case R.id.iv_report_cancel:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.ed_report_type:
                ReportTypeDialog reportTypeDialog = new ReportTypeDialog(mContext,listReportTypes);
                reportTypeDialog.shoeReportTypeDialog(recyclerItemClick);
            break;
        }
    }

    RecyclerItemClick recyclerItemClick = new RecyclerItemClick() {
        @Override
        public void onRecyclerItemClick(int position) {
            edReportType.setText(listReportTypes.get(position));
        }
    };

    private void callDocumentReportApi(){
        Utils.showProgressDialog(mContext,"");

        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put(NetworkHelper.USER_ID, document.getUserId());
            jsonObject.put(NetworkHelper.DOCUMENT_ID,document.getId());
            jsonObject.put(NetworkHelper.REPORT_TYPE,edReportType.getText().toString().trim());
            jsonObject.put(NetworkHelper.REPORT_DESCRIPTION,edDocumentDetails.getText().toString().trim());

            Log.e(TAG,"json request = "+jsonObject);
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            WebService.postWithAuth(mContext,NetworkHelper.API_ADD_DOCUMENTREPORT,stringEntity,NetworkHelper.CONTENT_TYPE_JSON,null,responseHandler);

        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            try {
                String response = new String(responseBody);
                Log.e(TAG, "response = " + response);
                JSONObject jsonObject = jsonObject = new JSONObject(response);;

                if (jsonObject.has(NetworkHelper.CODE) && jsonObject.getInt(NetworkHelper.CODE) == NetworkHelper.CODE200) {

                    Toast.makeText(TransparentReportActivity.this, ""+jsonObject.getString(NetworkHelper.MESSAGE), Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();

                }else
                    AlertDialogHelper.showDialog(mContext, jsonObject.getString(NetworkHelper.MESSAGE), null);

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
        }
    };
}
