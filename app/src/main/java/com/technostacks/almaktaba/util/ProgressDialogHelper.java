package com.technostacks.almaktaba.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

import com.technostacks.almaktaba.R;

public class ProgressDialogHelper {
    ProgressDialog mProgressDialog;
    private Activity mcontext;

    public ProgressDialogHelper() {

    }

    public ProgressDialogHelper(Activity context) {
        this.mcontext = context;
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage(context.getString(R.string.loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(true);
    }

    public ProgressDialogHelper(Activity context,String message) {
        this.mcontext = context;
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgressNumberFormat(null);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setIndeterminate(false);
    }

    public void setDownloadProgress(){
        mProgressDialog.setMessage(mcontext.getString(R.string.downloading));
        mProgressDialog.setMax(100);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    }

    public void setNormalDialog(){
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(mcontext.getString(R.string.loading));
    }

    public ProgressDialogHelper(Context context, String title, String message) {
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }

    public ProgressDialog getProgressDialog() {
        return mProgressDialog;
    }

    public void setProgressDialog(ProgressDialog mProgressDialog) {
        //make sure the previous dialog is hidden
        hide();
        this.mProgressDialog = mProgressDialog;
    }

    public void show() {
        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    public void create(Context context, String title, String message) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        mProgressDialog = ProgressDialog.show(context, title, message);
    }

    public void hide() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void dismiss() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mcontext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mProgressDialog.dismiss();
                }
            });
            mProgressDialog = null;
        }
    }

    public void destroyProgressObj() {
        mProgressDialog = null;
    }

    public boolean isProgressShowing(){
        return mProgressDialog.isShowing();
    }
}