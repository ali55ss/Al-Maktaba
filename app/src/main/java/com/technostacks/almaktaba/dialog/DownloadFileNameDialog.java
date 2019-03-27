package com.technostacks.almaktaba.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.drive.DriveFile;
import com.technostacks.almaktaba.R;
import com.technostacks.almaktaba.listener.DriveFileListener;


/**
 * Created by techno-110 on 2/8/17.
 */
public class DownloadFileNameDialog implements View.OnClickListener {

    AlertDialog dialog;
    Context context;
    CheckBox chkTerms;
    DriveFileListener listener;
    TextInputLayout textInputLayout;
    EditText edDocName;

    public DownloadFileNameDialog(Context context) {
        this.context = context;
    }

    public void showDialog(DriveFileListener listener) {

        this.listener = listener;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_download_file_name,null);

        textInputLayout = view.findViewById(R.id.ti_document_name);
        edDocName = view.findViewById(R.id.ed_document_name);

        view.findViewById(R.id.btn_submit_doc_name).setOnClickListener(this);

        builder.setCancelable(true);
        builder.setView(view);
        dialog = builder.create();
        if (!((Activity)context).isFinishing())
            dialog.show();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.btn_submit_doc_name:
                if (edDocName.getText().toString().trim().isEmpty()){
                    textInputLayout.setError(context.getString(R.string.please_enter_document_name));
                } else{
                    textInputLayout.setError(null);
                    dialog.dismiss();
                    listener.getFileName(edDocName.getText().toString().trim());
                }

                break;

        }
    }



}
