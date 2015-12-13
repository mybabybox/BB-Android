package com.babybox.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.babybox.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;

public class ColorSpinnerAdapter extends BaseAdapter {
    private List<Integer> colors;
    private Context context;

    public ColorSpinnerAdapter(Context context) {
        this.context = context;
        colors = new ArrayList<>();
        colors.addAll(ViewUtil.getHighlightColorValues());
    }

    public int getPosition(Integer item) {
        return colors.indexOf(item);
    }

    @Override
    public int getCount() {
        return colors.size();
    }

    @Override
    public Integer getItem(int arg0) {
        return colors.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int pos, View view, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(android.R.layout.simple_spinner_item, null);

        int color = colors.get(pos);
        TextView text1 = (TextView) view.findViewById(android.R.id.text1);
        text1.setText("");
        text1.setTextColor(color);
        text1.setBackgroundColor(color);
        text1.setTextSize(20f);
        return view;
    }
}