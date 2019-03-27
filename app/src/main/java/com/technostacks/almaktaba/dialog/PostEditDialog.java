package com.technostacks.almaktaba.dialog;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.BottomSheetDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.technostacks.almaktaba.R;
import com.technostacks.almaktaba.listener.RecyclerItemClick;

/**
 * Created by techno-110 on 2/8/17.
 */
public class PostEditDialog implements View.OnClickListener {

    private static BottomSheetDialog builder;
    private Context context;
    private RecyclerItemClick listener;
    private boolean showDelete;

    public PostEditDialog(Context context,boolean showDelete) {
        this.context = context;
        this.showDelete = showDelete;
    }

    public void showPostEditDialog(RecyclerItemClick listener) {

        this.listener = listener;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.dialog_edit_post, null);

        builder = new BottomSheetDialog(context,R.style.AppTheme_Translucent);
        builder.setContentView(view);

        TextView tvPostDelete = builder.findViewById(R.id.tv_post_delete);
        tvPostDelete.setOnClickListener(this);

        if (showDelete)
            tvPostDelete.setVisibility(View.VISIBLE);
        else tvPostDelete.setVisibility(View.GONE);

        view.findViewById(R.id.tv_post_share).setOnClickListener(this);
        view.findViewById(R.id.btn_cancel_post).setOnClickListener(this);

        if (!((Activity)context).isFinishing() && !builder.isShowing())
            builder.show();

    }

    @Override
    public void onClick(View view) {
        builder.dismiss();
        switch (view.getId()){
            case R.id.tv_post_share:
                listener.onRecyclerItemClick(2);
                break;
            case R.id.tv_post_delete:
                listener.onRecyclerItemClick(1);
                break;
            case R.id.btn_cancel_post:
                break;
        }
    }
}
