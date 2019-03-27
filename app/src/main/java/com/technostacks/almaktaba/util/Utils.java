package com.technostacks.almaktaba.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.google.gson.Gson;
import com.technostacks.almaktaba.R;
import com.technostacks.almaktaba.activity.LoginActivity;
import com.technostacks.almaktaba.dialog.AlertDialogHelper;
import com.technostacks.almaktaba.listener.DriveFileListener;
import com.technostacks.almaktaba.listener.RecyclerItemClick;
import com.technostacks.almaktaba.model.LoginResponseModel;
import com.technostacks.almaktaba.model.User;
import com.technostacks.almaktaba.model.Userdepartment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by techno-110 on 14/3/18.
 */

public class Utils {

    public static final String TAG = Utils.class.getSimpleName();
    private static Gson gson;
    private static User user;
    private static Userdepartment userdepartment;
    static ProgressDialog pDialog;
    static Dialog progressDialog;

    public static Gson getGson() {
        if (gson == null)
            gson = new Gson();
        return gson;
    }

    public static Userdepartment getUserdepartment(Context mContext) {

        if (userdepartment == null) {
            if (SharedPrefsUtils.getStringPreference(mContext, Const.DEPARTMENT_RESPONSE) != null) {
                userdepartment = getGson().fromJson(SharedPrefsUtils.getStringPreference(mContext, Const.DEPARTMENT_RESPONSE), Userdepartment.class);
            }
        }
        return userdepartment;

    }

    public static User getLoggedInUser(Context mContext) {

        if (user == null) {
            if (SharedPrefsUtils.getStringPreference(mContext, Const.LOGIN_RESPONSE) != null) {
                LoginResponseModel loginResponseModel = getGson().fromJson(SharedPrefsUtils.getStringPreference(mContext, Const.LOGIN_RESPONSE), LoginResponseModel.class);
                user = loginResponseModel.getUsers().get(0);
            }
        }
        return user;
    }

    public static void printHashKey(Context pContext) {
        try {
            PackageInfo info = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i(TAG, "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "printHashKey()", e);
        } catch (Exception e) {
            Log.e(TAG, "printHashKey()", e);
        }
    }

    public static void setLoggedInUser(User newUser) {
        user = newUser;
    }

    public static void clearLoggedInPreferences() {
        user = null;
        userdepartment = null;
    }

    public static void saveNewUserDepartmentDataToPreference(Context mContext, ArrayList<Userdepartment> userdepartment) {

        if (SharedPrefsUtils.getStringPreference(mContext, Const.LOGIN_RESPONSE) != null) {
            LoginResponseModel loginResponseModel = getGson().fromJson(SharedPrefsUtils.getStringPreference(mContext, Const.LOGIN_RESPONSE), LoginResponseModel.class);
            loginResponseModel.getUsers().get(0).setUserdepartments(userdepartment);

            Gson gson = Utils.getGson();
            String jsonString = gson.toJson(loginResponseModel);
            Log.e("Util", "json string = " + jsonString);
            SharedPrefsUtils.setStringPreference(mContext, Const.LOGIN_RESPONSE, jsonString);
        }

    }

    public static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static void hidekeyBoard(Activity mContext) {
        View view = mContext.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null)
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static Bitmap compressImage(Bitmap bitmap) {
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int targetWidth = 1024; // your arbitrary fixed limit
        int targetHeight = (int) (originalHeight * targetWidth / (double) originalWidth); // casts to avoid truncating
        if (originalHeight < originalWidth) {
            bitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true);
        } else {
            bitmap = Bitmap.createScaledBitmap(bitmap, targetHeight, targetWidth, true);
        }
        return bitmap;
    }

