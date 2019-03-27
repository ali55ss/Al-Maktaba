package com.technostacks.almaktaba.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.technostacks.almaktaba.QuickAction.ActionItemText;
import com.technostacks.almaktaba.R;

import java.util.ArrayList;
import java.util.List;

public class AddDoc_QuickAction_Adapter extends BaseAdapter {

    private final int[] ICONS = new int[]{

            R.drawable.ic_filter_no_filter,
            R.drawable.ic_filter_black_white,
            R.drawable.ic_filter_gray,
            R.drawable.ic_filter_magic_color,
    };

    

    private LayoutInflater mLayoutInflater;
    private List<ActionItemText> mItems;

    public AddDoc_QuickAction_Adapter(Context context) {
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mItems = new ArrayList<ActionItemText>();
        int total = (int) (4);

        final String[] TITLES = new String[]{
                context.getString(R.string.no_filter), context.getString(R.string.black_and_white), context.getString(R.string.gray), context.getString(R.string.magic_color)};
        
        for (int i = 0; i < total; i++) {
            ActionItemText item = new ActionItemText(context, TITLES[i], ICONS[i]);
            mItems.add(item);
        }
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int arg0) {
        return mItems.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View arg1, ViewGroup arg2) {
        View view = mLayoutInflater.inflate(R.layout.action_item, arg2, false);

        ActionItemText item = (ActionItemText) getItem(position);

        ImageView image = (ImageView) view.findViewById(R.id.image);
        TextView text = (TextView) view.findViewById(R.id.title);

        image.setImageDrawable(item.getIcon());
        text.setText(item.getTitle());

        return view;
    }

}