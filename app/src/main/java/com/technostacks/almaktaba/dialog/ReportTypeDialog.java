package com.technostacks.almaktaba.dialog;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.BottomSheetDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.technostacks.almaktaba.R;
import com.technostacks.almaktaba.activity.customview.WheelReportTypePicker;
import com.technostacks.almaktaba.listener.RecyclerItemClick;

import java.util.ArrayList;

/**
 * Created by techno-110 on 2/8/17.
 */
public class ReportTypeDialog implements View.OnClickListener {

    private static BottomSheetDialog builder;
    private Context context;
    private RecyclerItemClick listener;
    private ArrayList<String> listReportTypes;
    WheelReportTypePicker picker;

    public ReportTypeDialog(Context context,ArrayList<String> listReportTypes) {
        this.context = context;
        this.listReportTypes = listReportTypes;
    }

    public void shoeReportTypeDialog(RecyclerItemClick listener) {

        this.listener = listener;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.dialog_report_type, null);

        builder = new BottomSheetDialog(context,R.style.AppTheme_Translucent);
        builder.setContentView(view);

        picker = builder.findViewById(R.id.wheel_report_type);
        picker.initAdapter(listReportTypes);

        view.findViewById(R.id.tv_cancel_report).setOnClickListener(this);
        view.findViewById(R.id.tv_done_report).setOnClickListener(this);

        if (!((Activity)context).isFinishing() && !builder.isShowing())
            builder.show();

    }

    @Override
    public void onClick(View view) {
        builder.dismiss();
        switch (view.getId()){
            case R.id.tv_cancel_report:
                break;
            case R.id.tv_done_report:
                listener.onRecyclerItemClick(picker.getCurrentItemPosition());
                break;
        }
    }
}
