package com.technostacks.almaktaba.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.technostacks.almaktaba.R;
import com.technostacks.almaktaba.util.Const;
import com.technostacks.almaktaba.util.SharedPrefsUtils;

import java.util.Locale;


public abstract class BaseActivity extends AppCompatActivity {


    LinearLayout tvForgotBack;
    ImageView ivMenu;
    TextView toolbarTitle;
    Toolbar toolbar;
    FrameLayout container;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        findViews();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void setDeviceLanguage(){
        SharedPrefsUtils.setStringPreference(this, Const.CURRENT_LANGUAGE, Locale.getDefault().getLanguage());
    }

    private void findViews() {

        tvForgotBack = findViewById(R.id.tv_forgot_back);
        ivMenu = findViewById(R.id.iv_menu_icon);
        toolbarTitle = findViewById(R.id.toolbar_navigation_title);
        toolbar = findViewById(R.id.toolbar);
        container = findViewById(R.id.container);

        setSupportActionBar(toolbar);
        setTitle("");

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mAdView.setVisibility(View.VISIBLE);
            }
        });

    }

    public void setToolbarTitle(String strToolbarTitle) {

        if (toolbar != null) {

            if (!TextUtils.isEmpty(strToolbarTitle)) {

                toolbarTitle.setText(strToolbarTitle);


            } else {

                toolbarTitle.setText(R.string.app_name);
            }
        }

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

}
