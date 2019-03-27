package com.technostacks.almaktaba.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;

import com.technostacks.almaktaba.R;
import com.technostacks.almaktaba.listener.RecyclerItemClick;


public class AlertDialogHelper {

    public static void showDialog(Context mContext, String message, final RecyclerItemClick listener){

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mContext.getString(R.string.app_name))
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (listener!=null)
                            listener.onRecyclerItemClick(0);
                    }
                });

        AlertDialog dialog; dialog = builder.create();
        dialog.show();
    }

}
