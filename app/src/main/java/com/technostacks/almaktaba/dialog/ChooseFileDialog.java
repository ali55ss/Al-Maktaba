package com.technostacks.almaktaba.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.technostacks.almaktaba.R;
import com.technostacks.almaktaba.listener.RecyclerItemClick;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ChooseFileDialog implements View.OnClickListener {

    AlertDialog dialog;
    TextView tvDialogTitle,tvCamera,tvGallery,tvCancel;
    private Context context;
    RecyclerItemClick listener;
    private boolean isProfile;

    public ChooseFileDialog(Context context) {
        this.context = context;

    }

    public void createChooseFileDialog(boolean isProfile,final RecyclerItemClick listener) {
        this.listener = listener;
        this.isProfile = isProfile;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_choose_file, null);

        TextView tvTitle = view.findViewById(R.id.tv_dialog_title);
        view.findViewById(R.id.tv_camera).setOnClickListener(this);
        view.findViewById(R.id.tv_gallery).setOnClickListener(this);
        view.findViewById(R.id.tv_cancel).setOnClickListener(this);

        if (isProfile)
            tvTitle.setText(context.getString(R.string.choose_user_profile_image_from));
        else
            tvTitle.setText(context.getString(R.string.choose_university_logo_from));

        builder.setCancelable(true);
        builder.setView(view);
        dialog = builder.create();
        dialog.show();
    }


    @Override
    public void onClick(View view) {
        dialog.dismiss();
        switch (view.getId()) {
            case R.id.tv_camera:
                listener.onRecyclerItemClick(0);
                break;
            case R.id.tv_gallery:
                listener.onRecyclerItemClick(1);
                break;
        }
    }
}
