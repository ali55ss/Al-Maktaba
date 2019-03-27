package com.darsh.multipleimageselect.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.darsh.multipleimageselect.R;
import com.darsh.multipleimageselect.models.Image;

import java.util.ArrayList;

/**
 * Created by Darshan on 4/18/2015.
 */
public class CustomImageSelectAdapter extends CustomGenericAdapter<Image> {
    public CustomImageSelectAdapter(Context context, ArrayList<Image> images) {
        super(context, images);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.grid_view_item_image_select, null);

            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image_view_image_select);
            viewHolder.IVcheck = (ImageView) convertView.findViewById(R.id.IVcheck);
            viewHolder.view = convertView.findViewById(R.id.view_alpha);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.imageView.getLayoutParams().width = size;
        viewHolder.imageView.getLayoutParams().height = size;

        viewHolder.view.getLayoutParams().width = size;
        viewHolder.view.getLayoutParams().height = size;

        if (arrayList.get(position).isSelected) {
            viewHolder.view.setAlpha(0.4f);
            viewHolder.IVcheck.setImageResource(R.drawable.ic_selected_image);

        } else {
            viewHolder.view.setAlpha(0.0f);
            viewHolder.IVcheck.setImageResource(R.drawable.ic_non_select_image);
        }

        Glide.with(context)
                .load(arrayList.get(position).path)
                .placeholder(R.drawable.image_placeholder).into(viewHolder.imageView);

        return convertView;
    }

    private static class ViewHolder {
        public ImageView imageView,IVcheck;
        public View view;
    }
}
