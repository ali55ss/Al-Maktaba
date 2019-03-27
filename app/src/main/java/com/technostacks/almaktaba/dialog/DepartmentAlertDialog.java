package com.technostacks.almaktaba.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.technostacks.almaktaba.R;
import com.technostacks.almaktaba.activity.MainActivity;
import com.technostacks.almaktaba.listener.RecyclerItemClick;


public class DepartmentAlertDialog implements View.OnClickListener {

    AlertDialog dialog;
    TextView tvDialogTitle,tvCamera,tvGallery,tvCancel;
    private Context context;
    RecyclerItemClick listener;
    TextView tvAlert;

    public DepartmentAlertDialog(Context context) {
        this.context = context;

    }

    public void createDepartmentChooserDialog(String message,String positiveBtn,String negativeBtn , final RecyclerItemClick listener) {
        this.listener = listener;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_department_alert, null);

        tvAlert = view.findViewById(R.id.tv_alert_message);
        tvAlert.setText(message);
        Button btnNegative =  view.findViewById(R.id.btn_no);
        Button btnPositive = view.findViewById(R.id.btn_yes);

        if (!positiveBtn.isEmpty() && !negativeBtn.isEmpty()){
            btnPositive.setText(positiveBtn);
            btnNegative.setText(negativeBtn);
        }

        btnNegative.setOnClickListener(this);
        btnPositive.setOnClickListener(this);
        builder.setCancelable(true);
        builder.setView(view);
        dialog = builder.create();
        dialog.setCancelable(true);
        dialog.show();
    }


    @Override
    public void onClick(View view) {
        dialog.dismiss();
        switch (view.getId()) {
            case R.id.btn_no:
                listener.onRecyclerItemClick(0);
                break;
            case R.id.btn_yes:
                listener.onRecyclerItemClick(1);
                break;
        }
    }

}