    public static File createFile(Context mContext, Bitmap bmp, String fileName) {
        File file = null;
        try {
            //create first file to write bitmap data

            file = new File(mContext.getCacheDir(), fileName + ".jpg");
            file.createNewFile();
            bmp = Utils.compressImage(bmp);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    @SuppressLint("StaticFieldLeak")
    public static void downloadGoogleDriveFile(Context mContext, String asd, final String Filename, final String accountName, final String token, DriveFileListener listener) {
        final String fileurl = asd;

        new AsyncTask<Void, Integer, String>() {

            @Override
            protected String doInBackground(Void... params) {
                /*GoogleAccountCredential mCredential;

                mCredential = GoogleAccountCredential.usingOAuth2(mContext, Arrays.asList(SCOPES)).setBackOff(new ExponentialBackOff());
                Log.e(TAG, " accountName:-- " + accountName);
                Log.e(TAG, " token :-- " + token);
                mCredential.setSelectedAccountName(accountName);*/
                try {

                    URL url = new URL(fileurl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Authorization", "Bearer " + token);
                    conn.setDoInput(true);
                    conn.connect();

                    File f1 = new File(Environment.getExternalStorageDirectory(), Const.DIRECTORY_NAME);

                    if (!f1.exists()) {
                        f1.mkdir();
                    }

                    File file1 = new File(f1.getPath(), Filename);
                    Log.e(TAG, "full file path = " + file1.getAbsolutePath());
                    try {
                        file1.createNewFile();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                    FileOutputStream fileOutput = new FileOutputStream(file1);
                    InputStream inputStream = conn.getInputStream();

                    byte[] buffer = new byte[1024];
                    int bufferLength = 0;
                    int lenghtOfFile = conn.getContentLength();
                    long total = 0;
                    while ((bufferLength = inputStream.read(buffer)) > 0) {

                        total += bufferLength;

                        fileOutput.write(buffer, 0, bufferLength);

                        publishProgress((int) ((total * 100) / lenghtOfFile));

                    }

                    fileOutput.close();
                    return file1.getName();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                pDialog = new ProgressDialog(mContext);
                pDialog.setMessage(mContext.getString(R.string.preparing_preview));
                pDialog.setIndeterminate(true);
                pDialog.show();
                pDialog.setCancelable(false);
            }

            /**
             * Updating progress bar
             */
            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);

            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            protected void onPostExecute(final String path) {
                super.onPostExecute(path);

                Log.e(TAG, "file path = " + path);
                if (pDialog != null) {
                    pDialog.hide();
                    pDialog = null;
                }
                listener.getFileName(path);
            }
        }.execute();
    }


    public static String getFileNameOnly(String name) {
        if (name.indexOf(".") > 0)
            return name.substring(0, name.lastIndexOf("."));
        return getDate();
    }

    public static Uri getUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    public static Bitmap getBitmap(Context context, Uri uri) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        return bitmap;
    }

    public static String convertUtcToLocalTime(Context mContext, String utcDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date utcTime = null;
        String localTime = "";
        try {
            utcTime = format.parse(utcDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeZone = Calendar.getInstance().getTimeZone().getID();
        if (utcTime != null) {
            Date local = new Date(utcTime.getTime() + TimeZone.getTimeZone(timeZone).getOffset(utcTime.getTime()));

            localTime = handleDateFormats(mContext, local.getTime());

        }
        return localTime;
    }

    private static String handleDateFormats(Context mContext, long millis) {

        Calendar postTime = Calendar.getInstance();
        postTime.setTimeInMillis(millis);

        Calendar now = Calendar.getInstance();

        final String timeFormatString = "h:mm aa";
        final String weekTimeFormatString = "EEEE 'at' h:mm aa";
        final String dateTimeFormatString = "MMM d 'at' h:mm aa";

        if (now.get(Calendar.DATE) == postTime.get(Calendar.DATE)) {
            return "Today at " + DateFormat.format(timeFormatString, postTime);
        } else if (now.get(Calendar.DATE) - postTime.get(Calendar.DATE) == 1) {
            return mContext.getString(R.string.yesterday_at) + DateFormat.format(timeFormatString, postTime);
        } else if (now.get(Calendar.WEEK_OF_MONTH) == postTime.get(Calendar.WEEK_OF_MONTH)) {
            return DateFormat.format(weekTimeFormatString, postTime).toString();
        } else {
            return DateFormat.format(dateTimeFormatString, postTime).toString();
        }
    }

    public static String getDate() {

        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yy HH:mm:ss");
        return "Doc " + df.format(c.getTime());
    }

    public static String getDocName() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-dd-MMM_HH-mm-ss");
        String asd = "Doc_";
        String ext = ".pdf";
        try {
            asd = asd + simpleDateFormat.format(System.currentTimeMillis()) + ext;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return asd;
    }

    public static long getFileSize(File file) {
        long filesize = file.length() / 1024;
        long fileSizeInMB = filesize / 1024;
        Log.e(TAG, "filesize = " + fileSizeInMB);
        return fileSizeInMB;
    }

    public static void handleResponseErrors(Context mContext, int reqCode) {
        Log.e(TAG, "reqcode = " + reqCode);
        switch (reqCode) {
            case 403:
                AlertDialogHelper.showDialog(mContext, mContext.getString(R.string.unable_to_reach_to_server), null);
                break;
            case 408:
                AlertDialogHelper.showDialog(mContext, mContext.getString(R.string.request_time_out), null);
                break;
            case 500:
                AlertDialogHelper.showDialog(mContext, mContext.getString(R.string.request_time_out), null);
                break;

        }
    }

    public static void showProgressDialog(Context context, String message) {

        if (context != null && !((Activity) context).isFinishing()) {
            if (progressDialog == null || !progressDialog.isShowing()) {
                progressDialog = new Dialog(context);
                progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                try {
                    int dividerId = progressDialog.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
                    View divider = progressDialog.findViewById(dividerId);
                    if (divider != null)
                        divider.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    TextView mTitle = (TextView) progressDialog.findViewById(android.R.id.title);
                    if (mTitle != null)
                        mTitle.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                    int x = Resources.getSystem().getIdentifier("titleDivider", "id", "android");
                    View titleDivider = progressDialog.findViewById(x);
                    if (titleDivider != null)
                        titleDivider.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));

                } catch (Exception e) {
                    e.printStackTrace();
                }
                //   progressDialog.setContentView(R.layout.custom_progressbar);
                progressDialog.setContentView(R.layout.custom_progress_dialog);
            /*    TextView tvMessage = (TextView) progressDialog.findViewById(R.id.txtMessage);
                if (!message.equals("")) {
                    tvMessage.setText(message);
                }*/
                progressDialog.setCancelable(false);
                if (!((Activity) context).isFinishing())
                    progressDialog.show();
            }
        } else {
            Log.e(TAG, context.toString() + " Context Null");

        }

    }

    public static void hideProgressDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Throwable throwable) {

        } finally {
            progressDialog = null;
        }
    }
}
