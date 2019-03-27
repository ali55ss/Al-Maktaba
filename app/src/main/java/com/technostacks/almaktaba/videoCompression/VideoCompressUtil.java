package com.technostacks.almaktaba.videoCompression;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.technostacks.almaktaba.R;

/**
 * Created by techno-110 on 21/3/18.
 */

public class VideoCompressUtil {

    public static final String TAG = VideoCompressUtil.class.getSimpleName();
    private Context mContext;
    private ProgressDialog mProgressDialog;
    private String currentFilePath, outputPath;
    private boolean showProgress = false;
    private boolean isDeleteOriginalFile = false;

    public VideoCompressUtil(Context mContext, String currentFilePath, String outputPath, boolean showProgress, boolean isDeleteOriginalFile) {
        this.mContext = mContext;
        this.currentFilePath = currentFilePath;
        this.showProgress = showProgress;
        this.outputPath = outputPath;
        this.isDeleteOriginalFile = isDeleteOriginalFile;
    }

    public VideoCompressUtil(Context mContext, String currentFilePath, boolean showProgress, boolean isDeleteOriginalFile) {
        this.mContext = mContext;
        this.currentFilePath = currentFilePath;
        this.showProgress = showProgress;
        this.isDeleteOriginalFile = isDeleteOriginalFile;
    }

    public VideoCompressUtil(Context mContext, String currentFilePath) {
        this.mContext = mContext;
        this.currentFilePath = currentFilePath;
    }

    public void compressVideo(final CompressCallback compressCallback) {

        if (showProgress) {
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage(mContext.getString(R.string.please_wait));
            mProgressDialog.show();
        }

        Log.e(TAG, "time before compression = " + System.currentTimeMillis());

        new Thread(new Runnable() {
            @Override
            public void run() {

                if (outputPath == null || outputPath.isEmpty())
                    outputPath = Environment.getExternalStorageDirectory().getPath() + "/Compressed -" + System.currentTimeMillis() + ".mp4";

                final boolean isCompressed = MediaController.getInstance().convertVideo(currentFilePath, outputPath, isDeleteOriginalFile);

                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (mProgressDialog != null && mProgressDialog.isShowing())
                            mProgressDialog.hide();

                        if (isCompressed) {
                            Log.d(TAG, "Compression Successful!");
                            Log.e(TAG, "time after compression = " + System.currentTimeMillis());
                        }
                        compressCallback.onCompressionResult(outputPath,isCompressed);
                    }
                });
            }
        }).start();


       /* new AsyncTask<Void, Void, Boolean>() {
            boolean isDeleteFile = false;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected Boolean doInBackground(Void... voids) {

                if (outputPath==null || outputPath.isEmpty())
                    outputPath = Environment.getExternalStorageDirectory().getPath()+"/Compressed -"+System.currentTimeMillis()+".mp4";

                    return MediaController.getInstance().convertVideo(currentFilePath, outputPath, isDeleteFile);

            }

            @Override
            protected void onPostExecute(Boolean compressed) {
                super.onPostExecute(compressed);

                if (compressed) {
                    Log.d(TAG, "Compression Successful!");
                    Log.e(TAG,"time after compression = "+System.currentTimeMillis());
                }
                if (mProgressDialog!=null && mProgressDialog.isShowing())
                    mProgressDialog.hide();

                if (outputPath!=null && !outputPath.isEmpty())
                    compressCallback.compressCallback(outputPath);
                else
                    compressCallback.compressCallback(outputFilePath);
            }
        }.execute();*/
    }

    public interface CompressCallback {
        void onCompressionResult(String outputFilePath,boolean compressionStatus);
    }
}
