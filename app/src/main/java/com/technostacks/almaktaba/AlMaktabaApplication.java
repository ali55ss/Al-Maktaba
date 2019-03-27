package com.technostacks.almaktaba;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.MobileAds;
import com.technostacks.almaktaba.util.GlobalData;

import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by techno-110 on 10/3/18.
 */

public class AlMaktabaApplication extends Application {

    public static boolean isDocUploaded = false;
    public static boolean isFirstDownload = true;
    @Override
    public void onCreate() {
        super.onCreate();
        MobileAds.initialize(this, getString(R.string.ad_mob_key));
        Fabric.with(this, new Crashlytics());

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Montserrat_Regular.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        GlobalData.getInstance();
    }
}
