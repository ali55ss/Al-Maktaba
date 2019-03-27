package com.technostacks.almaktaba.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.technostacks.almaktaba.activity.SplashActivity;
import com.technostacks.almaktaba.util.Const;
import com.technostacks.almaktaba.util.SharedPrefsUtils;

import java.util.Locale;

public class LocaleReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving

        Log.e("LocaleReceiver","language changed !");
        SharedPrefsUtils.setStringPreference(context, Const.CURRENT_LANGUAGE, Locale.getDefault().getLanguage()); // setting current devicee language to handle menu in rtl

        Intent i = new Intent(context, SplashActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
        context.startActivity(i);
    }
}
