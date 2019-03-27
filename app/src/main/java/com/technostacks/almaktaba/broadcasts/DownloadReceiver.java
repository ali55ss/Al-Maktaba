package com.technostacks.almaktaba.broadcasts;

import android.app.DownloadManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.technostacks.almaktaba.R;
import com.technostacks.almaktaba.util.GlobalData;
import java.util.ArrayList;

public class DownloadReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

// remove it from our list
        ArrayList<Long> downloadIdList = GlobalData.getDownloadIdList();

        if(GlobalData.downloadIdList!=null){

            if (downloadIdList.contains(referenceId))
                GlobalData.downloadIdList.remove(referenceId);

// if list is empty means all downloads completed
            if (GlobalData.downloadIdList.isEmpty())
            {

// show a notification
                Log.e("INSIDE", "" + referenceId);
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.mipmap.ic_app_icon)
                                .setContentTitle(context.getString(R.string.app_name))
                                .setAutoCancel(true)
                                .setContentText(context.getString(R.string.all_downloads_completed));


                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(455, mBuilder.build());


            }
        }
    }

}
