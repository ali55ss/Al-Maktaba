package com.technostacks.almaktaba.webservices;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import com.bumptech.glide.util.Util;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.technostacks.almaktaba.R;
import com.technostacks.almaktaba.activity.SignUpActivity;
import com.technostacks.almaktaba.dialog.AlertDialogHelper;
import com.technostacks.almaktaba.model.LoginResponseModel;
import com.technostacks.almaktaba.model.User;
import com.technostacks.almaktaba.util.ProgressDialogHelper;
import com.technostacks.almaktaba.util.Utils;

import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.entity.StringEntity;


/**
 * Created by techno11 on 15/5/17.
 */

public class WebService {

    private static final String TAG = "WebService";

 //   private static final String BASE_URL = "http://almaktaba.technostacks.com/api/";
//    private static final String BASE_URL = "http://18.191.4.243/api/";
    private static final String  BASE_URL = "http://3.18.57.91/api/";
//    private static final String  BASE_URL = "http://php71server104.emizentech.com/Al_Maktaba/api/";
    public static final String LOCAL_URL = "http://192.168.0.160/api_almaktaba/";
    public static final String PROFILE_BASE_URL = BASE_URL + "geturl/profile/"; ///geturl/folder_name/imagename
    public static final String DOCUMENT_BASE_URL = BASE_URL + "geturl/documents/"; //  /geturl/university/document_name

    public static final String UNIVERSITY_BASE_URL = BASE_URL + "geturl/university/";
    public static final String VIDEO_URL = BASE_URL + "webroot/video/";


    private static AsyncHttpClient client = new AsyncHttpClient();

    {
        client.setTimeout(4000000);
        client.setConnectTimeout(4000000);
        client.setResponseTimeout(4000000);

    }


    public static void post(Context mContext,String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {

        if (Utils.isOnline(mContext)){
            Log.e(TAG, "BASE_URL : " + getAbsoluteUrl(url));
            client.removeAllHeaders();
            client.post(getAbsoluteUrl(url), params, responseHandler);
        }else {
            Utils.hideProgressDialog();
            AlertDialogHelper.showDialog(mContext,mContext.getString(R.string.please_check_your_internet_connection),null);
        }

    }

    public static void post(Context mContext, String url, StringEntity params, String content_type,AsyncHttpResponseHandler responseHandler) {

        if (Utils.isOnline(mContext)){
            Log.e(TAG, "BASE_URL : " + getAbsoluteUrl(url));
            client.removeAllHeaders();
            client.post(mContext, getAbsoluteUrl(url), params, content_type, responseHandler);
        }else {
            Utils.hideProgressDialog();
            AlertDialogHelper.showDialog(mContext,mContext.getString(R.string.please_check_your_internet_connection),null);
        }
    }

    public static void postWithAuth(Context mContext, String url, StringEntity params, String content_type, SwipeRefreshLayout swipeRefreshLayout, AsyncHttpResponseHandler responseHandler){

        if (Utils.isOnline(mContext)){
            Log.e(TAG, "BASE_URL : " + getAbsoluteUrl(url));
            User user = Utils.getLoggedInUser(mContext);
            client.removeAllHeaders();
            client.setBasicAuth(user.getEmail(),user.getApiPlainKey());
            client.post(mContext, getAbsoluteUrl(url), params, content_type, responseHandler);
        }else {
                Utils.hideProgressDialog();
            if (swipeRefreshLayout!=null && swipeRefreshLayout.isRefreshing())
                swipeRefreshLayout.setRefreshing(false);
            AlertDialogHelper.showDialog(mContext,mContext.getString(R.string.please_check_your_internet_connection),null);
        }

    }

    public static void get(Context mContext, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        Log.v(TAG, url);

        User user = Utils.getLoggedInUser(mContext);
        if (Utils.isOnline(mContext)) {
            client.removeAllHeaders();
            client.setBasicAuth(user.getEmail(),user.getApiPlainKey());
            client.get(url, params, responseHandler);
        }else {
            Utils.hideProgressDialog();
        }

    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
