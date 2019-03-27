package com.technostacks.almaktaba.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.technostacks.almaktaba.R;
import com.technostacks.almaktaba.model.BitmapListModel;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by techno-110 on 20/3/17.
 */
public class CustomPagerAdapter extends PagerAdapter {

    private Context context;
    boolean isExistDoc;
    private ArrayList<Bitmap> bitmapArrayList;
    ArrayList<BitmapListModel> bitmapListModel;
    PagerItemClick pagerItemClick;

    public interface PagerItemClick {
        void itemClick();
    }

    public CustomPagerAdapter(Context context, boolean isExistDoc, ArrayList<Bitmap> bitmapArrayList) {
        this.context = context;
        this.isExistDoc = isExistDoc;
        this.bitmapArrayList = bitmapArrayList;
    }

    public CustomPagerAdapter(Context context, boolean isExistDoc, ArrayList<BitmapListModel> bitmapListModel, boolean temp, PagerItemClick pagerItemClick) {
        this.context = context;
        this.isExistDoc = isExistDoc;
        this.bitmapListModel = bitmapListModel;
        this.pagerItemClick = pagerItemClick;
    }

    @Override
    public int getCount() {
        if (isExistDoc)
            return bitmapArrayList.size();
        else
            return bitmapListModel.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.temp_viewpager_single_item, container, false);
        ImageView imageView = (ImageView) layout.findViewById(R.id.iv_viewpager);
    //    RelativeLayout rl_Addnew = (RelativeLayout) layout.findViewById(R.id.rl_Addnew);

        if (isExistDoc)

            imageView.setImageBitmap(bitmapArrayList.get(position));

        else {

            /*if (position == bitmapListModel.size()) {
                rl_Addnew.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
                rl_Addnew.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pagerItemClick.itemClick();
                    }
                });
            } else {
                rl_Addnew.setVisibility(View.GONE);*/
                imageView.setVisibility(View.VISIBLE);
                if (bitmapListModel.get(position).getBitmap() != null)
                    imageView.setImageBitmap(bitmapListModel.get(position).getBitmap());
                else {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), bitmapListModel.get(position).getBitmapUri());

                        if (bitmap.getWidth() > 1280 || bitmap.getHeight() > 1280) {
                            int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
                            bitmap = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
                        }
                        imageView.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
        //        }
            }
        }

        container.addView(layout);

        return layout;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}
