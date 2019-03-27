package com.technostacks.almaktaba.dialog;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.technostacks.almaktaba.R;
import com.technostacks.almaktaba.listener.RecyclerItemClick;

/**
 * Created by techno-110 on 2/8/17.
 */
public class DocumentSelectionDialog implements View.OnClickListener {

    static BottomSheetDialog builder;
    Context context;
    RecyclerItemClick listener;

    public DocumentSelectionDialog(Context context) {
        this.context = context;
    }

    public void ShowRideSelectionDialog(RecyclerItemClick listener) {

        this.listener = listener;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.dialog_document_selection, null);

        builder = new BottomSheetDialog(context,R.style.AppTheme_Translucent);
        builder.setContentView(view);

        view.findViewById(R.id.tv_scan_document).setOnClickListener(this);
        view.findViewById(R.id.tv_videos).setOnClickListener(this);
        view.findViewById(R.id.tv_photos).setOnClickListener(this);
        view.findViewById(R.id.tv_Browse_doc).setOnClickListener(this);
        view.findViewById(R.id.btn_cancel).setOnClickListener(this);

        if (!((Activity)context).isFinishing() && !builder.isShowing())
            builder.show();

    }

    public static boolean isDialogShowing(){

        if (builder!=null){
            if (builder.isShowing())
                return true;
            else
                return false;
        }else
            return false;

    }

    @Override
    public void onClick(View view) {
        builder.dismiss();
        switch (view.getId()){
            case R.id.tv_scan_document:
                listener.onRecyclerItemClick(1);
                break;
            case R.id.tv_videos:
                listener.onRecyclerItemClick(2);
                break;
            case R.id.tv_photos:
                listener.onRecyclerItemClick(3);
                break;
            case R.id.tv_Browse_doc:
                listener.onRecyclerItemClick(4);
                break;
            case R.id.btn_cancel:
                break;
        }
    }
}
