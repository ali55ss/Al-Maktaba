package com.technostacks.almaktaba.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.github.barteksc.pdfviewer.util.FileUtils;
import com.technostacks.almaktaba.R;
import com.technostacks.almaktaba.util.Const;
import com.technostacks.almaktaba.util.SharedPrefsUtils;

import java.io.File;
import java.util.Locale;

public class SplashActivity extends AppCompatActivity {

    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        SharedPrefsUtils.setStringPreference(this,Const.CURRENT_LANGUAGE, Locale.getDefault().getLanguage()); // setting current devicee language to handle menu in rtl
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (SharedPrefsUtils.getBooleanPreference(mContext,Const.IS_LOGIN,false))
                    intent = new Intent(mContext,MainActivity.class);
                else
                    intent = new Intent(mContext,LoginActivity.class);

                startActivity(intent);
                finish();
            }
        },3000);

        File file = new File(Const.TEMP_FOLDER_PATH,Const.DIRECTORY_NAME);
        if (file.exists()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) )
                deleteRecursive(file);
        }
    }

    void deleteRecursive(File fileOrDirectory) {


        if (fileOrDirectory.isDirectory())
            if (fileOrDirectory.listFiles()!=null){
                for (File child : fileOrDirectory.listFiles())
                    deleteRecursive(child);
            }

        fileOrDirectory.delete();
        }
}
