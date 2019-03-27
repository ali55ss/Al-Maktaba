package com.technostacks.almaktaba.activity.customview;

import android.content.Context;
import android.util.AttributeSet;

import com.technostacks.almaktaba.listener.RecyclerItemClick;

import java.util.List;

public class WheelReportTypePicker extends WheelPicker {


    private Adapter adapter;

    private int lastScrollPosition;

    private RecyclerItemClick recycleItemClickListener;

    public WheelReportTypePicker(Context context) {
        this(context, null);
    }

    public WheelReportTypePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void initAdapter(List<String> listData) {
        /*final List<String> values = new ArrayList<>();
        Resources resources = getResources();
        values.add("Driver denied service");
        values.add("Ride is late");
        values.add("Changed my mind");*/
        adapter = new Adapter(listData);
        setAdapter(adapter);
    }


    public void setonwheelSelectedListener(RecyclerItemClick recycleItemClickListener) {
        this.recycleItemClickListener = recycleItemClickListener;
    }

    @Override
    protected void onItemSelected(int position, Object item) {
        if (recycleItemClickListener != null) {
            recycleItemClickListener.onRecyclerItemClick(position);
        }
    }

    @Override
    protected void onItemCurrentScroll(int position, Object item) {
        if (lastScrollPosition != position) {
            lastScrollPosition = position;
        }
    }

    @Override
    protected String getFormattedValue(Object value) {
        return String.valueOf(value);
    }

    @Override
    public int getDefaultItemPosition() {
        return 1;
    }

}